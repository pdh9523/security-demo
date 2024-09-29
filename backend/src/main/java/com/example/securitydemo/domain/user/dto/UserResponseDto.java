package com.example.securitydemo.domain.user.dto;

import com.example.securitydemo.domain.user.entity.User;

public record UserResponseDto(
        Long id,
        String email,
        String nickname
) {
    public static UserResponseDto from(User user) {
        return new UserResponseDto(user.getId(), user.getEmail(), user.getNickname());
    }
}
