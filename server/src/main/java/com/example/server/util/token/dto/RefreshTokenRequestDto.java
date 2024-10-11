package com.example.server.util.token.dto;

public record RefreshTokenRequestDto(
        String refreshToken,
        String email
) {
}
