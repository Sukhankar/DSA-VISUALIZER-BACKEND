package com.codeloom.dsa.analytics.repository;

import com.codeloom.dsa.analytics.entity.ActivityType;
import com.codeloom.dsa.analytics.entity.UserActivity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserActivityRepository extends JpaRepository<UserActivity, UUID> {
    Page<UserActivity> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
    List<UserActivity> findTop10ByUserIdOrderByCreatedAtDesc(UUID userId);
    long countByUserId(UUID userId);
    long countByUserIdAndActivityType(UUID userId, ActivityType activityType);
}

