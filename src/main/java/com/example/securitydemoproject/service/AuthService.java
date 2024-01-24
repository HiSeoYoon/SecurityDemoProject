package com.example.securitydemoproject.service;

import com.example.securitydemoproject.dto.JwtRequestDto;
import com.example.securitydemoproject.dto.MemberSignupRequestDto;

import javax.servlet.http.HttpServletRequest;


public interface AuthService{
    String login(JwtRequestDto request);
    String signup(MemberSignupRequestDto request);
    void logout(String token);
    String extractTokenFromRequest(HttpServletRequest request);
}
