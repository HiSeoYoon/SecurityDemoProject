package com.example.securitydemoproject.controller;

import com.example.securitydemoproject.dto.ChangePasswordRequest;
import com.example.securitydemoproject.security.JwtProvider;
import com.example.securitydemoproject.service.UserService;
import com.example.securitydemoproject.util.LoggerUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.MDC;
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
        String requestId = MDC.get("requestId");
        LoggerUtil.requestLogInfo(UserController.class, requestId, "Request received to get details for user " + username);
        Map<String, Object> response = userService.getUserByUserName(username);
        LoggerUtil.requestLogInfo(UserController.class, requestId, "User details retrieved successfully.");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/change-password")
    @ApiOperation(value = "Change user password")
    public ResponseEntity<String> changePassword(HttpServletRequest request, @RequestBody ChangePasswordRequest newPassword) {
        String username = jwtProvider.getUsernameFromToken(jwtProvider.resolveToken(request));
        String requestId = MDC.get("requestId");
        LoggerUtil.requestLogInfo(UserController.class, requestId, "Request received to change password for user " + username);
        userService.changePassword(username, newPassword.getNewPassword());
        LoggerUtil.requestLogInfo(UserController.class, requestId, "Password changed successfully for user " + username);
        return ResponseEntity.ok("Password changed successfully");
    }
}
