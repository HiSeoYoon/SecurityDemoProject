package com.example.securitydemoproject.controller;

import com.example.securitydemoproject.dto.ChangePasswordRequest;
import com.example.securitydemoproject.security.JwtProvider;
import com.example.securitydemoproject.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Api(tags = "User Controller", description = "APIs for managing user details and actions")
public class UserController {
    private final UserService userService;
    private final JwtProvider jwtProvider;

    @Autowired
    public UserController(UserService userService, JwtProvider jwtProvider) {
        this.userService = userService;
        this.jwtProvider = jwtProvider;
    }

    @GetMapping("")
    @ApiOperation(value = "Get user details")
    public ResponseEntity<Map<String, Object>> getUser(HttpServletRequest request) {
        String username = jwtProvider.getUsernameFromToken(jwtProvider.resolveToken(request));
        Map<String, Object> response = userService.getUserByUserName(username);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/change-password")
    @ApiOperation(value = "Change user password")
    public ResponseEntity<String> changePassword(HttpServletRequest request, @RequestBody ChangePasswordRequest newPassword) {
        String username = jwtProvider.getUsernameFromToken(jwtProvider.resolveToken(request));
        userService.changePassword(username, newPassword.getNewPassword());
        return ResponseEntity.ok("Password changed successfully");
    }
}
