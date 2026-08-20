package com.codeloom.dsa.algorithm.dto;

import com.codeloom.dsa.algorithm.entity.Difficulty;

import java.util.UUID;

public record AlgorithmResponse(
        UUID id,
        String name,
        String slug,
        String description,
        Difficulty difficulty,
        String timeComplexity,
        String spaceComplexity,
        String categoryName,
        String categorySlug
) {
}