package com.example.securitydemoproject.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtRequestDto {
    private String email;
    private String password;

    public JwtRequestDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
