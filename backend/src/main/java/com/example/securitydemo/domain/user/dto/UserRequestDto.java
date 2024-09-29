package com.example.securitydemo.domain.user.dto;

public record UserRequestDto(
        String email,
        String password,
        String nickname
) {

}
