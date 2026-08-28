package com.codeloom.dsa.algorithm.repository;

import com.codeloom.dsa.algorithm.entity.RelatedAlgorithm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RelatedAlgorithmRepository extends JpaRepository<RelatedAlgorithm, UUID> {
    List<RelatedAlgorithm> findByAlgorithmId(UUID algorithmId);
}
