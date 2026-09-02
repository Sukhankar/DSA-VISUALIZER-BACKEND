package com.codeloom.dsa.algorithm.repository;

import com.codeloom.dsa.algorithm.entity.AlgorithmLearningAdvanced;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AlgorithmLearningAdvancedRepository extends JpaRepository<AlgorithmLearningAdvanced, UUID> {
    Optional<AlgorithmLearningAdvanced> findByLearningContentId(UUID learningContentId);
}
