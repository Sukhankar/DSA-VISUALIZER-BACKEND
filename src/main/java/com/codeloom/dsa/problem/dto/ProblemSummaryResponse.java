package com.codeloom.dsa.problem.dto;

import com.codeloom.dsa.algorithm.entity.Difficulty;

import java.util.List;
import java.util.UUID;

public record ProblemSummaryResponse(
        UUID id,
        String title,
        String slug,
        Difficulty difficulty,
        String categoryName,
        String categorySlug,
        List<String> tags
) {
}
