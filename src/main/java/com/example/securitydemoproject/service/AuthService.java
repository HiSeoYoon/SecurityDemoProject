package com.example.securitydemoproject.service;

import com.example.securitydemoproject.dto.JwtRequestDto;
import com.example.securitydemoproject.dto.MemberSignupRequestDto;


public interface AuthService{
    public String login(JwtRequestDto request);
    public String signup(MemberSignupRequestDto request);
}
