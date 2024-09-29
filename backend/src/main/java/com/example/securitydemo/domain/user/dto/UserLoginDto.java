package com.example.securitydemo.domain.user.dto;

public record UserLoginDto(
        String email,
        String password
) {
}
