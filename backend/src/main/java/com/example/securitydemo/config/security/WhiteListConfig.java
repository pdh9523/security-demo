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
            // 로그인 관련 엔드포인트
            "/user",
            "/user/login",
            "/oauth2/authorization/kakao",
            "/oauth2/authorization/naver",
            // 기본 설정 관련
            "/favicon.ico"
    };
}
