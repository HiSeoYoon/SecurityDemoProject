package com.example.securitydemoproject.controller;

import com.example.securitydemoproject.dto.JwtRequestDto;
import com.example.securitydemoproject.dto.MemberSignupRequestDto;
import com.example.securitydemoproject.service.AuthService;
import com.example.securitydemoproject.service.LoginHistoryService;
import com.example.securitydemoproject.util.LoggerUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
@Api(tags = "Authorization Controller", description = "APIs for managing user's authorization")
public class AuthController {

    private final AuthService authService;
    private final LoginHistoryService loginHistoryService;

    @PostMapping(value = "/login")
    @ApiOperation(value = "Authenticate user and generate JWT token")
    public ResponseEntity<String> login(@RequestBody JwtRequestDto request) {
        LoggerUtil.logInfo(AuthController.class, "Request received to login user with email: "+request.getEmail());
        String token = authService.login(request);
        loginHistoryService.logLoginHistory(request.getEmail());
        LoggerUtil.logInfo(AuthController.class, "User logged in successfully.");
        return ResponseEntity.ok(token);
    }

    @PostMapping(value = "/signup")
    @ApiOperation(value = "Register a new user")
    public ResponseEntity<String> signup(@RequestBody MemberSignupRequestDto request) {
        LoggerUtil.logInfo(AuthController.class, "Request received to signup new user with email: "+ request.getEmail());
        String message = authService.signup(request);
        LoggerUtil.logInfo(AuthController.class, "New user signed up successfully.");
        return ResponseEntity.ok(message);
    }

    @PostMapping(value = "/logout")
    @ApiOperation(value = "Invalidate JWT token and log user out")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String token = authService.extractTokenFromRequest(request);
        authService.logout(token);
        LoggerUtil.logInfo(AuthController.class, "User logged out successfully.");
        return ResponseEntity.ok("Logout successful");
    }
}
