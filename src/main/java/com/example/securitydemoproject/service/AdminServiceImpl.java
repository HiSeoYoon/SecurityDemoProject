package com.example.securitydemoproject.service;

import com.example.securitydemoproject.model.Member;
import com.example.securitydemoproject.repository.AdminRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@AllArgsConstructor
public class AdminServiceImpl implements AdminService{
    private final AdminRepository adminRepository;
    @Override
    public List<Map<String, Object>> getUsers() {
        List<Map<String, Object>> returnMemList = new ArrayList<>();
        List<Member> members = adminRepository.findAll();
        for(Member member : members){
            Map<String, Object> cur = new HashMap<>();
            cur.put("id", member.getId());
            cur.put("name", member.getName());
            cur.put("email", member.getEmail());

            returnMemList.add(cur);
        }

        return returnMemList;
    }

    @Override
    public Map<String, Object> getUser(int userId){
        Map<String, Object> response = new HashMap<>();
        Member member = adminRepository.findById(Long.valueOf(userId));
        if(member != null){
            response.put("id", member.getId());
            response.put("name", member.getName());
            response.put("email", member.getEmail());
            response.put("password", member.getPassword());
            response.put("role", member.getRole());
        }

        return response;
    }

}
