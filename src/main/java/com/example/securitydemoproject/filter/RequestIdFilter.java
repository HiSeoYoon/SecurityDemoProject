package com.example.securitydemoproject.filter;

import com.example.securitydemoproject.util.LoggerUtil;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

public class RequestIdFilter extends OncePerRequestFilter {
    private static final String REQUEST_ID_HEADER = "X-Request-ID";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String requestId = request.getHeader(REQUEST_ID_HEADER);
            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
                LoggerUtil.requestLogInfo(RequestIdFilter.class, requestId, "No X-Request-ID found in the request header. Generated new request Id");
            } else {
                LoggerUtil.requestLogInfo(RequestIdFilter.class, requestId, "Found X-Request-ID in the request header");
            }
            MDC.put("requestId", requestId);

            filterChain.doFilter(request, response);
        } finally {
            MDC.remove("requestId");
        }
    }
}
