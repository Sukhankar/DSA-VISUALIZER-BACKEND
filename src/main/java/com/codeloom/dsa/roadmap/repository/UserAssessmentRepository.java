package com.codeloom.dsa.roadmap.repository;

import com.codeloom.dsa.roadmap.entity.UserAssessment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserAssessmentRepository extends JpaRepository<UserAssessment, UUID> {
    Optional<UserAssessment> findFirstByUserIdOrderByCompletedAtDesc(UUID userId);
}
