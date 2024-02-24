package com.example.securitydemoproject.service;

import com.example.securitydemoproject.dto.UpdateUserRequest;
import com.example.securitydemoproject.model.Member;
import com.example.securitydemoproject.model.Role;
import com.example.securitydemoproject.repository.AdminRepository;
import com.example.securitydemoproject.util.LoggerUtil;
import lombok.AllArgsConstructor;
import org.slf4j.MDC;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
@AllArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final AdminRepository adminRepository;

    @Override
    public List<Map<String, Object>> getUsers() {
        String requestId = MDC.get("requestId");
        LoggerUtil.requestLogInfo(AdminServiceImpl.class, requestId, "Retrieving all users.");
        List<Map<String, Object>> returnMemList = new ArrayList<>();
        List<Member> members = adminRepository.findAll();
        for (Member member : members) {
            Map<String, Object> cur = new HashMap<>();
            cur.put("id", member.getId());
            cur.put("name", member.getName());
            cur.put("email", member.getEmail());

            returnMemList.add(cur);
        }

        return returnMemList;
    }

    @Override
    public Map<String, Object> getUser(int userId) {
        String requestId = MDC.get("requestId");
        LoggerUtil.requestLogInfo(AdminServiceImpl.class, requestId, "Retrieving user by id: " + userId);
        Map<String, Object> response = new HashMap<>();
        Member member = adminRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> {
                    LoggerUtil.requestLogError(AdminServiceImpl.class, requestId, "User not found with id: " + userId, new UsernameNotFoundException("가입되지 않은 Id 입니다."));
                    return new UsernameNotFoundException("가입되지 않은 Id 입니다.");
                });

        if (member != null) {
            response.put("id", member.getId());
            response.put("name", member.getName());
            response.put("email", member.getEmail());
            response.put("password", member.getPassword());
            response.put("role", member.getRole());
        }

        return response;
    }

    @Override
    public Map<String, Object> updateUser(int userId, UpdateUserRequest updateUserRequest) {
        String requestId = MDC.get("requestId");
        LoggerUtil.requestLogInfo(AdminServiceImpl.class, requestId, "Updating user with id: " + userId);
        Map<String, Object> updatedUserDetails = new HashMap<>();

        Member member = adminRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> {
                    LoggerUtil.requestLogError(AdminServiceImpl.class, requestId, "User not found with id: " + userId, new UsernameNotFoundException("가입되지 않은 Id 입니다."));
                    return new UsernameNotFoundException("가입되지 않은 Id 입니다.");
                });

        if (member != null) {
            adminRepository.updateMemberRole(Long.valueOf(userId), Role.valueOf(updateUserRequest.getRole().toUpperCase()));

            updatedUserDetails.put("id", member.getId());
            updatedUserDetails.put("email", member.getEmail());
            updatedUserDetails.put("password", member.getPassword());
            updatedUserDetails.put("name", member.getName());
            updatedUserDetails.put("role", updateUserRequest.getRole());
        }

        return updatedUserDetails;
    }

}
