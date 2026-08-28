package com.codeloom.dsa.analytics.dto;

import java.time.OffsetDateTime;

public record BadgeDto(
        String code,
        String name,
        String description,
        String iconName,
        String category,
        int xpReward,
        boolean unlocked,
        OffsetDateTime unlockedAt
) {}
