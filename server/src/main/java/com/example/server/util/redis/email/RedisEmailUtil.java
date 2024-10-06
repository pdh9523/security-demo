package com.example.server.util.redis.email;

import com.example.server.util.redis.RedisUtil;
import com.example.server.util.redis.email.dto.EmailAuthCheckDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
@RequiredArgsConstructor
public class RedisEmailUtil {
    @Value("${spring.mail.username}")
    private String hostEmail;
    private final RedisUtil redisUtil;
    private final JavaMailSender mailSender;
    private static final SecureRandom secureRandom = new SecureRandom();

    private static final String REDIS_EMAIL_PREFIX = "auth:EmailAuthCode: ";
    private static final Long REDIS_EMAIL_DURATION = 60L * 5;       // 60sec * 5min

    /**
     * 임의의 6자리 숫자를 인증코드로 하여 레디스에 저장
     * @param email 레디스 저장 시 키로 사용할 이메일
     * @return 인증코드
     */
    private String createAuthCode(String email) {
        String authCode = String.valueOf(secureRandom.nextInt(900000) + 100000);
        redisUtil.setDataWithExpire(REDIS_EMAIL_PREFIX+email, authCode, REDIS_EMAIL_DURATION);
        return authCode;
    }

    /**
     * 이메일과 인증코드를 받고, 이를 레디스에 저장된 값과 비교 검증
     * @param emailAuthCheckDto email, authCode
     * @return 레디스에 저장된 값과 같다면 true, 아니면 false
     */
    public Boolean checkAuthCode(EmailAuthCheckDto emailAuthCheckDto) {
        String redisKey = REDIS_EMAIL_PREFIX+emailAuthCheckDto.email();
        if (emailAuthCheckDto.authCode().equals(redisUtil.getData(redisKey))) {
            redisUtil.deleteData(redisKey);
            return true;
        }
        return false;
    }

    /**
     * 이메일을 통해 인증코드를 발송
     * @param email 발송할 이메일
     * @throws MessagingException 이메일이 올바르게 설정되지 않은 경우
     */
    public void sendEmail(String email) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "utf-8");
        String authCode = createAuthCode(email);
        // TODO: 메일 템플릿 정하기
        String title = "서비스 인증코드";
        String content = "인증번호는 [" + authCode + " ]입니다";

        messageHelper.setFrom(hostEmail);
        messageHelper.setTo(email);
        messageHelper.setSubject(title);
        messageHelper.setText(content, true);
        mailSender.send(message);
    }
}
