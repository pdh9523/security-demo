package com.example.server.config.security.handler;

import com.example.server.util.redis.RedisUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import static com.example.server.util.redis.RedisUtil.REDIS_EMAIL_PREFIX;

@RequiredArgsConstructor
@Component
public class CustomLogoutHandler implements LogoutHandler {

    private final RedisUtil redisUtil;

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        // 커스텀 로그아웃 핸들러를 통해 레디스에 저장된 엑세스 토큰 삭제
        String email = authentication.getName();
        redisUtil.deleteData(REDIS_EMAIL_PREFIX+email);
    }
}
