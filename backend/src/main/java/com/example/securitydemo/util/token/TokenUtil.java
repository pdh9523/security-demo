package com.example.securitydemo.util.token;

import com.example.securitydemo.config.security.principal.SecurityPrincipal;
import com.example.securitydemo.domain.loginCredential.entity.LoginCredential;
import com.example.securitydemo.domain.loginCredential.repository.LoginCredentialRepository;
import com.example.securitydemo.domain.loginCredential.service.LoginCredentialService;
import com.example.securitydemo.util.cookie.CookieUtil;
import com.example.securitydemo.util.redis.RedisUtil;
import com.example.securitydemo.util.token.dto.TokenResponseDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@RequiredArgsConstructor
@Component
public class TokenUtil {

    private final RedisUtil redisUtil;
    private final LoginCredentialRepository loginCredentialRepository;
    private final LoginCredentialService loginCredentialService;

    @Value("${spring.jwt.salt}")
    private String salt;
    private static final Logger logger = LoggerFactory.getLogger(TokenUtil.class);

    /**
     * 암호화 해시 키 생성
     * @return 해시 키
     */
    private Key getKey() {
        return Keys.hmacShaKeyFor(salt.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 유저의 이메일을 받아 토큰을 발급
     * @param email 유저의 이메일
     * @param tokenType 발급 받을 토큰 타입 (accessToken, refreshToken 중 하나)
     * @return accessToken, refreshToken 중 하나
     */
    public String createToken(String email, TokenType tokenType) {
        long expireTime = tokenType.getExpireTime() * 1000;

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + expireTime))
                .signWith(getKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * 토큰을 복호화 해서 유효한지 확인
     * @param token 현재 가지고 있는 토큰
     * @return 토큰이 유효한지에 대한 boolean 값
     */
    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token", e);
        } catch (ExpiredJwtException e) {
            logger.error("Expired JWT token", e);
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT token", e);
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty", e);
        }
        return false;
    }

    /**
     * 유효한 토큰에서 사용자 이름을 추출
     * @param token 현재 가지고 있는 토큰
     * @return 토큰에 저장된 email 값
     */
    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * 쿠키에서 토큰을 삭제
     * @param response 서버에 응답이 나갈 때 동작
     */
    public void deleteTokenOnCookie(
            @NonNull HttpServletResponse response
    ) {
        CookieUtil.deleteCookie(response, "refresh_token");
        CookieUtil.deleteCookie(response, "access_token");
    }

    /**
     * 토큰 리프레시
     * @param response  서버에 응답이 나갈 때 동작
     * @param refreshToken  리프레시 토큰
     * @throws RuntimeException 리프레시 토큰을 통해 유저를 찾지 못하는 경우
     */
    @Transactional
    public void tokenRefresh(
            @NonNull HttpServletResponse response,
            String refreshToken
    ) throws RuntimeException {
        LoginCredential loginCredential = loginCredentialRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("토큰이 유효하지 않습니다."));

        // 토큰이 DB에 저장되어 있는것을 확인했으면,

        // 새로운 토큰을 발급
        String email = loginCredential.getEmail();
        TokenResponseDto tokenResponseDto = getToken(email);

        // 그리고 다시 response 에 담아 보내기
        CookieUtil.pushTokenOnCookie(response, tokenResponseDto);
    }

    /**
     * 토큰을 만들고, 레디스와 DB에 저장
     * @param email 유저의 이메일
     * @return 생성된 두 토큰을 반환
     */
    @Transactional
    public TokenResponseDto getToken(String email) {
        String accessToken = createToken(email, TokenType.accessToken);
        String refreshToken = createToken(email, TokenType.refreshToken);

        try {
            redisUtil.setDataWithExpire("access_token", accessToken, TokenType.accessToken.getExpireTime());

            LoginCredential loginCredential = loginCredentialRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("존재 하지 않는 회원입니다."));

            loginCredential.setRefreshToken(refreshToken);
            loginCredentialRepository.save(loginCredential);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return TokenResponseDto.of(accessToken, refreshToken);
    }

    /**
     * 토큰을 통해 인증을 받음
     * @param token 토큰
     * @throws RuntimeException 유효한 토큰이 아닌 경우
     */
    public void authenticateWithToken(String token) throws RuntimeException {
        String email = getEmailFromToken(token);
        if (email != null) {
            SecurityPrincipal securityPrincipal = loginCredentialService.loadUserByUsername(email);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    securityPrincipal,
                    null,
                    securityPrincipal.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        } else {
            throw new RuntimeException("인증 과정에서 문제가 발생했습니다.");
        }
    }
}

