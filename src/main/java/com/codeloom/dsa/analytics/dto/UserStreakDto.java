package com.codeloom.dsa.analytics.dto;

import java.time.LocalDate;

public record UserStreakDto(
        int currentStreak,
        int longestStreak,
        LocalDate lastActivityDate,
        int streakFreezeCount
) {}
