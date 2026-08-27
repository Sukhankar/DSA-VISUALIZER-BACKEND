package com.codeloom.dsa.progress.repository;

import com.codeloom.dsa.progress.entity.ProgressStatus;
import com.codeloom.dsa.progress.entity.UserAlgorithmProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserAlgorithmProgressRepository extends JpaRepository<UserAlgorithmProgress, UUID> {
    Optional<UserAlgorithmProgress> findByUserIdAndAlgorithmId(UUID userId, UUID algorithmId);
    List<UserAlgorithmProgress> findByUserIdOrderByUpdatedAtDesc(UUID userId);
    List<UserAlgorithmProgress> findTop5ByUserIdOrderByUpdatedAtDesc(UUID userId);
    long countByUserIdAndStatus(UUID userId, ProgressStatus status);
    long countByUserIdAndStatusIn(UUID userId, Collection<ProgressStatus> statuses);
}
