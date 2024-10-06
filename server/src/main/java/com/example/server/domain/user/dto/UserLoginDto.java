package com.example.server.domain.user.dto;

public record UserLoginDto(
        String email,
        String password
) {
}
