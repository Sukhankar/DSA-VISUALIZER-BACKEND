package com.codeloom.dsa.analytics.repository;

import com.codeloom.dsa.analytics.entity.UserAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserAchievementRepository extends JpaRepository<UserAchievement, UUID> {
    List<UserAchievement> findByUserIdOrderByUnlockedAtDesc(UUID userId);
    boolean existsByUserIdAndAchievementId(UUID userId, UUID achievementId);
    boolean existsByUserIdAndAchievementCode(UUID userId, String achievementCode);
    long countByUserId(UUID userId);
}
