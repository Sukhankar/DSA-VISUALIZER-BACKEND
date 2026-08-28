package com.codeloom.dsa.problem.repository;

import com.codeloom.dsa.problem.entity.ProblemTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProblemTagRepository extends JpaRepository<ProblemTag, UUID> {
    List<ProblemTag> findByProblemId(UUID problemId);
}
