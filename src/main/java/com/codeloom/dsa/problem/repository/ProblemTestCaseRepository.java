package com.codeloom.dsa.problem.repository;

import com.codeloom.dsa.problem.entity.ProblemTestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProblemTestCaseRepository extends JpaRepository<ProblemTestCase, UUID> {
    List<ProblemTestCase> findByProblemIdOrderByTestCaseNumberAsc(UUID problemId);
    List<ProblemTestCase> findByProblemIdAndIsHiddenFalseOrderByTestCaseNumberAsc(UUID problemId);
}
