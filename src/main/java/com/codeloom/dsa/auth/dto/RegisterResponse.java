package com.codeloom.dsa.auth.dto;

import java.util.UUID;

public record RegisterResponse(
        UUID id,
        String email,
        String username
) {
}