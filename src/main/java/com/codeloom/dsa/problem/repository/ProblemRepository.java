package com.codeloom.dsa.problem.repository;

import com.codeloom.dsa.algorithm.entity.Difficulty;
import com.codeloom.dsa.problem.entity.Problem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, UUID> {

    Optional<Problem> findBySlug(String slug);

    List<Problem> findByCategoryId(UUID categoryId);

    List<Problem> findByDifficulty(Difficulty difficulty);

    Page<Problem> findByDifficulty(Difficulty difficulty, Pageable pageable);

    Page<Problem> findByCategorySlug(String categorySlug, Pageable pageable);

    Page<Problem> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}
