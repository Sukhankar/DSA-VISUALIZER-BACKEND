package com.codeloom.dsa.algorithm.repository;

import com.codeloom.dsa.algorithm.entity.AlgorithmLearningContent;
import com.codeloom.dsa.learning.entity.ExperienceLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AlgorithmLearningContentRepository extends JpaRepository<AlgorithmLearningContent, UUID> {
    Optional<AlgorithmLearningContent> findByAlgorithmIdAndLevel(UUID algorithmId, ExperienceLevel level);
    Optional<AlgorithmLearningContent> findByAlgorithmSlugAndLevel(String algorithmSlug, ExperienceLevel level);
}
