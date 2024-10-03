package com.example.securitydemo.config.oauth2.handler;

import com.example.securitydemo.config.oauth2.principal.OAuth2UserPrincipal;
import com.example.securitydemo.config.oauth2.repository.OAuth2Repository;
import com.example.securitydemo.domain.user.entity.User;
import com.example.securitydemo.domain.user.repository.UserRepository;
import com.example.securitydemo.util.cookie.CookieUtil;
import com.example.securitydemo.util.token.TokenUtil;
import com.example.securitydemo.util.token.dto.TokenResponseDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Optional;

import static com.example.securitydemo.config.oauth2.repository.OAuth2Repository.MODE_PARAM_COOKIE_NAME;
import static com.example.securitydemo.config.oauth2.repository.OAuth2Repository.REDIRECT_URI_PARAM_COOKIE_NAME;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenUtil tokenUtil;
    private final PasswordEncoder passwordEncoder;
    private final OAuth2Repository oAuth2Repository;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        String targetURL = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            logger.debug("이미 처리된 요청입니다. " + targetURL);
        }

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetURL);
    }

    @Override
    protected String determineTargetUrl(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {

        String redirectURI = CookieUtil.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue)
                .orElse(getDefaultTargetUrl());

        String mode = CookieUtil.getCookie(request, MODE_PARAM_COOKIE_NAME)
                .map(Cookie::getValue)
                .orElse("");

        OAuth2UserPrincipal principal = getOAuth2UserPrincipal(authentication);

        if (mode.equalsIgnoreCase("login")) {
            User user = userRepository.findByEmail(principal.getEmail())
                    .orElseGet(() ->
                            userRepository.save(User.createUser(
                                    principal.getEmail(),
                                    principal.getUserInfo().getAccessToken(),
                                    principal.getNickname(),
                                    passwordEncoder
                            )));
            System.out.println(principal.getEmail());
            TokenResponseDto tokenResponseDto = tokenUtil.getToken(principal.getEmail());

            user.setRefreshToken(tokenUtil.hashToken(tokenResponseDto.refreshToken()));
            userRepository.save(user);

            CookieUtil.pushTokenOnCookie(response, tokenResponseDto);

            return UriComponentsBuilder.fromUriString(redirectURI)
                    .build().toUriString();
        }

        return UriComponentsBuilder.fromUriString(redirectURI)
                .queryParam("error", "Login failed")
                .build().toUriString();
    }


    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        oAuth2Repository.removeAuthorizationRequestCookies(response);
    }

    private OAuth2UserPrincipal getOAuth2UserPrincipal(Authentication authentication) {
        Object principal = authentication.getPrincipal();

        if (principal instanceof OAuth2UserPrincipal) {
            return (OAuth2UserPrincipal) principal;
        }
        return null;
    }
}
