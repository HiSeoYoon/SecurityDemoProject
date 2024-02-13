package com.example.securitydemoproject.controller;

import com.example.securitydemoproject.dto.UpdateUserRequest;
import com.example.securitydemoproject.service.AdminService;
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
        List<Map<String, Object>> members = adminService.getUsers();
        return ResponseEntity.ok(members);
    }

    @ApiOperation(value = "Get a user by ID", response = Map.class)
    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable int userId) {
        Map<String, Object> response = adminService.getUser(userId);
        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "Update a user by ID", response = Map.class)
    @PutMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> updateUser(
            @PathVariable int userId,
            @RequestBody UpdateUserRequest updateUserRequest) {
        updateUserRequest.validateRole();
        Map<String, Object> updatedUser = adminService.updateUser(userId, updateUserRequest);
        return ResponseEntity.ok(updatedUser);
    }
}
