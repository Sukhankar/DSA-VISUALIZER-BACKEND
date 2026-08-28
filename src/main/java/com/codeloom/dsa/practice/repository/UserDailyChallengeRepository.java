package com.codeloom.dsa.practice.repository;

import com.codeloom.dsa.practice.entity.UserDailyChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserDailyChallengeRepository extends JpaRepository<UserDailyChallenge, UUID> {
    Optional<UserDailyChallenge> findByUserIdAndDailyChallengeId(UUID userId, UUID dailyChallengeId);
}
