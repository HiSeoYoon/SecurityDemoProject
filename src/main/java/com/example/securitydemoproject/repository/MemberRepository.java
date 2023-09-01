package com.example.securitydemoproject.repository;

import com.example.securitydemoproject.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, String> {
}
