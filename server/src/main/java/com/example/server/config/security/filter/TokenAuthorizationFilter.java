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

        // 토큰 처리 방식 변경
        // accessToken 을 헤더에서 가져온 후,
        // 토큰이 유효한 경우 필터를 통과
        // 토큰이 유효하지 않은 경우 리프레시 해야한다는 응답을 반환
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new RuntimeException("인증 정보가 없습니다.");
        }

        String accessToken = authorizationHeader.substring(7); // 'Bearer ' 이후의 토큰 값만 추출
        String email = tokenUtil.getEmailFromToken(accessToken);
        // 토큰 검증 로직
        if (tokenUtil.isTokenValid(accessToken, email)) {
            tokenUtil.authenticateWithToken(accessToken);
            filterChain.doFilter(request, response);
        } else {
            throw new RuntimeException("토큰이 유효하지 않습니다.");
        }
    }
}
