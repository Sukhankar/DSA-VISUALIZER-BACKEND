package com.codeloom.dsa.problem.repository;

import com.codeloom.dsa.problem.entity.ProblemCodeDraft;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProblemCodeDraftRepository extends JpaRepository<ProblemCodeDraft, UUID> {
    Optional<ProblemCodeDraft> findByUserIdAndProblemIdAndLanguage(UUID userId, UUID problemId, String language);
}
