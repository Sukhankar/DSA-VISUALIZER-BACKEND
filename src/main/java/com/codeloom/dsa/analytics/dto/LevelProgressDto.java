package com.codeloom.dsa.analytics.dto;

public record LevelProgressDto(
        int currentLevel,
        int totalXp,
        int xpForCurrentLevel,
        int xpForNextLevel,
        int xpInCurrentLevel,
        int xpRemainingForNextLevel,
        double progressPercentage
) {}
