package com.example.server.domain.user.service;

import com.example.server.domain.user.dto.UserRequestDto;
import com.example.server.domain.user.dto.UserResponseDto;
import com.example.server.domain.user.entity.User;
import com.example.server.domain.user.repository.UserRepository;
import com.example.server.util.redis.email.RedisEmailUtil;
import com.example.server.util.redis.email.dto.EmailAuthCheckDto;
import com.example.server.util.token.TokenUtil;
import com.example.server.util.token.dto.RefreshTokenRequestDto;
import com.example.server.util.token.dto.TokenResponseDto;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final TokenUtil tokenUtil;
    private final RedisEmailUtil redisEmailUtil;
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

    @Override
    public UserResponseDto getMyInfo(User user) {
        return UserResponseDto.from(user);
    }

    @Override
    public void sendEmail(String email) throws MessagingException {
        redisEmailUtil.sendEmail(email);
    }

    @Override
    public Boolean verifyEmail(EmailAuthCheckDto emailAuthCheckDto) {
        return redisEmailUtil.checkAuthCode(emailAuthCheckDto);
    }

    @Override
    public TokenResponseDto tokenRefresh(RefreshTokenRequestDto refreshTokenRequestDto) {
        return tokenUtil.tokenRefresh(refreshTokenRequestDto);
    }
}
