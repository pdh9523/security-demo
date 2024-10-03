package com.example.securitydemo.util.cookie;

import com.example.securitydemo.util.token.TokenType;
import com.example.securitydemo.util.token.dto.TokenResponseDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.http.ResponseCookie;
import org.springframework.util.SerializationUtils;

import java.util.Base64;
import java.util.Optional;

public class CookieUtil {

    /**
     * 쿠키를 추출
     * @param request 서버에 요청이 들어왔을 때 동작
     * @param cookieName 추출하려는 쿠키의 키값
     * @return 추출된 쿠키의 밸류값
     */
    public static Optional<Cookie> getCookie(
            @NonNull HttpServletRequest request,
            String cookieName
    ) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie: cookies) {
                if (cookie.getName().equals(cookieName)) {
                    return Optional.of(cookie);
                }
            }
        }
        return Optional.empty();
    }

    /**
     * 쿠키에서 토큰을 추출
     * @param request 서버 요청을 받아 동작
     * @param tokenType accessToken/refreshToken 중 하나를 받음
     * @return 토큰
     */
    public static String extractToken(
            @NonNull HttpServletRequest request,
            TokenType tokenType
    ) {
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(tokenType.toString())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    /**
     * 쿠키를 삽입
     * @param response 서버에 응답이 나갈 때 동작
     * @param cookieName 삽입하려는 쿠키의 키값
     * @param cookieValue 삽입하려는 쿠키의 밸류값
     * @param maxAge 삽입하려는 쿠키의 유효기간
     */
    public static void addCookie(
            HttpServletResponse response,
            String cookieName,
            String cookieValue,
            Long maxAge
    ) {
        ResponseCookie cookie = ResponseCookie.from(cookieName, cookieValue)
                .httpOnly(true)
                .path("/")
                .maxAge(maxAge)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    /**
     * 특정 쿠키를 삭제
     * @param response 서버에 응답이 나갈 때 동작
     * @param cookieName 삭제하려는 쿠키의 키값
     */
    public static void deleteCookie(
            HttpServletResponse response,
            String cookieName
    ) {
         addCookie(response, cookieName, "", 0L);
    }


    public static String serialize(Object object) {
        return Base64.getUrlEncoder()
                .encodeToString(SerializationUtils.serialize(object));
    }

    public static <T> T deserialize(Cookie cookie, Class<T> tClass) {
        return tClass.cast(SerializationUtils.deserialize(
                Base64.getUrlDecoder().decode(cookie.getValue())));
    }

    /**
     * 토큰을 쿠키에 푸시
     * @param response  서버에 응답이 나갈 때 동작
     * @param tokenResponseDto 엑세스 토큰과 리프레시 토큰
     */
    public static void pushTokenOnCookie(
            @NonNull HttpServletResponse response,
            TokenResponseDto tokenResponseDto
    ) {
        addCookie(response, TokenType.accessToken.toString(), tokenResponseDto.accessToken(), TokenType.accessToken.getExpireTime());
        addCookie(response, TokenType.refreshToken.toString(), tokenResponseDto.refreshToken(), TokenType.refreshToken.getExpireTime());
    }
}
