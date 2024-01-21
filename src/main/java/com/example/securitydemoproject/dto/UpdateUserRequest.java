package com.example.securitydemoproject.dto;

import com.example.securitydemoproject.model.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {
    private String role;

    public void validateRole() {
        if (role == null || (!role.equals(Role.USER.name()) && !role.equals(Role.ADMIN.name()))) {
            throw new IllegalArgumentException("Invalid role specified");
        }
    }

}
