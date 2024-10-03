package com.example.securitydemo.config.oauth2;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OAuth2Provider {
    NAVER("naver"),
    KAKAO("kakao");

    private final String registrationId;
}
