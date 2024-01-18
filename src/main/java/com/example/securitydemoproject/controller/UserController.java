package com.example.securitydemoproject.controller;

import com.example.securitydemoproject.dto.ChangePasswordRequest;
import com.example.securitydemoproject.model.Member;
import com.example.securitydemoproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{useremail}")
    public ResponseEntity<String> getUser(@PathVariable String useremail) {
        try {
            Member member = userService.getUserByUserEmail(useremail);
            String responseBody = (member != null) ? member.toString() : "User not found";
            return ResponseEntity.ok(responseBody);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    @PostMapping("/{useremail}/password")
    public ResponseEntity<String> changePassword(@PathVariable String useremail, @RequestBody ChangePasswordRequest newPassword) {
        try {
            userService.changePassword(useremail, newPassword.getNewPassword());
            return ResponseEntity.ok("Password changed successfully");
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }
}
