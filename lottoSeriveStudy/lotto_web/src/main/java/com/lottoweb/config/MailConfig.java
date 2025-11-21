package com.lottoweb.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * 이메일 설정 클래스
 * 이메일 설정이 있을 때만 JavaMailSender 빈을 생성합니다.
 */
@Configuration
public class MailConfig {

    /**
     * 이메일 호스트가 설정되어 있을 때만 JavaMailSender 빈 생성
     */
    @Bean
    @ConditionalOnProperty(name = "spring.mail.host")
    public JavaMailSender javaMailSender() {
        return new JavaMailSenderImpl();
    }
}

