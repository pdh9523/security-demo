package com.example.server.domain.user.dto;

public record UserRequestDto(
        String email,
        String password,
        String nickname
) {

}
