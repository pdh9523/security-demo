package com.example.server.util.token;

import com.example.server.config.security.principal.SecurityPrincipal;
import com.example.server.domain.loginCredential.entity.LoginCredential;
import com.example.server.domain.loginCredential.repository.LoginCredentialRepository;
import com.example.server.domain.loginCredential.service.LoginCredentialService;
import com.example.server.util.redis.RedisUtil;
import com.example.server.util.token.dto.AccessTokenRequestDto;
import com.example.server.util.token.dto.RefreshTokenRequestDto;
import com.example.server.util.token.dto.TokenResponseDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;

import static com.example.server.util.redis.RedisUtil.REDIS_ACCESS_TOKEN_PREFIX;

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
     * <p>엑세스 토큰용</p>
     * 토큰을 복호화 해서 유효한지 확인
     * @param accessTokenRequestDto 엑세스 토큰과 이메일(아이디)
     * @return 토큰이 유효한지에 대한 boolean 값
     */
    public boolean isTokenValid(AccessTokenRequestDto accessTokenRequestDto) {
        String accessToken = accessTokenRequestDto.accessToken();
        String email = accessTokenRequestDto.email();
        if (!redisUtil.getData(REDIS_ACCESS_TOKEN_PREFIX+email).equals(accessToken)) {
            logger.error("토큰이 만료되었거나, 유효하지 않은 토큰");
            return false;
        }
        return parseToken(accessToken);
    }

    /**
     * <p>리프레시 토큰용</p>
     * 토큰을 복호화 해서 유효한지 확인
     * @param refreshTokenRequestDto 리프레시 토큰과 이메일(아이디)
     * @return 토큰이 유효한지에 대한 boolean 값
     */
    public boolean isTokenValid(RefreshTokenRequestDto refreshTokenRequestDto) {
        String refreshToken = refreshTokenRequestDto.refreshToken();
        String email = refreshTokenRequestDto.email();
        if (loginCredentialRepository.existsByRefreshTokenAndEmail(refreshToken, email)) {
            logger.error("유효하지 않은 토큰");
            return false;
        }
        return parseToken(refreshToken);
    }

    /**
     * 토큰에서 사용자 이름을 추출
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
     * 토큰 리프레시
     * @param refreshTokenRequestDto  리프레시 토큰
     * @throws RuntimeException 리프레시토큰도 만료되었거나, 리프레시 토큰을 통해 유저를 찾지 못하는 경우
     */
    @Transactional
    public TokenResponseDto tokenRefresh(
            RefreshTokenRequestDto refreshTokenRequestDto
    ) throws RuntimeException {
        if (!isTokenValid(refreshTokenRequestDto)) {
            throw new RuntimeException("토큰이 유효하지 않습니다.");
        }
        return getToken(refreshTokenRequestDto.email());
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
            redisUtil.setDataWithExpire(REDIS_ACCESS_TOKEN_PREFIX+email, accessToken, TokenType.accessToken.getExpireTime());

            LoginCredential loginCredential = loginCredentialRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("존재 하지 않는 회원입니다."));

            loginCredential.setRefreshToken(hashToken(refreshToken));
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

    public String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(token.getBytes());
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("해싱 중 에러가 발생했습니다.");
        }
    }


    private boolean parseToken(String token) {
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
}

