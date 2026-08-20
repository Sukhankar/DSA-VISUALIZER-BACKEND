package com.codeloom.dsa.algorithm.repository;

import com.codeloom.dsa.algorithm.entity.AlgorithmCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AlgorithmCategoryRepository
        extends JpaRepository<AlgorithmCategory, UUID> {

    Optional<AlgorithmCategory> findBySlug(String slug);

    boolean existsBySlug(String slug);

    boolean existsByName(String name);
}