package com.codeloom.dsa.analytics.dto;

import java.util.List;

public record GamificationSummaryDto(
        int level,
        int totalXp,
        LevelProgressDto levelProgress,
        int currentStreak,
        int longestStreak,
        int totalProblemsSolved,
        int totalAlgorithmsCompleted,
        int totalPracticeSessions,
        long achievementsUnlocked,
        long totalAchievements,
        long badgesEarned,
        long totalBadges,
        List<UserActivityDto> recentActivity
) {}
