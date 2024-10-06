package com.example.server.domain.user.service;

import com.example.server.domain.user.dto.UserRequestDto;
import com.example.server.domain.user.dto.UserResponseDto;
import com.example.server.domain.user.entity.User;
import com.example.server.util.redis.email.dto.EmailAuthCheckDto;
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

    /**
     * 인증을 위한 이메일 전송
     * @param email 인증코드를 받을 이메일
     * @throws MessagingException 이메일이 정상적으로 발송되지 않은 경우
     */
    void sendEmail(String email) throws MessagingException;

    /**
     * 인증코드 검증
     * @param emailAuthCheckDto email, authCode
     * @return 인증이 성공되었는지에 대한 여부
     */
    Boolean verifyEmail(EmailAuthCheckDto emailAuthCheckDto);
}
