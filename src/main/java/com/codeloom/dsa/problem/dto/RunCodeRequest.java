package com.codeloom.dsa.problem.dto;

import jakarta.validation.constraints.NotBlank;

public record RunCodeRequest(
        @NotBlank(message = "Language is required")
        String language,

        @NotBlank(message = "Source code is required")
        String sourceCode
) {
}
