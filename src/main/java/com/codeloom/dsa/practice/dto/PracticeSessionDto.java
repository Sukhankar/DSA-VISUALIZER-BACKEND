package com.codeloom.dsa.practice.dto;

import com.codeloom.dsa.algorithm.entity.Difficulty;
import com.codeloom.dsa.practice.entity.PracticeMode;
import com.codeloom.dsa.practice.entity.SessionStatus;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record PracticeSessionDto(
        UUID id,
        PracticeMode mode,
        SessionStatus status,
        Difficulty difficulty,
        String categoryName,
        Integer timeLimitSeconds,
        int totalProblems,
        int solvedProblems,
        int score,
        int xpEarned,
        double accuracyPercentage,
        OffsetDateTime startedAt,
        OffsetDateTime completedAt,
        List<PracticeSessionProblemDto> problems
) {}
