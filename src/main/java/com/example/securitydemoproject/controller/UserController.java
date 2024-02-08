package com.example.securitydemoproject.controller;

import com.example.securitydemoproject.dto.ChangePasswordRequest;
import com.example.securitydemoproject.security.JwtProvider;
import com.example.securitydemoproject.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
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
        Map<String, Object> response = new HashMap<>();
        try {
            String username = jwtProvider.getUsernameFromToken(jwtProvider.resolveToken(request));
            response = userService.getUserByUserName(username);
            return ResponseEntity.ok(response);
        } catch (UsernameNotFoundException e) {
            response.put("error", "An error occurred: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("error", "An error occurred: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/change-password")
    @ApiOperation(value = "Change user password")
    public ResponseEntity<String> changePassword(HttpServletRequest request, @RequestBody ChangePasswordRequest newPassword) {
        try {
            String username = jwtProvider.getUsernameFromToken(jwtProvider.resolveToken(request));
            userService.changePassword(username, newPassword.getNewPassword());
            return ResponseEntity.ok("Password changed successfully");
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }
}
