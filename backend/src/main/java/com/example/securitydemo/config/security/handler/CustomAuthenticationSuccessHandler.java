package com.example.securitydemo.config.security.handler;

import com.example.securitydemo.config.security.principal.SecurityPrincipal;
import com.example.securitydemo.domain.user.dto.UserResponseDto;
import com.example.securitydemo.domain.user.entity.User;
import com.example.securitydemo.util.token.TokenUtil;
import com.example.securitydemo.util.token.dto.TokenResponseDto;
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
        tokenUtil.pushTokenOnCookie(response, tokenResponseDto);

        // 응답 설정
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        // JSON 객체를 만들기 위해 Map으로 데이터 구성
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("access_token", tokenResponseDto.accessToken()); // access_token 추가
        responseBody.put("refresh_token", tokenResponseDto.refreshToken()); // refresh_token 추가

        // ObjectMapper로 Map 객체를 JSON 형식으로 변환 후 응답으로 전송
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(response.getOutputStream(), responseBody);
    }
}
