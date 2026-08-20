package com.codeloom.dsa.algorithm.dto;

import java.util.UUID;

public record AlgorithmCategoryResponse(
        UUID id,
        String name,
        String slug,
        String description
) {
}