package com.codeloom.dsa.profile.dto;

import com.codeloom.dsa.analytics.dto.LevelProgressDto;
import com.codeloom.dsa.analytics.dto.StreakStatusDto;

import java.util.UUID;

public record UserProfileDto(
        UUID id,
        UUID userId,
        String username,
        String displayName,
        String bio,
        String avatarUrl,
        String country,
        String githubUrl,
        String linkedinUrl,
        int totalXp,
        int currentLevel,
        int currentStreak,
        int longestStreak,
        int totalProblemsSolved,
        int totalAlgorithmsCompleted,
        int totalPracticeSessions,
        double acceptanceRate,
        LevelProgressDto levelProgress,
        StreakStatusDto streakStatus,
        long achievementsUnlocked,
        long badgesEarned,
        String createdAt
) {}
