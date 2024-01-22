package com.example.securitydemoproject.service;

import com.example.securitydemoproject.model.LoginHistory;

import java.time.LocalDateTime;
import java.util.List;

public interface LoginHistoryService {
    void logLoginHistory(String email);

    List<LoginHistory> getLoginHistoryByUserAndTimeRange(String username, LocalDateTime startTime, LocalDateTime endTime);
}
