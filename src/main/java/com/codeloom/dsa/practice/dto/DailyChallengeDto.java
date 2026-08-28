package com.codeloom.dsa.practice.dto;

import com.codeloom.dsa.problem.dto.ProblemSummaryResponse;

import java.time.LocalDate;
import java.util.UUID;

public record DailyChallengeDto(
        UUID id,
        LocalDate challengeDate,
        ProblemSummaryResponse problem,
        int bonusXp,
        boolean completed,
        String status
) {}
