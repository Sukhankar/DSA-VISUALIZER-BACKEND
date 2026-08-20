package com.codeloom.dsa.auth.dto;

public record LoginResponse(
        String accessToken,
        String tokenType
) {
}