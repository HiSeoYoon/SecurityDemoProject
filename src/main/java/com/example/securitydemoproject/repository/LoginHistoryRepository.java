package com.example.securitydemoproject.repository;

import com.example.securitydemoproject.model.LoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {

    List<LoginHistory> findByUserIdAndLoginTimeBetween(Long userId, LocalDateTime startTime, LocalDateTime endTime);
}
