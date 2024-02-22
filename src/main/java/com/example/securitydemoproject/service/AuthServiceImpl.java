package com.example.securitydemoproject.service;

import com.example.securitydemoproject.dto.JwtRequestDto;
import com.example.securitydemoproject.dto.MemberSignupRequestDto;
import com.example.securitydemoproject.model.Member;
import com.example.securitydemoproject.repository.MemberRepository;
import com.example.securitydemoproject.security.JwtProvider;
import io.jsonwebtoken.security.InvalidKeyException;
import lombok.AllArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;

    @Override
    public String login(JwtRequestDto request) {

        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("가입되지 않은 E-MAIL 입니다."));

        if(request.getPassword().compareTo(member.getPassword()) != 0){
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }
        try {
            return jwtProvider.createToken(member.getName(), member.getRole().toString());
        } catch (Exception e) {
            throw new AuthenticationServiceException("토큰 생성에 실패하였습니다.", e);
        }
    }

    @Override
    public String signup(MemberSignupRequestDto request) {
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateKeyException("이미 등록된 E-MAIL 주소입니다.");
        }

        Member member = new Member(request);

        memberRepository.save(member);
        return member.getEmail();
    }

    @Override
    public void logout(String token) {
        if (jwtProvider.validateToken(token)) {
            jwtProvider.addToBlacklist(token);
        } else {
            throw new InvalidKeyException("Invalid token");
        }
    }

    @Override
    public String extractTokenFromRequest(HttpServletRequest request) {
        return jwtProvider.resolveToken(request);
        String token = jwtProvider.resolveToken(request);
        if (token == null) {
            throw new AuthenticationServiceException("Failed to extract token.");
        }
        return token;
    }
}
