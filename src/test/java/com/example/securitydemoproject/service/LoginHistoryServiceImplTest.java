package com.example.securitydemoproject.service;

import com.example.securitydemoproject.model.LoginHistory;
import com.example.securitydemoproject.model.Member;
import com.example.securitydemoproject.repository.LoginHistoryRepository;
import com.example.securitydemoproject.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class LoginHistoryServiceImplTest {
    @Mock
    private LoginHistoryRepository loginHistoryRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private LoginHistoryServiceImpl loginHistoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void logLoginHistory_ValidEmail_LoginHistorySavedSuccessfully() {
        // Mock data
        String email = "test@example.com";

        Member mockMember = new Member();
        mockMember.setId(1L);
        mockMember.setEmail(email);

        // Mocking repository behavior
        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(mockMember));

        // Perform logLoginHistory
        assertDoesNotThrow(() -> loginHistoryService.logLoginHistory(email));

        // Verify that repository methods were called
        verify(memberRepository, times(1)).findByEmail(email);
        verify(loginHistoryRepository, times(1)).save(any(LoginHistory.class));
    }

    @Test
    void logLoginHistory_InvalidEmail_ThrowsException() {
        // Mock data
        String email = "nonExisting@example.com";

        // Mocking repository behavior
        when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Perform logLoginHistory and verify that the expected exception is thrown
        assertThrows(UsernameNotFoundException.class, () -> loginHistoryService.logLoginHistory(email));

        // Verify that repository method was called
        verify(memberRepository, times(1)).findByEmail(email);

        // Verify that loginHistoryRepository.save was not called
        verify(loginHistoryRepository, never()).save(any(LoginHistory.class));
    }

    @Test
    void getLoginHistoryByUserAndTimeRange_ValidUser_ReturnsLoginHistoryList() {
        // Mock data
        String username = "testUser";
        LocalDateTime startTime = LocalDateTime.now().minusHours(1);
        LocalDateTime endTime = LocalDateTime.now();

        Member mockMember = new Member();
        mockMember.setId(1L);

        // Mocking repository behavior
        when(memberRepository.findByName(username)).thenReturn(Optional.of(mockMember));

        List<LoginHistory> mockLoginHistoryList = new ArrayList<>();
        mockLoginHistoryList.add(new LoginHistory());
        mockLoginHistoryList.add(new LoginHistory());

        when(loginHistoryRepository.findByUserIdAndLoginTimeBetween(mockMember.getId(), startTime, endTime))
                .thenReturn(mockLoginHistoryList);

        // Perform getLoginHistoryByUserAndTimeRange
        List<LoginHistory> loginHistoryList = loginHistoryService.getLoginHistoryByUserAndTimeRange(username, startTime, endTime);

        // Verify the result
        assertNotNull(loginHistoryList);
        assertEquals(2, loginHistoryList.size());

        // Verify that repository methods were called
        verify(memberRepository, times(1)).findByName(username);
        verify(loginHistoryRepository, times(1)).findByUserIdAndLoginTimeBetween(mockMember.getId(), startTime, endTime);
    }

    @Test
    void getLoginHistoryByUserAndTimeRange_InvalidUser_ThrowsException() {
        // Mock data
        String username = "nonExistingUser";
        LocalDateTime startTime = LocalDateTime.now().minusHours(1);
        LocalDateTime endTime = LocalDateTime.now();

        // Mocking repository behavior
        when(memberRepository.findByName(username)).thenReturn(Optional.empty());

        // Perform getLoginHistoryByUserAndTimeRange and verify that the expected exception is thrown
        assertThrows(UsernameNotFoundException.class,
                () -> loginHistoryService.getLoginHistoryByUserAndTimeRange(username, startTime, endTime));

        // Verify that repository method was called
        verify(memberRepository, times(1)).findByName(username);

        // Verify that loginHistoryRepository.findByUserIdAndLoginTimeBetween was not called
        verify(loginHistoryRepository, never()).findByUserIdAndLoginTimeBetween(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class));
    }

}