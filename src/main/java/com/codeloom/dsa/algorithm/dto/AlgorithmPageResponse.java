package com.codeloom.dsa.algorithm.dto;

import java.util.List;

public record AlgorithmPageResponse(
        List<AlgorithmResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {
}