package com.codeloom.dsa.roadmap.repository;

import com.codeloom.dsa.roadmap.entity.UserRoadmapProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRoadmapProgressRepository extends JpaRepository<UserRoadmapProgress, UUID> {
    List<UserRoadmapProgress> findByUserId(UUID userId);
    Optional<UserRoadmapProgress> findByUserIdAndModuleSlug(UUID userId, String moduleSlug);
    Optional<UserRoadmapProgress> findByUserIdAndModuleId(UUID userId, UUID moduleId);
}
