package com.example.securitydemo.domain.user.service;

import com.example.securitydemo.domain.user.dto.UserRequestDto;
import com.example.securitydemo.domain.user.dto.UserResponseDto;
import com.example.securitydemo.domain.user.entity.User;

public interface UserService {

    UserResponseDto createUser(UserRequestDto userRequestDto);

    UserResponseDto getMyInfo(User user);
}
