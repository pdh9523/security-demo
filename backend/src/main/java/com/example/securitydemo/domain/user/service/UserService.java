package com.example.securitydemo.domain.user.service;

import com.example.securitydemo.domain.user.dto.UserRequestDto;
import com.example.securitydemo.domain.user.dto.UserResponseDto;
import com.example.securitydemo.domain.user.entity.User;
import com.example.securitydemo.util.redis.email.dto.EmailAuthCheckDto;
import jakarta.mail.MessagingException;

public interface UserService {

    /**
     * 회원가입
     * @param userRequestDto email, password, nickname
     * @return pk, email, nickname, createdAt, updatedAt
     */
    UserResponseDto createUser(UserRequestDto userRequestDto);

    /**
     * 내 정보 보기
     * @param user user 객체 (AuthenticationPrincipal 어노테이션을 통해 자동 입력)
     * @return pk, email, nickname, createdAt, updatedAt
     */
    UserResponseDto getMyInfo(User user);

    void sendEmail(String email) throws MessagingException;

    Boolean verifyEmail(EmailAuthCheckDto emailAuthCheckDto);
}
