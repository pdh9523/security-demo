package com.example.server.config.oauth2.userInfo;


import com.example.server.config.oauth2.OAuth2Provider;

import java.util.Map;

public interface OAuth2UserInfo {
    OAuth2Provider getProvider();

    String getAccessToken();

    String getName();

    Map<String, Object> getAttributes();

    String getEmail();

    String getNickname();

    String getId();
}
