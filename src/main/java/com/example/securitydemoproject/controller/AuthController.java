package com.example.securitydemoproject.controller;

import com.example.securitydemoproject.dto.JwtRequestDto;
import com.example.securitydemoproject.dto.MemberSignupRequestDto;
import com.example.securitydemoproject.service.AuthService;
import com.example.securitydemoproject.service.LoginHistoryService;
import lombok.AllArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final LoginHistoryService loginHistoryService;

    @PostMapping(value = "/login")
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
}
