package com.codeloom.dsa.analytics.dto;

import java.util.UUID;

public record BadgeItemDto(
        UUID id,
        String code,
        String name,
        String description,
        String iconName,
        String category,
        String rarity,
        int xpReward,
        boolean isEarned,
        String earnedAt
) {}
