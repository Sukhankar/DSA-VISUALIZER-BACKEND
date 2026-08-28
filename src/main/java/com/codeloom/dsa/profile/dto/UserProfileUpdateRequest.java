package com.codeloom.dsa.profile.dto;

import jakarta.validation.constraints.Size;

public record UserProfileUpdateRequest(
        @Size(max = 100, message = "Display name cannot exceed 100 characters")
        String displayName,

        @Size(max = 500, message = "Bio cannot exceed 500 characters")
        String bio,

        String avatarUrl,

        @Size(max = 50, message = "Country cannot exceed 50 characters")
        String country,

        String githubUrl,

        String linkedinUrl
) {}
