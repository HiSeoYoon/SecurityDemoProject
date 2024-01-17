package com.example.securitydemoproject.model;

import com.example.securitydemoproject.dto.MemberSignupRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
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
}
