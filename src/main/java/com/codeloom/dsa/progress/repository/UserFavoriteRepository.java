package com.codeloom.dsa.progress.repository;

import com.codeloom.dsa.progress.entity.UserFavorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserFavoriteRepository extends JpaRepository<UserFavorite, UUID> {
    boolean existsByUserIdAndAlgorithmId(UUID userId, UUID algorithmId);
    Optional<UserFavorite> findByUserIdAndAlgorithmId(UUID userId, UUID algorithmId);
    List<UserFavorite> findByUserIdOrderByCreatedAtDesc(UUID userId);
    long countByUserId(UUID userId);
    void deleteByUserIdAndAlgorithmId(UUID userId, UUID algorithmId);
}
