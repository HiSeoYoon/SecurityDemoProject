package com.example.securitydemoproject.domain;

import com.example.securitydemoproject.dto.MemberSignupRequestDto;
import com.example.securitydemoproject.model.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String password;

    private String name;

    @Enumerated(EnumType.STRING)
    private Role role;

    public Member(MemberSignupRequestDto request) {
        email = request.getEmail();
        password = request.getPassword();
        name = request.getName();
        role = Role.USER;
    }

    public void encryptPassword(PasswordEncoder passwordEncoder) {
        password = passwordEncoder.encode(password);
    }
}
