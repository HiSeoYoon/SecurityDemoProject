package com.example.securitydemoproject.security;

import com.example.securitydemoproject.util.LoggerUtil;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class JwtProvider {

    @Value("${spring.security.jwt.secret}")
    private String secretKey;

    @Value("${spring.security.jwt.expiration}")
    private long validityInMilliseconds;

    private static final double TOKEN_REFRESH_RATIO = 0.5;

    private static final long TOKEN_CLEANUP_INTERVAL = 60 * 60 * 1000; // 1시간(ms)
    private static final long TOKEN_EXPIRATION_THRESHOLD = 2 * 60 * 60 * 1000; // 2시간(ms)

    private final UserDetailsService userDetailsService;

    //Creates a new JWT token
    public String createToken(String username, String role) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("roles", role);

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        LoggerUtil.logInfo(JwtProvider.class, "New JWT token created for user: " + username);

        return token;
    }

    //Refreshes an expired JWT token
    public String refreshExpiredToken(String expiredToken) {
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(expiredToken).getBody();

        Date expiration = claims.getExpiration();
        Date now = new Date();

        if (expiration.getTime() - now.getTime() <= validityInMilliseconds * TOKEN_REFRESH_RATIO) {
            String refreshedToken = createToken(claims.getSubject(), claims.get("roles").toString());
            LoggerUtil.logInfo(JwtProvider.class, "Expired JWT token refreshed for user: " + claims.getSubject());
            return refreshedToken;
        } else {
            return expiredToken;
        }
    }

    //Retrieves authentication information from a JWT token
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUsernameFromToken(token));
        LoggerUtil.logInfo(JwtProvider.class, "Get Authentication Info from request.");
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    //Retrieves the username from a JWT token
    public String getUsernameFromToken(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    //Resolves the JWT token from the request
    public String resolveToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        LoggerUtil.logInfo(JwtProvider.class, "Resolved JWT token from request.");
        return token;
    }

    //Validates the JWT token
    public boolean validateToken(String token) {
        if(isTokenBlacklisted(token)){
            LoggerUtil.logInfo(JwtProvider.class, "JWT token blacklisted: " + token);
            return false;
        }

        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            LoggerUtil.logInfo(JwtProvider.class, "JWT token validated: " + token);
            return !claimsJws.getBody().getExpiration().before((new Date()));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    //Checks if the token has passed the refresh time
    public boolean isTokenPassedRefreshTime(String token){
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();

        Date expiration = claims.getExpiration();
        Date now = new Date();
        if (expiration.getTime() - now.getTime() <= validityInMilliseconds * TOKEN_REFRESH_RATIO) {
            return true;
        } else {
            return false;
        }
    }

    private final Set<String> blacklist = new HashSet<>();

    //Adds a token to the blacklist
    public void addToBlacklist(String token) {
        blacklist.add(token);
        LoggerUtil.logInfo(JwtProvider.class, "Add token to Balcklist: " + token);
    }

    //Checks if a token is blacklisted
    public boolean isTokenBlacklisted(String token) {
        return blacklist.contains(token);
    }

    //Checks if a token has passed the expiration time threshold
    private boolean isTokenPassedExpiredTime(String token){
        try {
            Date expiration = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getExpiration();
            long currentTime = System.currentTimeMillis();
            return expiration.before(new Date(currentTime - TOKEN_EXPIRATION_THRESHOLD));
        } catch (JwtException | IllegalArgumentException e) {
            return true;
        }
    }

    //Scheduled task to clean up expired tokens from the blacklist
    @Scheduled(fixedRate = TOKEN_CLEANUP_INTERVAL)
    public void scheduleTokenCleanup() {
        LoggerUtil.logInfo(JwtProvider.class, "Token cleanup scheduled task running at: " + new Date());
        Iterator<String> iterator = blacklist.iterator();
        while (iterator.hasNext()) {
            String token = iterator.next();
            if (isTokenPassedExpiredTime(token)) {
                iterator.remove();
                LoggerUtil.logInfo(JwtProvider.class, "Expired token removed from the blacklist: " + token);
            }
        }
    }
}
