package com.codeloom.dsa.progress.dto;

import java.time.OffsetDateTime;

public record AlgorithmMasteryDto(
        String algorithmSlug,
        boolean mastered,
        OffsetDateTime masteredAt,
        int xpEarned,
        boolean newlyMastered
) {}
