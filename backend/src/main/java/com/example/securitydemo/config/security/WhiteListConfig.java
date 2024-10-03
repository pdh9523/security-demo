package com.example.securitydemo.config.security;

import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
public class WhiteListConfig {
    private final String[] whiteListForSwagger = {
            // 스웨거 관련 엔드포인트
            "/v1/swagger-ui/**",
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/v1/swagger-ui/**",
            "/v3/api-docs/**",
            "/api-docs/**",
            "/error",
    };

    private final String[] whiteList = {
            // 회원가입 관련 엔드포인트
            "/api/user/register",
            // 로그인 관련 엔드포인트
            "/api/oauth2/authorization/kakao",
            "/api/oauth2/authorization/naver",
            "/api/login/oauth2/code/kakao",
            // 기본 설정 관련
            "/favicon.ico"
    };

    private final String[] whiteListForGet = {
            "/api/post/**"
    };
}
