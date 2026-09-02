package com.codeloom.dsa.algorithm.repository;

import com.codeloom.dsa.algorithm.entity.AlgorithmLearningPractice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AlgorithmLearningPracticeRepository extends JpaRepository<AlgorithmLearningPractice, UUID> {
    List<AlgorithmLearningPractice> findByLearningContentIdOrderByDisplayOrderAsc(UUID learningContentId);
}
