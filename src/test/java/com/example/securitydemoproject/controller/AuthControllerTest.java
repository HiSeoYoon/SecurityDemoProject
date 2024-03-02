package com.example.securitydemoproject.controller;

import com.example.securitydemoproject.dto.JwtRequestDto;
import com.example.securitydemoproject.dto.MemberSignupRequestDto;
import com.example.securitydemoproject.service.AuthService;
import com.example.securitydemoproject.service.LoginHistoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {
    @Mock
    private AuthService authService;

    @Mock
    private LoginHistoryService loginHistoryService;

    @InjectMocks
    private AuthController authController;

    @Test
    void login_Success() {
        JwtRequestDto request = new JwtRequestDto("test@example.com", "password");
        when(authService.login(request)).thenReturn("mockedToken");

        ResponseEntity<String> response = authController.login(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("mockedToken", response.getBody());
        verify(loginHistoryService, times(1)).logLoginHistory(request.getEmail());
    }

    @Test
    void login_EmptyResult() {
        JwtRequestDto request = new JwtRequestDto("nonexistent@example.com", "password");
        when(authService.login(request)).thenThrow(new EmptyResultDataAccessException(0));

        ResponseEntity<String> response = authController.login(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("error: 가입되지 않은 Id 입니다.", response.getBody());
        verify(loginHistoryService, never()).logLoginHistory(any());
    }

    @Test
    void signup_Success() {
        MemberSignupRequestDto request = new MemberSignupRequestDto();
        when(authService.signup(request)).thenReturn("Signup successful");

        ResponseEntity<String> response = authController.signup(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Signup successful", response.getBody());
    }

    @Test
    void signup_UserAlreadyExists() {
        MemberSignupRequestDto request = new MemberSignupRequestDto();
        when(authService.signup(request)).thenThrow(new DuplicateKeyException("User already exists"));

        ResponseEntity<String> response = authController.signup(request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("error: User already exists", response.getBody());
    }

    @Test
    void logout_Success() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(authService.extractTokenFromRequest(request)).thenReturn("validToken");

        ResponseEntity<String> response = authController.logout(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Logout successful", response.getBody());
    }
}