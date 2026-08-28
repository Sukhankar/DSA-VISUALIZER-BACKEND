package com.codeloom.dsa.problem.repository;

import com.codeloom.dsa.problem.entity.ProblemSubmission;
import com.codeloom.dsa.problem.entity.SubmissionVerdict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProblemSubmissionRepository extends JpaRepository<ProblemSubmission, UUID> {

    List<ProblemSubmission> findByUserIdAndProblemIdOrderBySubmittedAtDesc(UUID userId, UUID problemId);

    Page<ProblemSubmission> findByUserIdOrderBySubmittedAtDesc(UUID userId, Pageable pageable);

    long countByUserId(UUID userId);

    long countByUserIdAndVerdict(UUID userId, SubmissionVerdict verdict);

    @Query("SELECT COUNT(DISTINCT s.problem.id) FROM ProblemSubmission s WHERE s.user.id = :userId AND s.verdict = 'ACCEPTED'")
    long countAcceptedSubmissionsByUser(@Param("userId") UUID userId);

    @Query("SELECT COUNT(DISTINCT s.problem.id) FROM ProblemSubmission s WHERE s.user.id = :userId AND s.verdict = :verdict")
    long countDistinctSolvedProblemsByUserId(@Param("userId") UUID userId, @Param("verdict") SubmissionVerdict verdict);

    @Query("SELECT COUNT(DISTINCT s.problem.id) FROM ProblemSubmission s WHERE s.user.id = :userId AND s.verdict = :verdict AND s.problem.difficulty = :difficulty")
    long countDistinctSolvedProblemsByUserIdAndDifficulty(
            @Param("userId") UUID userId,
            @Param("verdict") SubmissionVerdict verdict,
            @Param("difficulty") com.codeloom.dsa.algorithm.entity.Difficulty difficulty
    );

    @Query("SELECT COUNT(DISTINCT s.problem.id) FROM ProblemSubmission s WHERE s.user.id = :userId AND s.problem.category.id = :categoryId AND s.verdict = com.codeloom.dsa.problem.entity.SubmissionVerdict.ACCEPTED")
    long countSolvedByUserIdAndCategory(@Param("userId") UUID userId, @Param("categoryId") UUID categoryId);
}
