package com.codeloom.dsa.progress.repository;

import com.codeloom.dsa.progress.entity.UserAlgorithmMastery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserAlgorithmMasteryRepository extends JpaRepository<UserAlgorithmMastery, UUID> {
    Optional<UserAlgorithmMastery> findByUserIdAndAlgorithmId(UUID userId, UUID algorithmId);
    Optional<UserAlgorithmMastery> findByUserIdAndAlgorithmSlug(UUID userId, String slug);
    List<UserAlgorithmMastery> findByUserId(UUID userId);
    long countByUserIdAndMasteredTrue(UUID userId);
}
