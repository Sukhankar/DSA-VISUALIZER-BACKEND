package com.codeloom.dsa.practice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record SessionSubmitRequest(
        @NotNull(message = "Problem ID is required")
        UUID problemId,

        @NotBlank(message = "Language is required")
        String language,

        @NotBlank(message = "Code is required")
        String code
) {}
