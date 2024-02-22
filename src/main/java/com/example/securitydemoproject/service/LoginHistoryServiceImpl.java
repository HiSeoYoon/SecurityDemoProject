package com.example.securitydemoproject.service;

import com.example.securitydemoproject.model.LoginHistory;
import com.example.securitydemoproject.model.Member;
import com.example.securitydemoproject.repository.LoginHistoryRepository;
import com.example.securitydemoproject.repository.MemberRepository;
import com.example.securitydemoproject.util.LoggerUtil;
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
        LoggerUtil.logDebug(LoginHistoryServiceImpl.class, "Logging login history for email: " + email);
        LoginHistory loginHistory = new LoginHistory();
        loginHistory.setUser(new Member(getUserId(email)));
        loginHistory.setLoginTime(LocalDateTime.now());
        loginHistoryRepository.save(loginHistory);
        LoggerUtil.logInfo(LoginHistoryServiceImpl.class, "Login history saved successfully for email: " + email);
    }

    private Long getUserId(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> {
                    LoggerUtil.logError(LoginHistoryServiceImpl.class, "Unregistered E-MAIL address: {}" + email, new UsernameNotFoundException("가입되지 않은 E-MAIL 입니다."));
                    return new UsernameNotFoundException("가입되지 않은 E-MAIL 입니다.");
                });

        return member.getId();
    }

    @Override
    public List<LoginHistory> getLoginHistoryByUserAndTimeRange(String username, LocalDateTime startTime, LocalDateTime endTime) {
        LoggerUtil.logInfo(LoginHistoryServiceImpl.class, "Retrieving login history for user: " + username + " between " + startTime + " and " + endTime);
        Member member = memberRepository.findByName(username)
                .orElseThrow(() -> {
                    LoggerUtil.logError(LoginHistoryServiceImpl.class, "Unregistered USER NAME: {}" + username, new UsernameNotFoundException("가입되지 않은 USER NAME 입니다."));
                    return new UsernameNotFoundException("가입되지 않은 USER NAME 입니다.");
                });

        Long userId = member.getId();
        List<LoginHistory> loginHistories = loginHistoryRepository.findByUserIdAndLoginTimeBetween(userId, startTime, endTime);
        LoggerUtil.logInfo(LoginHistoryServiceImpl.class, "Retrieved " + loginHistories.size() + " login history entries for user: " + username + " between " + startTime + " and " + endTime);
        return loginHistories;
    }
}
