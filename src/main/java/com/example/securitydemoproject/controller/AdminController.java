package com.example.securitydemoproject.controller;

import com.example.securitydemoproject.dto.UpdateUserRequest;
import com.example.securitydemoproject.security.JwtProvider;
import com.example.securitydemoproject.service.AdminService;
import com.example.securitydemoproject.util.LoggerUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
        LoggerUtil.logInfo(AdminController.class, "Request received to get all users.");
        List<Map<String, Object>> members = adminService.getUsers();
        LoggerUtil.logInfo(AdminController.class, "Returning all users.");
        return ResponseEntity.ok(members);
    }

    @ApiOperation(value = "Get a user by ID", response = Map.class)
    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable int userId) {
        LoggerUtil.logInfo(AdminController.class, "Request received to get user by ID: "+ userId);
        Map<String, Object> response = adminService.getUser(userId);
        LoggerUtil.logInfo(AdminController.class, "Returning user with ID: "+ userId);
        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "Update a user by ID", response = Map.class)
    @PutMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> updateUser(
            @PathVariable int userId,
            @RequestBody UpdateUserRequest updateUserRequest) {
        LoggerUtil.logInfo(AdminController.class, "Request received to update user with ID: "+ userId);
        updateUserRequest.validateRole();
        Map<String, Object> updatedUser = adminService.updateUser(userId, updateUserRequest);
        LoggerUtil.logInfo(AdminController.class, "User with ID "+userId +" updated successfully.");
        return ResponseEntity.ok(updatedUser);
    }
}
