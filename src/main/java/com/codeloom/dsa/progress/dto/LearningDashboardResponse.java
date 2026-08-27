package com.codeloom.dsa.progress.dto;

import java.util.List;

public record LearningDashboardResponse(
        long totalAlgorithms,
        long startedAlgorithms,
        long completedAlgorithms,
        long favoriteAlgorithms,
        double completionPercentage,
        List<ProgressResponse> recentProgress
) {
}
