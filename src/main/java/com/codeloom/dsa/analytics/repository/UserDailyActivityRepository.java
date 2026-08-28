package com.codeloom.dsa.analytics.repository;

import com.codeloom.dsa.analytics.entity.UserDailyActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserDailyActivityRepository extends JpaRepository<UserDailyActivity, UUID> {
    Optional<UserDailyActivity> findByUserIdAndActivityDate(UUID userId, LocalDate activityDate);
    List<UserDailyActivity> findByUserIdAndActivityDateBetweenOrderByActivityDateAsc(UUID userId, LocalDate startDate, LocalDate endDate);
}
