package com.codeloom.dsa.progress.dto;

import com.codeloom.dsa.progress.entity.ProgressStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ProgressResponse(
        UUID algorithmId,
        String algorithmName,
        String algorithmSlug,
        ProgressStatus status,
        int progressPercentage,
        Integer lastStep,
        OffsetDateTime startedAt,
        OffsetDateTime completedAt,
        OffsetDateTime updatedAt
) {
}
