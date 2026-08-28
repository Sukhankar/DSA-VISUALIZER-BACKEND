package com.codeloom.dsa.problem.repository;

import com.codeloom.dsa.problem.entity.ProblemRelatedAlgorithm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProblemRelatedAlgorithmRepository extends JpaRepository<ProblemRelatedAlgorithm, UUID> {
    List<ProblemRelatedAlgorithm> findByProblemId(UUID problemId);
}
