package com.codeloom.dsa.algorithm.repository;

import com.codeloom.dsa.algorithm.entity.AlgorithmExample;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AlgorithmExampleRepository extends JpaRepository<AlgorithmExample, UUID> {
    List<AlgorithmExample> findByAlgorithmIdOrderByExampleNumberAsc(UUID algorithmId);
}
