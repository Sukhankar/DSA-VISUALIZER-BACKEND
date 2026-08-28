package com.codeloom.dsa.algorithm.dto;

import com.codeloom.dsa.algorithm.entity.Difficulty;

import java.util.List;
import java.util.UUID;

public record AlgorithmDetailRichResponse(
        UUID id,
        String name,
        String slug,
        String description,
        String overview,
        String whenToUse,
        String advantages,
        String limitations,
        String constraints,
        Difficulty difficulty,
        String timeComplexity,
        String spaceComplexity,
        String categoryName,
        String categorySlug,
        List<AlgorithmExampleResponse> examples,
        List<AlgorithmImplementationResponse> implementations,
        List<RelatedAlgorithmSummary> relatedAlgorithms
) {
}
