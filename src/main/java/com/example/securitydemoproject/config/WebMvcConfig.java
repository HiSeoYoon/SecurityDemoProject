package com.example.securitydemoproject.config;

import com.example.securitydemoproject.interceptor.RequestResponseLoggingInterceptor;
import com.example.securitydemoproject.interceptor.TokenRefreshInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private RequestResponseLoggingInterceptor requestResponseLoggingInterceptor;
    private final TokenRefreshInterceptor tokenRefreshInterceptor;

    @Autowired
    public WebMvcConfig(TokenRefreshInterceptor tokenRefreshInterceptor) {
        this.tokenRefreshInterceptor = tokenRefreshInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenRefreshInterceptor);
        registry.addInterceptor(requestResponseLoggingInterceptor);
    }
}
