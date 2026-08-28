package com.codeloom.dsa.practice.repository;

import com.codeloom.dsa.practice.entity.PracticeSession;
import com.codeloom.dsa.practice.entity.SessionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PracticeSessionRepository extends JpaRepository<PracticeSession, UUID> {
    Optional<PracticeSession> findFirstByUserIdAndStatusOrderByStartedAtDesc(UUID userId, SessionStatus status);
    Page<PracticeSession> findByUserIdOrderByStartedAtDesc(UUID userId, Pageable pageable);
    List<PracticeSession> findTop5ByUserIdOrderByStartedAtDesc(UUID userId);
    long countByUserIdAndStatus(UUID userId, SessionStatus status);
}
