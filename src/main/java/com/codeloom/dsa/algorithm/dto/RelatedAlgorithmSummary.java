package com.codeloom.dsa.algorithm.dto;

import com.codeloom.dsa.algorithm.entity.Difficulty;

import java.util.UUID;

public record RelatedAlgorithmSummary(
        UUID id,
        String name,
        String slug,
        Difficulty difficulty,
        String categoryName
) {
}
