package com.example.securitydemoproject.service;

import com.example.securitydemoproject.dto.UpdateUserRequest;

import java.util.List;
import java.util.Map;

public interface AdminService {
    List<Map<String, Object>> getUsers();

    Map<String, Object> getUser(int userId);

    Map<String, Object> updateUser(int userId, UpdateUserRequest updateUserRequest);
}
