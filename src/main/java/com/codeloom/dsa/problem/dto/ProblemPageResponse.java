package com.codeloom.dsa.problem.dto;

import java.util.List;

public record ProblemPageResponse(
        List<ProblemSummaryResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {
}
