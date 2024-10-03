package com.example.securitydemo.config.oauth2;

import com.example.securitydemo.config.oauth2.principal.OAuth2UserPrincipal;
import com.example.securitydemo.config.oauth2.userInfo.OAuth2UserInfo;
import com.example.securitydemo.config.oauth2.userInfo.OAuth2UserInfoFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OAuth2Service extends DefaultOAuth2UserService {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info(oAuth2User.toString());
        try {
            return processOAuth2User(userRequest, oAuth2User);
        } catch (OAuth2AuthenticationException e) {
            throw e;
        } catch(Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(
            OAuth2UserRequest userRequest,
            OAuth2User oAuth2User
    ) {
        String registeredId = userRequest.getClientRegistration().getRegistrationId();

        String accessToken = userRequest.getAccessToken().getTokenValue();

        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
                registeredId, accessToken, oAuth2User.getAttributes()
        );

        return new OAuth2UserPrincipal(userInfo);
    }
}
