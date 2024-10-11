package com.example.server.util.token.dto;

public record AccessTokenRequestDto(
        String accessToken,
        String email
) {
}
