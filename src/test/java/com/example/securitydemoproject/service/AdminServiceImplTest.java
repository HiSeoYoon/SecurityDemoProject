package com.example.securitydemoproject.service;

import com.example.securitydemoproject.dto.UpdateUserRequest;
import com.example.securitydemoproject.model.Member;
import com.example.securitydemoproject.model.Role;
import com.example.securitydemoproject.repository.AdminRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminServiceImplTest {
    @Mock
    private AdminRepository adminRepository;

    @InjectMocks
    private AdminServiceImpl adminService;

    private static Member member1;
    private static Member member2;

    @BeforeAll
    static void setUpBeforeAll() {
        // Create a common Member object for testing
        member1 = new Member();
        member1.setId(1L);
        member1.setName("User1");
        member1.setEmail("user1@example.com");
        member1.setPassword("password1");
        member1.setRole(Role.USER);

        member2 = new Member();
        member2.setId(2L);
        member2.setName("User2");
        member2.setEmail("user2@example.com");
        member2.setPassword("password2");
        member2.setRole(Role.ADMIN);
    }


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUsers_ReturnsUserList() {
        // Mock data
        List<Member> members = Arrays.asList(member1, member2);

        // Mocking the behavior of adminRepository.findAll()
        when(adminRepository.findAll()).thenReturn(members);

        // Perform the service method
        List<Map<String, Object>> result = adminService.getUsers();

        // Verify the interactions and assertions
        verify(adminRepository, times(1)).findAll();

        assertEquals(2, result.size());

        Map<String, Object> user1 = result.get(0);
        assertEquals(1L, user1.get("id"));
        assertEquals("User1", user1.get("name"));
        assertEquals("user1@example.com", user1.get("email"));

        Map<String, Object> user2 = result.get(1);
        assertEquals(2L, user2.get("id"));
        assertEquals("User2", user2.get("name"));
        assertEquals("user2@example.com", user2.get("email"));
    }

    @Test
    void getUser_ExistingUserId_ReturnsUserData() {
        // Mocking the behavior of adminRepository.findById()
        when(adminRepository.findById(1L)).thenReturn(Optional.of(member1));

        // Perform the service method
        Map<String, Object> result = adminService.getUser(1);

        // Verify the interactions and assertions
        verify(adminRepository, times(1)).findById(1L);

        assertEquals(1L, result.get("id"));
        assertEquals("User1", result.get("name"));
        assertEquals("user1@example.com", result.get("email"));
        assertEquals("password1", result.get("password"));
        assertEquals(Role.USER, result.get("role"));
    }

    @Test
    void updateUser_ExistingUserId_ReturnsUpdatedUserData() {
        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setRole("ADMIN");

        // Mocking the behavior of adminRepository.findById() and adminRepository.updateMemberRole()
        when(adminRepository.findById(1L)).thenReturn(Optional.of(member1));
        //when(adminRepository.updateMemberRole(1L, Role.ADMIN)).thenReturn(1);

        // Perform the service method
        Map<String, Object> result = adminService.updateUser(1, updateUserRequest);

        // Verify the interactions and assertions
        verify(adminRepository, times(1)).findById(1L);
        verify(adminRepository, times(1)).updateMemberRole(1L, Role.ADMIN);

        assertEquals(1L, result.get("id"));
        assertEquals("user1@example.com", result.get("email"));
        assertEquals("password1", result.get("password"));
        assertEquals("User1", result.get("name"));
        assertEquals("ADMIN", result.get("role"));
    }
}