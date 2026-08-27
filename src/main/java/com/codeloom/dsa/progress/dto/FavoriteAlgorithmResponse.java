package com.codeloom.dsa.progress.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record FavoriteAlgorithmResponse(
        UUID algorithmId,
        String name,
        String slug,
        String difficulty,
        String category,
        OffsetDateTime createdAt
) {
}
