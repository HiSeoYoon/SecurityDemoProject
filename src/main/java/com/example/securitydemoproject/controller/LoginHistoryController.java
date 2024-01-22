package com.example.securitydemoproject.controller;

import com.example.securitydemoproject.model.LoginHistory;
import com.example.securitydemoproject.security.JwtProvider;
import com.example.securitydemoproject.service.LoginHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/login-history")
public class LoginHistoryController {
    private final LoginHistoryService loginHistoryService;
    private final JwtProvider jwtProvider;

    @Autowired
    public LoginHistoryController(LoginHistoryService loginHistoryService, JwtProvider jwtProvider) {
        this.loginHistoryService = loginHistoryService;
        this.jwtProvider = jwtProvider;
    }

    @GetMapping("")
    public ResponseEntity<List<LoginHistory>> getLoginHistory(
            HttpServletRequest request,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        try {
            String username = jwtProvider.getUsernameFromToken(jwtProvider.resolveToken(request));
            LocalDateTime startTime = startDate.atStartOfDay();
            LocalDateTime endTime = endDate.atTime(LocalTime.MAX);
            List<LoginHistory> loginHistory = loginHistoryService.getLoginHistoryByUserAndTimeRange(username, startTime, endTime);

            return ResponseEntity.ok(loginHistory);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
}
