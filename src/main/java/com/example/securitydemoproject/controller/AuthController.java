package com.example.securitydemoproject.controller;

import com.example.securitydemoproject.dto.JwtRequestDto;
import com.example.securitydemoproject.dto.MemberSignupRequestDto;
import com.example.securitydemoproject.service.AuthService;
import com.example.securitydemoproject.service.LoginHistoryService;
import com.example.securitydemoproject.util.LoggerUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.MDC;
import io.jsonwebtoken.security.InvalidKeyException;

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
        String requestId = MDC.get("requestId");
        LoggerUtil.requestLogInfo(AuthController.class, requestId, "Request received to login user with email: "+request.getEmail());
        try {
            String token = authService.login(request);
            loginHistoryService.logLoginHistory(request.getEmail());
            LoggerUtil.requestLogInfo(AuthController.class, requestId, "User logged in successfully.");
            return ResponseEntity.ok(token);
        } catch (UsernameNotFoundException e) {
            LoggerUtil.requestLogError(AuthController.class, requestId, "User not found: ", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found: " + e.getMessage());
        }catch (BadCredentialsException e) {
            LoggerUtil.requestLogError(AuthController.class, requestId, "Authentication failed due to bad credentials: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Authentication failed due to bad credentials: " + e.getMessage());
        } catch (AuthenticationServiceException e) {
            LoggerUtil.requestLogError(AuthController.class, requestId, "Authentication service exception occurred: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Authentication service exception occurred: " + e.getMessage());
        }
    }

    @PostMapping(value = "/signup")
    @ApiOperation(value = "Register a new user")
    public ResponseEntity<String> signup(@RequestBody MemberSignupRequestDto request) {
        String requestId = MDC.get("requestId");
        LoggerUtil.requestLogInfo(AuthController.class, requestId, "Request received to signup new user with email: "+ request.getEmail());
        try {
            String message = authService.signup(request);
            LoggerUtil.requestLogInfo(AuthController.class, requestId, "New user signed up successfully.");
            return ResponseEntity.ok(message);
        } catch (DuplicateKeyException e) {
            LoggerUtil.requestLogError(AuthController.class, requestId, "User already exists: ", e);
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists: " + e.getMessage());
        }
    }

    @PostMapping(value = "/logout")
    @ApiOperation(value = "Invalidate JWT token and log user out")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String requestId = MDC.get("requestId");
        LoggerUtil.requestLogInfo(AuthController.class, requestId, "Request received to logged out.");
        try {
            String token = authService.extractTokenFromRequest(request);
            authService.logout(token);
            LoggerUtil.requestLogInfo(AuthController.class, requestId, "User logged out successfully.");
            return ResponseEntity.ok("Logout successful");
        } catch (InvalidKeyException e) {
            LoggerUtil.requestLogError(AuthController.class, requestId, "User already exists: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid token: " + e.getMessage());
        } catch (AuthenticationServiceException e) {
            LoggerUtil.requestLogError(AuthController.class, requestId, "Authentication service exception occurred: ", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Authentication service exception occurred: " + e.getMessage());
        }
    }
}
