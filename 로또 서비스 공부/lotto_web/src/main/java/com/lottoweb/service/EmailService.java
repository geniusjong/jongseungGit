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
        
        // MimeMessage를 사용하여 UTF-8 인코딩 명시
        MimeMessage message = mailSender.createMimeMessage();
        
        // MimeMessageHelper를 UTF-8로 생성 (단순 모드)
        MimeMessageHelper helper = new MimeMessageHelper(message, false, StandardCharsets.UTF_8.name());
        
        // 수신자 설정
        helper.setTo(toEmail);
        
        // 제목을 UTF-8로 인코딩하여 설정 (MimeMessage 직접 사용)
        String subject = "[로또 번호 추천 서비스] 이메일 인증을 완료해주세요";
        message.setSubject(subject, StandardCharsets.UTF_8.name());
        
        // HTML 콘텐츠 생성
        String htmlContent = buildVerificationEmailContent(username, verificationUrl);
        
        // HTML 형식으로 이메일 본문 설정 (UTF-8 인코딩 명시)
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
     */
    private String buildVerificationEmailContent(String username, String verificationUrl) {
        return "<!DOCTYPE html>" +
               "<html>" +
               "<head>" +
               "<meta charset='UTF-8'>" +
               "<style>" +
               "body { font-family: 'Malgun Gothic', '맑은 고딕', sans-serif; line-height: 1.6; color: #333; }" +
               ".container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
               ".header { background: linear-gradient(135deg, #7c6cff, #00e0b8); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }" +
               ".content { background: #f7f8fb; padding: 30px; border-radius: 0 0 10px 10px; }" +
               ".button { display: inline-block; background: linear-gradient(135deg, #7c6cff, #00e0b8); color: white; padding: 15px 30px; text-decoration: none; border-radius: 8px; font-weight: 600; margin: 20px 0; }" +
               ".button:hover { opacity: 0.9; }" +
               ".footer { margin-top: 30px; padding-top: 20px; border-top: 1px solid #ddd; color: #666; font-size: 12px; }" +
               "</style>" +
               "</head>" +
               "<body>" +
               "<div class='container'>" +
               "<div class='header'>" +
               "<h1 style='margin: 0;'>로또 번호 추천 서비스</h1>" +
               "</div>" +
               "<div class='content'>" +
               "<p>안녕하세요, <strong>" + username + "</strong>님!</p>" +
               "<p>로또 번호 추천 서비스에 가입해주셔서 감사합니다.</p>" +
               "<p>이메일 인증을 완료하시려면 아래 버튼을 클릭해주세요:</p>" +
               "<div style='text-align: center;'>" +
               "<a href='" + verificationUrl + "' class='button' style='color: white; text-decoration: none;'>이메일 인증하기</a>" +
               "</div>" +
               "<div class='footer'>" +
               "<p><strong>주의사항:</strong></p>" +
               "<ul style='margin: 10px 0; padding-left: 20px;'>" +
               "<li>이 링크는 24시간 동안 유효합니다.</li>" +
               "<li>링크가 만료되면 다시 회원가입을 진행해주세요.</li>" +
               "<li>이 링크는 한 번만 사용할 수 있습니다.</li>" +
               "</ul>" +
               "</div>" +
               "<p style='margin-top: 30px;'>감사합니다.<br>로또 번호 추천 서비스</p>" +
               "</div>" +
               "</div>" +
               "</body>" +
               "</html>";
    }
}

