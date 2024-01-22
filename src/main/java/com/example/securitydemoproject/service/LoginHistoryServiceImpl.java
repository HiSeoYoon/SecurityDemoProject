package com.example.securitydemoproject.service;

import com.example.securitydemoproject.model.LoginHistory;
import com.example.securitydemoproject.model.Member;
import com.example.securitydemoproject.repository.LoginHistoryRepository;
import com.example.securitydemoproject.repository.MemberRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class LoginHistoryServiceImpl implements LoginHistoryService {
    private final LoginHistoryRepository loginHistoryRepository;
    private final MemberRepository memberRepository;

    @Override
    public void logLoginHistory(String email) {
        LoginHistory loginHistory = new LoginHistory();
        loginHistory.setUser(new Member(getUserId(email)));
        loginHistory.setLoginTime(LocalDateTime.now());
        loginHistoryRepository.save(loginHistory);
    }

    private Long getUserId(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("가입되지 않은 E-MAIL 입니다."));

        return member.getId();
    }

    @Override
    public List<LoginHistory> getLoginHistoryByUserAndTimeRange(String username, LocalDateTime startTime, LocalDateTime endTime) {
        Member member = memberRepository.findByName(username)
                .orElseThrow(() -> new UsernameNotFoundException("가입되지 않은 USER NAME 입니다."));

        Long userId = member.getId();
        return loginHistoryRepository.findByUserIdAndLoginTimeBetween(userId, startTime, endTime);
    }


}
