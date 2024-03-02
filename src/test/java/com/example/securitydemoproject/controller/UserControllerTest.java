package com.example.securitydemoproject.controller;

import com.example.securitydemoproject.dto.ChangePasswordRequest;
import com.example.securitydemoproject.security.JwtProvider;
import com.example.securitydemoproject.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private UserController userController;

    @Test
    void getUser_Success() throws AuthenticationException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        String username = "testUser";
        when(jwtProvider.resolveToken(request)).thenReturn("fakeToken");
        when(jwtProvider.getUsernameFromToken("fakeToken")).thenReturn(username);
        when(userService.getUserByUserName(username)).thenReturn(createUserResponse());

        ResponseEntity<Map<String, Object>> responseEntity = userController.getUser(request);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(createUserResponse(), responseEntity.getBody());
        verify(jwtProvider, times(1)).resolveToken(request);
        verify(jwtProvider, times(1)).getUsernameFromToken("fakeToken");
        verify(userService, times(1)).getUserByUserName(username);
    }

    @Test
    void getUser_EmptyResultException() throws AuthenticationException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(jwtProvider.resolveToken(request)).thenReturn("fakeToken");
        when(jwtProvider.getUsernameFromToken("fakeToken")).thenReturn("testUser");
        doThrow(new EmptyResultDataAccessException(0)).when(userService).getUserByUserName("testUser");

        ResponseEntity<Map<String, Object>> responseEntity = userController.getUser(request);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals(createErrorResponse("가입되지 않은 Id 입니다."), responseEntity.getBody());
        verify(jwtProvider, times(1)).resolveToken(request);
        verify(jwtProvider, times(1)).getUsernameFromToken("fakeToken");
        verify(userService, times(1)).getUserByUserName("testUser");
    }

    @Test
    void changePassword_Success() throws AuthenticationException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        String username = "testUser";
        ChangePasswordRequest newPassword = new ChangePasswordRequest();
        newPassword.setNewPassword("newPassword");
        when(jwtProvider.resolveToken(request)).thenReturn("fakeToken");
        when(jwtProvider.getUsernameFromToken("fakeToken")).thenReturn(username);

        ResponseEntity<String> responseEntity = userController.changePassword(request, newPassword);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Password changed successfully", responseEntity.getBody());
        verify(jwtProvider, times(1)).resolveToken(request);
        verify(jwtProvider, times(1)).getUsernameFromToken("fakeToken");
        verify(userService, times(1)).changePassword(username, "newPassword");
    }

    @Test
    void changePassword_EmptyResultException() throws AuthenticationException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        ChangePasswordRequest newPassword = new ChangePasswordRequest();
        newPassword.setNewPassword("newPassword");
        when(jwtProvider.resolveToken(request)).thenReturn("fakeToken");
        when(jwtProvider.getUsernameFromToken("fakeToken")).thenReturn("testUser");
        doThrow(new EmptyResultDataAccessException(0)).when(userService).changePassword("testUser", "newPassword");

        ResponseEntity<String> responseEntity = userController.changePassword(request, newPassword);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("가입되지 않은 Id 입니다.", responseEntity.getBody());
        verify(jwtProvider, times(1)).resolveToken(request);
        verify(jwtProvider, times(1)).getUsernameFromToken("fakeToken");
        verify(userService, times(1)).changePassword("testUser", "newPassword");
    }

    // Helper method to create a sample user response
    private Map<String, Object> createUserResponse() {
        Map<String, Object> response = new HashMap<>();
        response.put("username", "testUser");
        response.put("email", "test@example.com");
        return response;
    }

    // Helper method to create an error response
    private Map<String, Object> createErrorResponse(String errorMessage) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", errorMessage);
        return response;
    }
}