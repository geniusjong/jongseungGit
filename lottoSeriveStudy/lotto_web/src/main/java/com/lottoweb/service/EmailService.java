package com.lottoweb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;

/**
 * 이메일 발송 서비스
 */
@Service
public class EmailService {

    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username:}")
    private String fromEmail;
    
    @Value("${app.url:http://localhost:8080}")
    private String appUrl;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * 이메일 인증 메일 발송
     */
    public void sendVerificationEmail(String toEmail, String username, String token) throws MessagingException {
        String verificationUrl = appUrl + "/verify-email?token=" + token;
        
        // MimeMessage를 사용하여 UTF-8 인코딩 보장
        MimeMessage message = mailSender.createMimeMessage();
        
        // MimeMessageHelper를 UTF-8로 생성 (멀티파트)
        MimeMessageHelper helper = new MimeMessageHelper(message, false, StandardCharsets.UTF_8.name());
        
        // 수신자 설정
        helper.setTo(toEmail);
        
        // 제목을 UTF-8로 인코딩하여 설정 (MimeMessage 직접 사용)
        String subject = "[로또 번호 추천 서비스] 이메일 인증을 완료해주세요";
        message.setSubject(subject, StandardCharsets.UTF_8.name());
        
        // HTML 내용 생성
        String htmlContent = buildVerificationEmailContent(username, verificationUrl);
        
        // HTML 형식으로 이메일 본문 설정 (UTF-8 인코딩 보장)
        helper.setText(htmlContent, true);
        
        // Content-Type 명시적으로 설정 (UTF-8)
        message.setHeader("Content-Type", "text/html; charset=UTF-8");
        
        // 발신자 이메일이 설정되어 있으면 사용
        if (fromEmail != null && !fromEmail.isEmpty()) {
            helper.setFrom(fromEmail);
        }
        
        mailSender.send(message);
    }

    /**
     * 이메일 인증 메일 내용 생성 (HTML 형식)
     * 이메일 클라이언트 호환성을 위해 인라인 스타일과 테이블 기반 레이아웃 사용
     */
    private String buildVerificationEmailContent(String username, String verificationUrl) {
        return "<!DOCTYPE html>" +
               "<html>" +
               "<head>" +
               "<meta charset='UTF-8'>" +
               "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
               "</head>" +
               "<body style='margin: 0; padding: 0; font-family: \"Malgun Gothic\", \"맑은 고딕\", Arial, sans-serif; background-color: #f5f5f5;'>" +
               "<table role='presentation' cellspacing='0' cellpadding='0' border='0' width='100%' style='background-color: #f5f5f5;'>" +
               "<tr>" +
               "<td align='center' style='padding: 20px 0;'>" +
               "<table role='presentation' cellspacing='0' cellpadding='0' border='0' width='600' style='max-width: 600px; background-color: #ffffff; border-radius: 10px; overflow: hidden; box-shadow: 0 2px 4px rgba(0,0,0,0.1);'>" +
               // 헤더
               "<tr>" +
               "<td style='background-color: #7c6cff; padding: 30px; text-align: center;'>" +
               "<h1 style='margin: 0; color: #ffffff; font-size: 24px; font-weight: 600;'>로또 번호 추천 서비스</h1>" +
               "</td>" +
               "</tr>" +
               // 본문
               "<tr>" +
               "<td style='background-color: #f7f8fb; padding: 30px;'>" +
               "<p style='margin: 0 0 15px 0; color: #333333; font-size: 16px; line-height: 1.6;'>안녕하세요, <strong style='color: #7c6cff;'>" + username + "</strong>님!</p>" +
               "<p style='margin: 0 0 15px 0; color: #333333; font-size: 16px; line-height: 1.6;'>로또 번호 추천 서비스에 가입해주셔서 감사합니다.</p>" +
               "<p style='margin: 0 0 25px 0; color: #333333; font-size: 16px; line-height: 1.6;'>이메일 인증을 완료하시려면 아래 버튼을 클릭해주세요:</p>" +
               // 버튼 (테이블 기반, 인라인 스타일 - 이메일 클라이언트 호환성 최대화)
               "<table role='presentation' cellspacing='0' cellpadding='0' border='0' width='100%'>" +
               "<tr>" +
               "<td align='center' style='padding: 20px 0;'>" +
               "<table role='presentation' cellspacing='0' cellpadding='0' border='0' style='border-collapse: collapse;'>" +
               "<tr>" +
               "<td align='center' bgcolor='#7c6cff' style='background-color: #7c6cff; border-radius: 8px; padding: 0;'>" +
               "<a href='" + verificationUrl + "' style='display: block; padding: 15px 40px; background-color: #7c6cff; color: #ffffff; text-decoration: none; font-size: 16px; font-weight: 600; border-radius: 8px; border: 2px solid #7c6cff;'>이메일 인증하기</a>" +
               "</td>" +
               "</tr>" +
               "</table>" +
               "</td>" +
               "</tr>" +
               "</table>" +
               // 주의사항
               "<table role='presentation' cellspacing='0' cellpadding='0' border='0' width='100%' style='margin-top: 30px; padding-top: 20px; border-top: 1px solid #dddddd;'>" +
               "<tr>" +
               "<td>" +
               "<p style='margin: 0 0 10px 0; color: #666666; font-size: 14px; font-weight: 600;'><strong>주의사항:</strong></p>" +
               "<ul style='margin: 0; padding-left: 20px; color: #666666; font-size: 14px; line-height: 1.8;'>" +
               "<li style='margin-bottom: 5px;'>이 링크는 24시간 동안 유효합니다.</li>" +
               "<li style='margin-bottom: 5px;'>링크가 만료되면 다시 회원가입을 진행해주세요.</li>" +
               "<li style='margin-bottom: 5px;'>이 링크는 한 번만 사용할 수 있습니다.</li>" +
               "</ul>" +
               "</td>" +
               "</tr>" +
               "</table>" +
               "</td>" +
               "</tr>" +
               // 푸터
               "<tr>" +
               "<td style='padding: 20px 30px; text-align: center; background-color: #ffffff;'>" +
               "<p style='margin: 0; color: #666666; font-size: 14px; line-height: 1.6;'>감사합니다.<br>로또 번호 추천 서비스</p>" +
               "</td>" +
               "</tr>" +
               "</table>" +
               "</td>" +
               "</tr>" +
               "</table>" +
               "</body>" +
               "</html>";
    }
}
