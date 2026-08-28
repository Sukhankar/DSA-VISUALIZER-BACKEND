package com.codeloom.dsa.problem.dto;

import com.codeloom.dsa.algorithm.dto.RelatedAlgorithmSummary;
import com.codeloom.dsa.algorithm.entity.Difficulty;

import java.util.List;
import java.util.UUID;

public record ProblemDetailResponse(
        UUID id,
        String title,
        String slug,
        Difficulty difficulty,
        String description,
        String constraints,
        String inputFormat,
        String outputFormat,
        String hints,
        String solutionExplanation,
        String categoryName,
        String categorySlug,
        List<String> tags,
        List<ProblemExampleResponse> examples,
        List<RelatedAlgorithmSummary> relatedAlgorithms
) {
}
