package com.example.server.config.oauth2.userInfo;

import com.example.server.config.oauth2.OAuth2Provider;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(
            String registrationId, String accessToken, Map<String, Object> attributes
    ) {
        if (registrationId.equals(OAuth2Provider.KAKAO.getRegistrationId())) {
            return new KakaoOAuth2UserInfo(accessToken, attributes);
        }
        // 제공자(google, naver, 등) 추가 파트
        throw new RuntimeException("Login with " + registrationId + " is not supported");
    }
}
