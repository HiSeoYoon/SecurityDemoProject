package com.example.securitydemoproject.service;

import com.example.securitydemoproject.model.Member;
import com.example.securitydemoproject.model.Role;
import com.example.securitydemoproject.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


class UserServiceImplTest {
    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUserByUserName_ExistingUser_ReturnsUserDetails() {
        // Mock data
        String username = "testUser";

        Member mockMember = new Member();
        mockMember.setName(username);
        mockMember.setEmail("test@example.com");
        mockMember.setPassword("password");
        mockMember.setRole(Role.USER);

        // Mocking repository behavior
        when(memberRepository.findByName(username)).thenReturn(Optional.of(mockMember));

        // Perform the getUserByUserName
        Map<String, Object> userDetails = userService.getUserByUserName(username);

        // Verify the result
        assertNotNull(userDetails);
        assertEquals(username, userDetails.get("name"));
        assertEquals("test@example.com", userDetails.get("email"));
        assertEquals("password", userDetails.get("password"));
        assertEquals(Role.USER, userDetails.get("role"));

        // Verify that repository method was called
        verify(memberRepository, times(1)).findByName(username);
    }

    @Test
    void getUserByUserName_NonExistingUser_ThrowsException() {
        // Mock data
        String username = "nonExistingUser";

        // Mocking repository behavior
        when(memberRepository.findByName(username)).thenReturn(Optional.empty());

        // Perform the getUserByUserName and verify that the expected exception is thrown
        assertThrows(UsernameNotFoundException.class, () -> userService.getUserByUserName(username));

        // Verify that repository method was called
        verify(memberRepository, times(1)).findByName(username);
    }

    @Test
    void changePassword_ExistingUser_PasswordChangedSuccessfully() {
        // Mock data
        String username = "testUser";
        String newPassword = "newPassword";

        Member mockMember = new Member();
        mockMember.setName(username);
        mockMember.setEmail("test@example.com");
        mockMember.setPassword("password");
        mockMember.setRole(Role.USER);

        // Mocking repository behavior
        when(memberRepository.findByName(username)).thenReturn(Optional.of(mockMember));

        // Perform the changePassword
        assertDoesNotThrow(() -> userService.changePassword(username, newPassword));

        // Verify that repository method was called
        verify(memberRepository, times(1)).findByName(username);

        // Verify that member's password is updated
        assertEquals(newPassword, mockMember.getPassword());
    }

    @Test
    void changePassword_NonExistingUser_ThrowsException() {
        // Mock data
        String username = "nonExistingUser";
        String newPassword = "newPassword";

        // Mocking repository behavior
        when(memberRepository.findByName(username)).thenReturn(Optional.empty());

        // Perform the changePassword and verify that the expected exception is thrown
        assertThrows(UsernameNotFoundException.class, () -> userService.changePassword(username, newPassword));

        // Verify that repository method was called
        verify(memberRepository, times(1)).findByName(username);
    }
}