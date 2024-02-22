package com.example.securitydemoproject.service;

import com.example.securitydemoproject.dto.JwtRequestDto;
import com.example.securitydemoproject.dto.MemberSignupRequestDto;
import com.example.securitydemoproject.model.Member;
import com.example.securitydemoproject.repository.MemberRepository;
import com.example.securitydemoproject.security.JwtProvider;
import com.example.securitydemoproject.util.LoggerUtil;
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
public class AuthServiceImpl implements AuthService {

    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;

    @Override
    public String login(JwtRequestDto request) {
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    LoggerUtil.logError(AuthServiceImpl.class, "Unregistered E-MAIL address: " + request.getEmail(), new UsernameNotFoundException("가입되지 않은 E-MAIL 입니다."));
                    return new UsernameNotFoundException("가입되지 않은 E-MAIL 입니다.");
                });

        if (request.getPassword().compareTo(member.getPassword()) != 0) {
            LoggerUtil.logError(AuthServiceImpl.class, "Incorrect password.", new BadCredentialsException("비밀번호가 일치하지 않습니다."));
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }
        try {
            return jwtProvider.createToken(member.getName(), member.getRole().toString());
        } catch (Exception e) {
            LoggerUtil.logError(AuthServiceImpl.class, "Failed to generate token.", e);
            throw new AuthenticationServiceException("토큰 생성에 실패하였습니다.", e);
        }
    }

    @Override
    public String signup(MemberSignupRequestDto request) {
        if (memberRepository.existsByEmail(request.getEmail())) {
            LoggerUtil.logError(AuthServiceImpl.class, "Already registered E-MAIL address.", new DuplicateKeyException("이미 등록된 E-MAIL 주소입니다."));
            throw new DuplicateKeyException("이미 등록된 E-MAIL 주소입니다.");
        }

        Member member = new Member(request);

        memberRepository.save(member);
        LoggerUtil.logInfo(AuthServiceImpl.class, "Sign up completed. Email: " + member.getEmail());
        return member.getEmail();
    }

    @Override
    public void logout(String token) {
        if (jwtProvider.validateToken(token)) {
            jwtProvider.addToBlacklist(token);
            LoggerUtil.logInfo(AuthServiceImpl.class, "Logout completed. Token: {}" + token);
        } else {
            LoggerUtil.logError(AuthServiceImpl.class, "Invalid token.", new InvalidKeyException("Invalid token"));
            throw new InvalidKeyException("Invalid token");
        }
    }

    @Override
    public String extractTokenFromRequest(HttpServletRequest request) {
        String token = jwtProvider.resolveToken(request);
        if (token == null) {
            LoggerUtil.logError(AuthServiceImpl.class, "Failed to extract token.", new AuthenticationServiceException("Failed to extract token."));
            throw new AuthenticationServiceException("Failed to extract token.");
        }
        return token;
    }
}
