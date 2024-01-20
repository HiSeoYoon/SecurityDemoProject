package com.example.securitydemoproject.service;

import java.util.List;
import java.util.Map;

public interface AdminService {
    List<Map<String, Object>> getUsers();
    Map<String, Object> getUser(int userId);
}
