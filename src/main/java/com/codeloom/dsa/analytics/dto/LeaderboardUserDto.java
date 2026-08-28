package com.codeloom.dsa.analytics.dto;

public record LeaderboardUserDto(
        int rank,
        String username,
        int currentLevel,
        int totalXp,
        long problemsSolved
) {}
