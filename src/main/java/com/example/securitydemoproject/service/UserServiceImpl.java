package com.example.securitydemoproject.service;

import com.example.securitydemoproject.model.Member;
import com.example.securitydemoproject.repository.MemberRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final MemberRepository memberRepository;

    @Override
    public Member getUserByUserEmail(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("가입되지 않은 E-MAIL 입니다."));

        return member;
    }

    @Override
    public void changePassword(String email, String newPassword) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("가입되지 않은 E-MAIL 입니다."));
        try {
            member.setPassword(newPassword);
            memberRepository.save(member);
        } catch (DataAccessException e) {
            throw new RuntimeException("비밀번호 변경 중 오류가 발생했습니다.", e);
        }
    }
}
