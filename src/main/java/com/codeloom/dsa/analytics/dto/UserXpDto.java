package com.codeloom.dsa.analytics.dto;

public record UserXpDto(
        int totalXp,
        int currentLevel,
        int xpForCurrentLevel,
        int xpForNextLevel,
        double levelProgressPercentage
) {}
