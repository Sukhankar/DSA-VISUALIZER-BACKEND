package com.codeloom.dsa.algorithm.dto;

public record AlgorithmImplementationResponse(
        String language,
        String code,
        String explanation,
        int displayOrder
) {
}
