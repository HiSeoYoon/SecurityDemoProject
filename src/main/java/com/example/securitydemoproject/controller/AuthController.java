package com.example.securitydemoproject.controller;

import com.example.securitydemoproject.dto.JwtRequestDto;
import com.example.securitydemoproject.dto.MemberSignupRequestDto;
import com.example.securitydemoproject.service.AuthService;
import com.example.securitydemoproject.service.LoginHistoryService;
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
        String token = authService.login(request);
        loginHistoryService.logLoginHistory(request.getEmail());
        return ResponseEntity.ok(token);
    }

    @PostMapping(value = "/signup")
    @ApiOperation(value = "Register a new user")
    public ResponseEntity<String> signup(@RequestBody MemberSignupRequestDto request) {
        String message = authService.signup(request);
        return ResponseEntity.ok(message);
    }

    @PostMapping(value = "/logout")
    @ApiOperation(value = "Invalidate JWT token and log user out")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String token = authService.extractTokenFromRequest(request);
        authService.logout(token);
        return ResponseEntity.ok("Logout successful");
    }
}
