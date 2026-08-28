package com.codeloom.dsa.algorithm.repository;

import com.codeloom.dsa.algorithm.entity.AlgorithmImplementation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AlgorithmImplementationRepository extends JpaRepository<AlgorithmImplementation, UUID> {
    List<AlgorithmImplementation> findByAlgorithmIdOrderByDisplayOrderAsc(UUID algorithmId);
}
