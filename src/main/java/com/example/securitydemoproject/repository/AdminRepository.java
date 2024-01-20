package com.example.securitydemoproject.repository;

import com.example.securitydemoproject.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminRepository extends JpaRepository<Member, String> {
    List<Member> findAll();

    Member findById(long userId);

}