package com.example.securitydemo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfig {

    @Value("${spring.mail.username}")
    private String username;
    @Value("${spring.mail.password}")
    private String password;
    @Value("${spring.mail.host}")
    private String host;
    @Value("${spring.mail.port}")
    private int port;

    @Bean
    public JavaMailSender mailSender() {
        // 기본적인 설정(호스트, 포트, 사용자, 비밀번호 등)을 담당

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username); // 구글 아이디
        mailSender.setPassword(password); // 구글 비밀번호

        // 세부적인 설정(인증, 보안, 디버깅 등)을 담당
        Properties props = mailSender.getJavaMailProperties();
        // 전송 프로토콜을 smtp로 설정
        props.put("mail.transport.protocol", "smtp");
        // 이메일 전송 시 인증 활성화
        props.put("mail.smtp.auth", "true");
        // START TLS. TLS(Transport Layer Security)를 사용하여 보안 연결을 업그레이드
        props.put("mail.smtp.starttls.enable", "true");
        // SSL/TLS 에 대한 인증서를 신뢰하도록 설정
        props.put("mail.smtp.ssl.trust", host);
        // TLS의 최신 프로토콜인 v1.2를 사용하도록 설정
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        // 디버깅
        props.put("mail.debug", "true");

        return mailSender;
    }
}

