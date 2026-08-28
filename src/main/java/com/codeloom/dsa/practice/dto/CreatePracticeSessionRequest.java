package com.codeloom.dsa.practice.dto;

import com.codeloom.dsa.algorithm.entity.Difficulty;
import com.codeloom.dsa.practice.entity.PracticeMode;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreatePracticeSessionRequest(
        @NotNull(message = "Practice mode is required")
        PracticeMode mode,

        Difficulty difficulty,
        UUID categoryId,
        Integer timeLimitSeconds
) {}
