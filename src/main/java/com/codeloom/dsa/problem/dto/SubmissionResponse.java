package com.codeloom.dsa.problem.dto;

import com.codeloom.dsa.problem.entity.SubmissionStatus;
import com.codeloom.dsa.problem.entity.SubmissionVerdict;

import java.time.OffsetDateTime;
import java.util.UUID;

public record SubmissionResponse(
        UUID id,
        String problemSlug,
        String problemTitle,
        String language,
        String sourceCode,
        SubmissionStatus status,
        SubmissionVerdict verdict,
        Integer executionTimeMs,
        Integer memoryUsedKb,
        int totalTests,
        int passedTests,
        OffsetDateTime submittedAt,
        OffsetDateTime completedAt
) {
}
