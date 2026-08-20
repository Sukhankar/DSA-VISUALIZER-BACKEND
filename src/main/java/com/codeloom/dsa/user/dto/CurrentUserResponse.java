package com.codeloom.dsa.user.dto;

import java.util.UUID;
import java.util.Set;

public record CurrentUserResponse(
        UUID id,
        String email,
        String username,
        Set<String> roles
) {
}