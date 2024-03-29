package com.example.securitydemoproject.repository;

import com.example.securitydemoproject.model.Member;
import com.example.securitydemoproject.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataAccessException;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Member, String> {
    List<Member> findAll();

    Optional<Member> findById(long userId);

    @Transactional
    @Modifying
    @Query("UPDATE Member m SET m.role = :role WHERE m.id = :userId")
    void updateMemberRole(@Param("userId") long userId, @Param("role") Role role);

    default void updateMemberRoleWithExceptionHandling(long userId, Role role) {
        try {
            updateMemberRole(userId, role);
        } catch (DataAccessException e) {
            throw new RuntimeException("An error occurred while updating member role", e);
        }
    }

}