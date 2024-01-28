package com.example.securitydemoproject.interceptor;

import com.example.securitydemoproject.security.JwtProvider;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class TokenRefreshInterceptor implements HandlerInterceptor {
    private final JwtProvider jwtProvider;

    public TokenRefreshInterceptor(JwtProvider jwtProvider){
        this.jwtProvider = jwtProvider;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = jwtProvider.resolveToken(request);

        if (jwtProvider.validateToken(token) && jwtProvider.isTokenPassedRefreshTime(token)) {
            String refreshedToken = jwtProvider.refreshExpiredToken(token);

            response.setHeader("Authorization", refreshedToken);
        }

        return true;
    }

}
