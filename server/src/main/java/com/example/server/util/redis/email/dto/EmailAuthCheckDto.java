package com.example.server.util.redis.email.dto;

public record EmailAuthCheckDto(
        String email,
        String authCode
) {
}
