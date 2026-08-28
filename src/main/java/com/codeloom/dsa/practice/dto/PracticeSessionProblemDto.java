package com.codeloom.dsa.practice.dto;

import com.codeloom.dsa.practice.entity.SessionProblemStatus;
import com.codeloom.dsa.problem.dto.ProblemSummaryResponse;

import java.time.OffsetDateTime;
import java.util.UUID;

public record PracticeSessionProblemDto(
        UUID id,
        int orderIndex,
        ProblemSummaryResponse problem,
        SessionProblemStatus status,
        UUID submissionId,
        OffsetDateTime solvedAt
) {}
