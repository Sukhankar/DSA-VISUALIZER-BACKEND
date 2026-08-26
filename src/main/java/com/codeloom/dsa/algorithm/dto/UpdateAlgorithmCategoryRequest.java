package com.codeloom.dsa.algorithm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateAlgorithmCategoryRequest(

        @NotBlank(message = "Category name is required")
        @Size(max = 100, message = "Category name must not exceed 100 characters")
        String name,

        @NotBlank(message = "Category slug is required")
        @Size(max = 100, message = "Category slug must not exceed 100 characters")
        @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$", message = "Slug must be lowercase kebab-case")
        String slug,

        @Size(max = 500, message = "Description must not exceed 500 characters")
        String description
) {
}
