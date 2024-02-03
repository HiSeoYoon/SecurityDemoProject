package com.example.securitydemoproject.service;

import static org.junit.jupiter.api.Assertions.*;

import com.example.securitydemoproject.dto.JwtRequestDto;
import com.example.securitydemoproject.dto.MemberSignupRequestDto;
import com.example.securitydemoproject.model.Member;
import com.example.securitydemoproject.model.Role;
import com.example.securitydemoproject.repository.MemberRepository;
import com.example.securitydemoproject.security.JwtProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.servlet.http.HttpServletRequest;
import java.security.InvalidKeyException;
import java.util.Optional;

import static org.mockito.Mockito.*;

class AuthServiceImplTest {
    @Mock
    private MemberRepository memberRepository;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login_ValidCredentials_ReturnsToken() {
        // Mock data
        String email = "test@example.com";
        String password = "password";
        JwtRequestDto requestDto = new JwtRequestDto(email, password);

        Member mockMember = new Member();
        mockMember.setEmail(email);
        mockMember.setPassword(password);
        mockMember.setName("TestUser");
        mockMember.setRole(Role.USER);

        // Mocking repository behavior
        when(memberRepository.findByEmail(email)).thenReturn(java.util.Optional.of(mockMember));

        // Mocking JwtProvider behavior
        when(jwtProvider.createToken(anyString(), anyString())).thenReturn("mockedToken");

        // Perform the login
        String token = authService.login(requestDto);

        // Verify the result
        assertNotNull(token);
        assertEquals("mockedToken", token);

        // Verify that repository method was called
        verify(memberRepository, times(1)).findByEmail(email);

        // Verify that JwtProvider method was called
        verify(jwtProvider, times(1)).createToken("TestUser", Role.USER.toString());
    }

    @Test
    void login_InvalidCredentials_ThrowsException() {
        // Mock data
        String email = "test@example.com";
        String password = "invalidPassword";
        JwtRequestDto requestDto = new JwtRequestDto(email, password);

        // Mocking repository behavior
        when(memberRepository.findByEmail(email)).thenReturn(java.util.Optional.empty());

        // Perform the login and verify that the expected exception is thrown
        assertThrows(UsernameNotFoundException.class, () -> authService.login(requestDto));

        // Verify that repository method was called
        verify(memberRepository, times(1)).findByEmail(email);

        // Verify that JwtProvider method was not called
        verify(jwtProvider, never()).createToken(any(), any());
    }

    @Test
    void login_InvalidEmail_ThrowsUsernameNotFoundException() {
        // Arrange
        JwtRequestDto request = new JwtRequestDto("nonexistent@example.com", "password");
        when(memberRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> authService.login(request));
    }

    @Test
    void login_InvalidPassword_ThrowsBadCredentialsException() {
        // Arrange
        JwtRequestDto request = new JwtRequestDto("test@example.com", "wrongPassword");
        Member mockMember = new Member();
        mockMember.setEmail("test@example.com");
        mockMember.setPassword("password");
        when(memberRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(mockMember));

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }

    @Test
    void signup_ValidRequest_ReturnsEmail() {
        // Arrange
        MemberSignupRequestDto request = new MemberSignupRequestDto();
        request.setEmail("test@example.com");
        request.setPassword("password");
        request.setName("TestUser");

        when(memberRepository.existsByEmail(request.getEmail())).thenReturn(false);

        // Act
        String email = authService.signup(request);

        // Assert
        assertNotNull(email);
        assertEquals("test@example.com", email);
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    void signup_DuplicateEmail_ThrowsDuplicateKeyException() {
        // Arrange
        MemberSignupRequestDto request = new MemberSignupRequestDto();
        request.setEmail("existing@example.com");
        request.setPassword("password");
        request.setName("TestUser");

        when(memberRepository.existsByEmail(request.getEmail())).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateKeyException.class, () -> authService.signup(request));
    }

    @Test
    void logout_ValidToken_BlacklistsToken() {
        // Arrange
        String validToken = "validToken";
        when(jwtProvider.validateToken(validToken)).thenReturn(true);

        // Act
        authService.logout(validToken);

        // Assert
        verify(jwtProvider, times(1)).addToBlacklist(eq(validToken));
    }

    @Test
    void logout_InvalidToken_ThrowsInvalidKeyException() {
        // Arrange
        String invalidToken = "invalid_token";
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(jwtProvider.validateToken(invalidToken)).thenReturn(false);

        // Act
        Executable act = () -> authService.logout(invalidToken);

        // Assert
        Assertions.assertThrows(io.jsonwebtoken.security.InvalidKeyException.class, () -> {
            authService.logout("invalidToken");
        });
    }

    @Test
    void extractTokenFromRequest_ValidRequest_ReturnsToken() {
        // Arrange
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(jwtProvider.resolveToken(mockRequest)).thenReturn("mockedToken");

        // Act
        String token = authService.extractTokenFromRequest(mockRequest);

        // Assert
        assertNotNull(token);
        assertEquals("mockedToken", token);
    }
}