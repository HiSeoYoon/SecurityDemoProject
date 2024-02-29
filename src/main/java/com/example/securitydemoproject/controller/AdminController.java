package com.example.securitydemoproject.controller;

import com.example.securitydemoproject.dto.UpdateUserRequest;
import com.example.securitydemoproject.service.AdminService;
import com.example.securitydemoproject.util.LoggerUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/admin")
@Api(tags = "Admin Controller", description = "APIs for managing admin users")
public class AdminController {
    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @ApiOperation(value = "Get all users", response = List.class)
    @GetMapping("")
    public ResponseEntity<List<Map<String, Object>>> getUsers() {
        String requestId = MDC.get("requestId");
        LoggerUtil.requestLogInfo(AdminController.class, requestId, "Request received to get all users.");
        List<Map<String, Object>> members = adminService.getUsers();
        LoggerUtil.requestLogInfo(AdminController.class, requestId, "Returning all users.");
        return ResponseEntity.ok(members);
    }

    @ApiOperation(value = "Get a user by ID", response = Map.class)
    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable int userId) {
        String requestId = MDC.get("requestId");
        LoggerUtil.requestLogInfo(AdminController.class, requestId, "Request received to get user by ID: "+ userId);
        try {
            Map<String, Object> response = adminService.getUser(userId);
            LoggerUtil.requestLogInfo(AdminController.class, requestId, "Returning user with ID: " + userId);
            return ResponseEntity.ok(response);
        } catch (UsernameNotFoundException e) {
            LoggerUtil.requestLogError(AdminController.class, requestId, "User not found :", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createErrorResponse(e));
        }
    }

    @ApiOperation(value = "Update a user by ID", response = Map.class)
    @PutMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> updateUser(
            @PathVariable int userId,
            @RequestBody UpdateUserRequest updateUserRequest) {
        String requestId = MDC.get("requestId");
        LoggerUtil.requestLogInfo(AdminController.class, requestId, "Request received to update user with ID: " + userId);
        try {
            updateUserRequest.validateRole();
            Map<String, Object> updatedUser = adminService.updateUser(userId, updateUserRequest);
            LoggerUtil.requestLogInfo(AdminController.class, requestId, "User with ID " + userId + " updated successfully.");
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            LoggerUtil.requestLogError(AdminController.class, requestId, "Invalid role specified: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse(e));
        } catch (UsernameNotFoundException e) {
            LoggerUtil.requestLogError(AdminController.class, requestId, "User not found :", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createErrorResponse(e));
        }
    }

    private Map<String, Object> createErrorResponse(Exception e) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", e.getMessage());
        return errorResponse;
    }
}
