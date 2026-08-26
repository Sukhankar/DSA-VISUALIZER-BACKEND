package com.codeloom.dsa.algorithm.dto;

import com.codeloom.dsa.algorithm.entity.Difficulty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateAlgorithmRequest(

        @NotBlank(message = "Name is required")
        @Size(max = 150, message = "Name must not exceed 150 characters")
        String name,

        @NotBlank(message = "Slug is required")
        @Size(max = 150, message = "Slug must not exceed 150 characters")
        String slug,

        @NotBlank(message = "Description is required")
        String description,

        @NotNull(message = "Difficulty is required")
        Difficulty difficulty,

        @NotBlank(message = "Time complexity is required")
        String timeComplexity,

        @NotBlank(message = "Space complexity is required")
        String spaceComplexity,

        @NotBlank(message = "Category slug is required")
        String categorySlug
) {
}
