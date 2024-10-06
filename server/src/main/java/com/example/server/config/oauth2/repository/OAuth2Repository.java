package com.example.server.config.oauth2.repository;

import com.example.server.util.cookie.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
public class OAuth2Repository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    public static final String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";
    public static final String REDIRECT_URI_PARAM_COOKIE_NAME = "redirect_uri";
    public static final String MODE_PARAM_COOKIE_NAME = "mode";

    private static final Long COOKIE_EXPIRE_SECONDS = 60 * 3L;  // 60sec * 3min

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(
            HttpServletRequest request
    ) {
        log.info("loadAuthorizationRequest");
        return CookieUtil.getCookie(request, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
                .map(cookie -> CookieUtil.deserialize(cookie, OAuth2AuthorizationRequest.class))
                .orElse(null);
    }

    @Override
    public void saveAuthorizationRequest(
            OAuth2AuthorizationRequest authorizationRequest,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        // 요청이 더 이상 없는 경우 cookie 삭제
        log.info("saveAuthorizationRequest");
        if (authorizationRequest == null) {
            removeAuthorizationRequestCookies(response);
            return;
        }

        // 쿠키에 리다이렉트 URI, MODE, REQUEST COOKIE 정보 추가
        CookieUtil.addCookie(
                response,
                OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME,
                CookieUtil.serialize(authorizationRequest),
                COOKIE_EXPIRE_SECONDS
        );

        String redirectUriAfterLogin = request.getParameter(REDIRECT_URI_PARAM_COOKIE_NAME);

        if (StringUtils.hasText(redirectUriAfterLogin)) {
            CookieUtil.addCookie(
                    response,
                    REDIRECT_URI_PARAM_COOKIE_NAME,
                    redirectUriAfterLogin,
                    COOKIE_EXPIRE_SECONDS
            );
        }

        String mode = request.getParameter(MODE_PARAM_COOKIE_NAME);
        if (StringUtils.hasText(mode)) {
            CookieUtil.addCookie(
                    response,
                    MODE_PARAM_COOKIE_NAME,
                    mode,
                    COOKIE_EXPIRE_SECONDS
            );
        }
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        return this.loadAuthorizationRequest(request);
    }

    public void removeAuthorizationRequestCookies(
            HttpServletResponse response
    ) {
        log.info("removeAuthorizationRequestCookies");
        String[] cookieNames = {
                OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME,
                REDIRECT_URI_PARAM_COOKIE_NAME,
                MODE_PARAM_COOKIE_NAME
        };

        for (String cookieName : cookieNames) {
            CookieUtil.deleteCookie(response, cookieName);
        }
    }

}
