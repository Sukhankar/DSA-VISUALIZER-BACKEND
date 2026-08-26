package com.codeloom.dsa.algorithm.repository;

import com.codeloom.dsa.algorithm.entity.Algorithm;
import com.codeloom.dsa.algorithm.entity.Difficulty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AlgorithmRepository
        extends JpaRepository<Algorithm, UUID> {

    Optional<Algorithm> findBySlug(String slug);

    boolean existsBySlug(String slug);

    Page<Algorithm> findByCategorySlug(
            String slug,
            Pageable pageable
    );

    Page<Algorithm> findByDifficulty(
            Difficulty difficulty,
            Pageable pageable
    );

    Page<Algorithm> findByNameContainingIgnoreCase(
            String name,
            Pageable pageable
    );
}