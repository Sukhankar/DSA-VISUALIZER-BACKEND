package com.codeloom.dsa.analytics.dto;

public record StreakStatusDto(
        int currentStreak,
        int longestStreak,
        String lastActivityDate,
        boolean isActiveToday,
        int streakFreezeCount,
        int nextMilestone,
        int daysUntilNextMilestone
) {}
