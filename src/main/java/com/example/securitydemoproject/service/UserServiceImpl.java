package com.example.securitydemoproject.service;

import com.example.securitydemoproject.model.Member;
import com.example.securitydemoproject.repository.MemberRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final MemberRepository memberRepository;

    @Override
    public Map<String, Object> getUserByUserName(String user) {
        Map<String, Object> response = new HashMap<>();
        Member member = memberRepository.findByName(user)
                .orElseThrow(() -> new UsernameNotFoundException("가입되지 않은 USER NAME 입니다."));

        if(member != null){
            response.put("name", member.getName());
            response.put("email", member.getEmail());
            response.put("password", member.getPassword());
            response.put("role", member.getRole());
        }

        return response;
    }

    @Override
    public void changePassword(String username, String newPassword) {
        Member member = memberRepository.findByName(username)
                .orElseThrow(() -> new UsernameNotFoundException("가입되지 않은 E-MAIL 입니다."));
        try {
            member.setPassword(newPassword);
            memberRepository.save(member);
        } catch (DataAccessException e) {
            throw new RuntimeException("비밀번호 변경 중 오류가 발생했습니다.", e);
        }
    }
}
