package com.example.securitydemoproject.service;

import java.util.Map;


public interface UserService {
    Map<String, Object> getUserByUserName(String user);

    void changePassword(String username, String newPassword);
}
