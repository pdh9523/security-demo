package com.example.server.config.security.handler;

import com.example.server.config.security.principal.SecurityPrincipal;
import com.example.server.util.cookie.CookieUtil;
import com.example.server.util.token.TokenType;
import com.example.server.util.token.TokenUtil;
import com.example.server.util.token.dto.TokenResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final TokenUtil tokenUtil;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        SecurityPrincipal securityPrincipal = (SecurityPrincipal) authentication.getPrincipal();

        TokenResponseDto tokenResponseDto = tokenUtil.getToken(securityPrincipal.getEmail());

        // 응답 설정
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        // JSON 객체를 만들기 위해 Map으로 데이터 구성
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put(TokenType.accessToken.toString(), tokenResponseDto.accessToken()); // access_token 추가
        responseBody.put(TokenType.refreshToken.toString(), tokenResponseDto.refreshToken()); // refresh_token 추가

        // ObjectMapper로 Map 객체를 JSON 형식으로 변환 후 응답으로 전송
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(response.getOutputStream(), responseBody);
    }
}
