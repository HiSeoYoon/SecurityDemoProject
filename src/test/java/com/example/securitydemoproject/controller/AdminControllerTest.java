package com.example.securitydemoproject.controller;

import com.example.securitydemoproject.dto.UpdateUserRequest;
import com.example.securitydemoproject.service.AdminService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {
    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    @Test
    void getUsers_Success() {
        List<Map<String, Object>> mockUsers = Collections.singletonList(Collections.singletonMap("userId", 1));
        when(adminService.getUsers()).thenReturn(mockUsers);

        ResponseEntity<List<Map<String, Object>>> responseEntity = adminController.getUsers();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockUsers, responseEntity.getBody());
    }

    @Test
    void getUser_Success() {
        int userId = 1;
        Map<String, Object> mockUser = Collections.singletonMap("userId", userId);
        when(adminService.getUser(userId)).thenReturn(mockUser);

        ResponseEntity<Map<String, Object>> responseEntity = adminController.getUser(userId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockUser, responseEntity.getBody());
    }

    @Test
    void updateUser_Success() {
        int userId = 1;
        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setRole("USER");
        Map<String, Object> mockUpdatedUser = Collections.singletonMap("userId", userId);
        when(adminService.updateUser(userId, updateUserRequest)).thenReturn(mockUpdatedUser);

        ResponseEntity<Map<String, Object>> responseEntity = adminController.updateUser(userId, updateUserRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockUpdatedUser, responseEntity.getBody());
    }

    @Test
    void updateUser_InvalidRole() {
        int userId = 1;
        UpdateUserRequest updateUserRequest = new UpdateUserRequest();

        ResponseEntity<Map<String, Object>> responseEntity = adminController.updateUser(userId, updateUserRequest);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(createErrorResponse("Invalid role specified"), responseEntity.getBody());
    }

    private Map<String, Object> createErrorResponse(String errorMessage) {
        return Collections.singletonMap("error", "An error occurred: " + errorMessage);
    }

}