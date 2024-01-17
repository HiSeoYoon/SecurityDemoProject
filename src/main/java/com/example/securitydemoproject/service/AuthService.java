package com.example.securitydemoproject.service;

import com.example.securitydemoproject.model.Member;
import com.example.securitydemoproject.dto.JwtRequestDto;
import com.example.securitydemoproject.dto.MemberSignupRequestDto;
import com.example.securitydemoproject.repository.MemberRepository;
import com.example.securitydemoproject.security.JwtProvider;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;

    public String login(JwtRequestDto request) throws Exception {

        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 E-MAIL 입니다."));

        return jwtProvider.createToken(member.getName());
    }

    public String signup(MemberSignupRequestDto request) {
        if (memberRepository.existsByEmail(request.getEmail())) {
            return null;
        }

        Member member = new Member(request);

        memberRepository.save(member);
        return member.getEmail();
    }
}
