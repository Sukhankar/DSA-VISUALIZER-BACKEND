package com.codeloom.dsa.analytics.dto;

import java.util.UUID;

public record UserActivityDto(
        UUID id,
        String activityType,
        String referenceType,
        String referenceId,
        int xpEarned,
        String metadata,
        String createdAt
) {}
