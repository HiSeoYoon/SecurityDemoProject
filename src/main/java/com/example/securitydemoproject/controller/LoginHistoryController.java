package com.example.securitydemoproject.controller;

import com.example.securitydemoproject.model.LoginHistory;
import com.example.securitydemoproject.security.JwtProvider;
import com.example.securitydemoproject.service.LoginHistoryService;
import com.example.securitydemoproject.util.LoggerUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/login-history")
@Api(tags = "Login History Controller", description = "APIs for managing user login history")
public class LoginHistoryController {
    private final LoginHistoryService loginHistoryService;
    private final JwtProvider jwtProvider;

    @Autowired
    public LoginHistoryController(LoginHistoryService loginHistoryService, JwtProvider jwtProvider) {
        this.loginHistoryService = loginHistoryService;
        this.jwtProvider = jwtProvider;
    }

    @GetMapping("")
    @ApiOperation(value = "Get login history for a user within a time range")
    public ResponseEntity<?> getLoginHistory(
            HttpServletRequest request,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        String username = jwtProvider.getUsernameFromToken(jwtProvider.resolveToken(request));
        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime = endDate.atTime(LocalTime.MAX);
        String requestId = MDC.get("requestId");
        LoggerUtil.requestLogInfo(LoginHistoryController.class, requestId, "Request received to get login history for user '" + username + "' between " + startTime + " and " + endTime);
        try {
            List<LoginHistory> loginHistory = loginHistoryService.getLoginHistoryByUserAndTimeRange(username, startTime, endTime);
            LoggerUtil.requestLogInfo(LoginHistoryController.class, requestId, "Login history retrieved successfully.");
            return ResponseEntity.ok(loginHistory);
        } catch (UsernameNotFoundException e) {
            LoggerUtil.requestLogError(LoginHistoryController.class, requestId, "User not found :", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }
}
