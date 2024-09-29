package com.example.securitydemo.domain.user.service;

import com.example.securitydemo.domain.user.dto.UserRequestDto;
import com.example.securitydemo.domain.user.dto.UserResponseDto;
import com.example.securitydemo.domain.user.entity.User;
import com.example.securitydemo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        User user = User.createUser(
                userRequestDto.email(),
                userRequestDto.password(),
                userRequestDto.nickname(),
                passwordEncoder
        );
        userRepository.save(user);

        return UserResponseDto.from(user);
    }

    public UserResponseDto getMyInfo(User user) {
        return UserResponseDto.from(user);
    }


}
