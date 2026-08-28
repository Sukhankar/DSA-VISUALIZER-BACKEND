package com.codeloom.dsa.problem.dto;

public record ProblemUserStatsResponse(
        long totalSolved,
        long easySolved,
        long mediumSolved,
        long hardSolved,
        long totalSubmissions,
        long acceptedSubmissions,
        double acceptanceRate
) {
}
