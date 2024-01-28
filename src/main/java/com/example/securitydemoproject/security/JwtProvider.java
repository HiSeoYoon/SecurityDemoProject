package com.example.securitydemoproject.security;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class JwtProvider {
    @Value("${spring.security.jwt.secret}")
    private String secretKey;

    @Value("${spring.security.jwt.expiration}")
    private long validityInMilliseconds;

    private static final double TOKEN_REFRESH_RATIO = 0.5;

    private final UserDetailsService userDetailsService;

    public String createToken(String username, String role) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("roles", role);

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String refreshExpiredToken(String expiredToken) {
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(expiredToken).getBody();

        Date expiration = claims.getExpiration();
        Date now = new Date();

        if (expiration.getTime() - now.getTime() <= validityInMilliseconds * TOKEN_REFRESH_RATIO) {
            return createToken(claims.getSubject(), claims.get("roles").toString());
        } else {
            return expiredToken;
        }
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUsernameFromToken(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

    public boolean validateToken(String token) {
        if(isTokenBlacklisted(token)){
            return false;
        }

        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return !claimsJws.getBody().getExpiration().before((new Date()));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

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

    public void addToBlacklist(String token) {
        blacklist.add(token);
    }

    public boolean isTokenBlacklisted(String token) {
        return blacklist.contains(token);
    }
}
