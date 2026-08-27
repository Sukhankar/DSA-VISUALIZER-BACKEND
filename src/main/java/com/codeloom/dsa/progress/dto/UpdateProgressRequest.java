package com.codeloom.dsa.progress.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateProgressRequest(
        @NotNull(message = "Progress percentage is required")
        @Min(value = 0, message = "Progress percentage must be at least 0")
        @Max(value = 100, message = "Progress percentage must not exceed 100")
        Integer progressPercentage,

        Integer lastStep
) {
}
