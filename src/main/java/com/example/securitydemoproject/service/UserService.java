package com.example.securitydemoproject.service;

import com.example.securitydemoproject.model.Member;


public interface UserService {
    Member getUserByUserEmail(String email);

    void changePassword(String username, String newPassword);
}
