package com.example.securitydemoproject.service;

import com.example.securitydemoproject.dto.JwtRequestDto;
import com.example.securitydemoproject.dto.MemberSignupRequestDto;


public interface AuthService{
    String login(JwtRequestDto request);
    String signup(MemberSignupRequestDto request);
}
