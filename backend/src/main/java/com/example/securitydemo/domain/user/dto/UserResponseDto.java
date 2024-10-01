package com.example.securitydemo.domain.user.dto;

import com.example.securitydemo.domain.user.entity.User;

import java.time.LocalDateTime;

public record UserResponseDto(
        Long id,
        String email,
        String nickname,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static UserResponseDto from(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
