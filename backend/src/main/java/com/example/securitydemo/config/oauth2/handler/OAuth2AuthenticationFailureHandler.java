package com.example.securitydemo.config.oauth2.handler;

import com.example.securitydemo.config.oauth2.repository.OAuth2Repository;
import com.example.securitydemo.util.cookie.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

import static com.example.securitydemo.config.oauth2.repository.OAuth2Repository.REDIRECT_URI_PARAM_COOKIE_NAME;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final OAuth2Repository oAuth2Repository;

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException {
        log.info("소셜 인증 실패");

        String redirectURI = CookieUtil.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue)
                .orElse("/");

        redirectURI = UriComponentsBuilder.fromUriString(redirectURI)
                .queryParam("error", exception.getLocalizedMessage())
                .build().toUriString();

        oAuth2Repository.removeAuthorizationRequestCookies(response);

        getRedirectStrategy().sendRedirect(request, response, redirectURI);
    }
}
