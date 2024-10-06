package com.example.server.config.security.filter;

import com.example.server.config.security.WhiteListConfig;
import com.example.server.util.cookie.CookieUtil;
import com.example.server.util.token.TokenType;
import com.example.server.util.token.TokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;


@Component
@Slf4j
@RequiredArgsConstructor
public class TokenAuthorizationFilter extends OncePerRequestFilter {

    private final TokenUtil tokenUtil;
    private final WhiteListConfig whiteList;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        log.info("Token Filter 요청 주소: {}", request.getRequestURI());

        if (Arrays.stream(whiteList.getWhiteList())
                .anyMatch(whiteList -> new AntPathRequestMatcher(whiteList).matches(request))) {
            log.info("JWT FILTER PASS BY WHITELIST");
            filterChain.doFilter(request, response);
            return;
        }
        
    
        if (Arrays.stream(whiteList.getWhiteListForSwagger())
                .anyMatch(whiteList -> new AntPathRequestMatcher(whiteList).matches(request))) {
            log.info("JWT FILTER PASS BY WHITELIST");
            filterChain.doFilter(request, response);
            return;
        }
        
        if (request.getMethod().equalsIgnoreCase("GET")) {
            if (Arrays.stream(whiteList.getWhiteListForGet())
                    .anyMatch(whiteList -> new AntPathRequestMatcher(whiteList).matches(request))) {
                log.info("JWT FILTER PASS BY WHITELIST");
                filterChain.doFilter(request, response);
                return;
            }
        }


        if (request.getMethod().equalsIgnoreCase("OPTION")) {
            response.setStatus(HttpServletResponse.SC_OK);
            filterChain.doFilter(request, response);
            return;
        }
        // TODO: 토큰이 없을 수 있음. 이거 예외 처리 해야함

        // 쿠키에서 토큰 추출
        String accessToken = CookieUtil.extractToken(request, TokenType.accessToken);
        String refreshToken = CookieUtil.extractToken(request, TokenType.refreshToken);

        // 1. 토큰이 제대로 추출된 경우
        if (accessToken != null && refreshToken != null) {
            // 1-1. 엑세스 토큰이 유효한 경우
            if (tokenUtil.isTokenValid(accessToken, tokenUtil.getEmailFromToken(accessToken))) {
                // 토큰을 통해 인증정보를 저장하고, 다음 필터로 이동
                tokenUtil.authenticateWithToken(accessToken);
                filterChain.doFilter(request, response);
            }

            // 1-2. 엑세스 토큰이 유효하지 않은 경우
            else {
                // 1-2-1.근데 리프레시 토큰은 유효한 경우
                if (tokenUtil.isTokenValid(refreshToken, tokenUtil.getEmailFromToken(refreshToken))) {
                    // 리프레시 토큰을 통해 리프레시 한 후
                    tokenUtil.tokenRefresh(response, refreshToken);
                    // 리프레시 토큰을 통해 인증 정보를 저장하고, 다음 필터로 이동한다.
                    tokenUtil.authenticateWithToken(refreshToken);
                    filterChain.doFilter(request, response);
                }
                // 1-2-2. 리프레시 토큰도 유효하지 않은 경우
                else {
                    // 토큰을 삭제
                    tokenUtil.deleteTokenOnCookie(response);
                    throw new RuntimeException("토큰이 유효하지 않습니다.");
                }
            }
            // 토큰이 제대로 추출되지 않은 경우
        } else {
            tokenUtil.deleteTokenOnCookie(response);
            throw new RuntimeException("토큰이 정상적으로 추출되지 않았습니다.");
        }
    }
}
