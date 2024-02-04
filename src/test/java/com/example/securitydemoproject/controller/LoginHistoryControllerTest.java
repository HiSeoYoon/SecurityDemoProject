package com.example.securitydemoproject.controller;

import com.example.securitydemoproject.model.LoginHistory;
import com.example.securitydemoproject.security.JwtProvider;
import com.example.securitydemoproject.service.LoginHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LoginHistoryControllerTest {
    @Mock
    private LoginHistoryService loginHistoryService;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private LoginHistoryController loginHistoryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getLoginHistory_ValidRequest_ReturnsLoginHistoryList() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);

        // Use lenient mode for unnecessary stubbings
        lenient().when(jwtProvider.resolveToken(any(HttpServletRequest.class))).thenReturn("fakeToken");
        lenient().when(jwtProvider.getUsernameFromToken("fakeToken")).thenReturn("fakeUsername");

        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime = endDate.atTime(LocalTime.MAX);

        // Configure loginHistoryService to return an empty list
        when(loginHistoryService.getLoginHistoryByUserAndTimeRange("fakeUsername", startTime, endTime))
                .thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<LoginHistory>> response = loginHistoryController.getLoginHistory(request, startDate, endDate);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }
}