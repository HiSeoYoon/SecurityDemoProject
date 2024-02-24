package com.example.securitydemoproject.config;

import com.example.securitydemoproject.filter.RequestIdFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    @Bean
    public RequestIdFilter requestIdFilter() {
        return new RequestIdFilter();
    }

    @Bean
    public FilterRegistrationBean<RequestIdFilter> loggingFilterRegistration(RequestIdFilter requestIdFilter) {
        FilterRegistrationBean<RequestIdFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(requestIdFilter);
        registrationBean.addUrlPatterns("/*"); // 필터를 적용할 URL 패턴 지정
        registrationBean.setOrder(1); // 필터 순서 지정
        return registrationBean;
    }
}
