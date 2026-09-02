package com.codeloom.dsa.visualization.repository;

import com.codeloom.dsa.visualization.entity.AlgorithmVisualizationContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AlgorithmVisualizationContractRepository extends JpaRepository<AlgorithmVisualizationContract, UUID> {

    Optional<AlgorithmVisualizationContract> findByAlgorithmId(UUID algorithmId);

    Optional<AlgorithmVisualizationContract> findByAlgorithmSlug(String slug);
}
