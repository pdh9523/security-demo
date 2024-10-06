package com.example.server.domain.loginCredential.dto;

public record LoginCredentialResponseDto(
        Long id,
        String email,
        String password
) {
}
