package com.codeloom.dsa.analytics.dto;

import java.util.UUID;

public record AchievementItemDto(
        UUID id,
        String code,
        String name,
        String description,
        String category,
        String icon,
        String rarity,
        int xpReward,
        String requirementType,
        int requirementValue,
        boolean isUnlocked,
        String unlockedAt,
        int currentProgress,
        double progressPercentage
) {}
