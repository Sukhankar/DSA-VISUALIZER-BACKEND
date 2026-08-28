package com.codeloom.dsa.practice.repository;

import com.codeloom.dsa.practice.entity.PracticeSessionProblem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PracticeSessionProblemRepository extends JpaRepository<PracticeSessionProblem, UUID> {
    Optional<PracticeSessionProblem> findBySessionIdAndProblemId(UUID sessionId, UUID problemId);
}
