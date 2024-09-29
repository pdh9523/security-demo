package com.example.securitydemo.domain.loginCredential.dto;

public record LoginCredentialResponseDto(
        Long id,
        String email,
        String password
) {
}
