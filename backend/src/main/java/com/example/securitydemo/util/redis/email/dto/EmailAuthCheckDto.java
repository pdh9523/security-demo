package com.example.securitydemo.util.redis.email.dto;

public record EmailAuthCheckDto(
        String email,
        String authCode
) {
}
