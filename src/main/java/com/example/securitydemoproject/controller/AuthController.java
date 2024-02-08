package com.example.securitydemoproject.controller;

import com.example.securitydemoproject.dto.JwtRequestDto;
import com.example.securitydemoproject.dto.MemberSignupRequestDto;
import com.example.securitydemoproject.service.AuthService;
import com.example.securitydemoproject.service.LoginHistoryService;
import io.jsonwebtoken.security.InvalidKeyException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
        try {
            String token = authService.login(request);
            loginHistoryService.logLoginHistory(request.getEmail());
            return ResponseEntity.ok(token);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed: " + e.getMessage());
        }
    }

    @PostMapping(value = "/signup")
    @ApiOperation(value = "Register a new user")
    public ResponseEntity<String> signup(@RequestBody MemberSignupRequestDto request) {
        try {
            String message = authService.signup(request);
            return ResponseEntity.ok(message);
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    @PostMapping(value = "/logout")
    @ApiOperation(value = "Invalidate JWT token and log user out")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        try {
            String token = authService.extractTokenFromRequest(request);
            authService.logout(token);
            return ResponseEntity.ok("Logout successful");
        } catch (InvalidKeyException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid token: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }
}
