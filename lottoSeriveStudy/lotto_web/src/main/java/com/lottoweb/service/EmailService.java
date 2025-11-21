package com.lottoweb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;

/**
 * ?´ë©”ì¼ ë°œì†¡ ?„œë¹„ìŠ¤
 */
@Service
@ConditionalOnBean(JavaMailSender.class)
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
     * ?´ë©”ì¼ ?¸ì¦? ë©”ì¼ ë°œì†¡
     */
    public void sendVerificationEmail(String toEmail, String username, String token) throws MessagingException {
        String verificationUrl = appUrl + "/verify-email?token=" + token;
        
        // MimeMessageë¥? ?‚¬?š©?•˜?—¬ UTF-8 ?¸ì½”ë”© ë³´ì¥
        MimeMessage message = mailSender.createMimeMessage();
        
        // MimeMessageHelperë¥? UTF-8ë¡? ?ƒ?„± (ë©??‹°?ŒŒ?Š¸)
        MimeMessageHelper helper = new MimeMessageHelper(message, false, StandardCharsets.UTF_8.name());
        
        // ?ˆ˜?‹ ? ?„¤? •
        helper.setTo(toEmail);
        
        // ? œëª©ì„ UTF-8ë¡? ?¸ì½”ë”©?•˜?—¬ ?„¤? • (MimeMessage ì§ì ‘ ?‚¬?š©)
        String subject = "[ë¡œë˜ ë²ˆí˜¸ ì¶”ì²œ ?„œë¹„ìŠ¤] ?´ë©”ì¼ ?¸ì¦ì„ ?™„ë£Œí•´ì£¼ì„¸?š”";
        message.setSubject(subject, StandardCharsets.UTF_8.name());
        
        // HTML ?‚´?š© ?ƒ?„±
        String htmlContent = buildVerificationEmailContent(username, verificationUrl);
        
        // HTML ?˜•?‹?œ¼ë¡? ?´ë©”ì¼ ë³¸ë¬¸ ?„¤? • (UTF-8 ?¸ì½”ë”© ë³´ì¥)
        helper.setText(htmlContent, true);
        
        // Content-Type ëª…ì‹œ? ?œ¼ë¡? ?„¤? • (UTF-8)
        message.setHeader("Content-Type", "text/html; charset=UTF-8");
        
        // ë°œì‹ ? ?´ë©”ì¼?´ ?„¤? •?˜?–´ ?ˆ?œ¼ë©? ?‚¬?š©
        if (fromEmail != null && !fromEmail.isEmpty()) {
            helper.setFrom(fromEmail);
        }
        
        mailSender.send(message);
    }

    /**
     * ?´ë©”ì¼ ?¸ì¦? ë©”ì¼ ?‚´?š© ?ƒ?„± (HTML ?˜•?‹)
     * ?´ë©”ì¼ ?´?¼?´?–¸?Š¸ ?˜¸?™˜?„±?„ ?œ„?•´ ?¸?¼?¸ ?Š¤????¼ê³? ?…Œ?´ë¸? ê¸°ë°˜ ? ˆ?´?•„?›ƒ ?‚¬?š©
     */
    private String buildVerificationEmailContent(String username, String verificationUrl) {
        return "<!DOCTYPE html>" +
               "<html>" +
               "<head>" +
               "<meta charset='UTF-8'>" +
               "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
               "</head>" +
               "<body style='margin: 0; padding: 0; font-family: \"Malgun Gothic\", \"ë§‘ì?? ê³ ë”•\", Arial, sans-serif; background-color: #f5f5f5;'>" +
               "<table role='presentation' cellspacing='0' cellpadding='0' border='0' width='100%' style='background-color: #f5f5f5;'>" +
               "<tr>" +
               "<td align='center' style='padding: 20px 0;'>" +
               "<table role='presentation' cellspacing='0' cellpadding='0' border='0' width='600' style='max-width: 600px; background-color: #ffffff; border-radius: 10px; overflow: hidden; box-shadow: 0 2px 4px rgba(0,0,0,0.1);'>" +
               // ?—¤?”
               "<tr>" +
               "<td style='background-color: #7c6cff; padding: 30px; text-align: center;'>" +
               "<h1 style='margin: 0; color: #ffffff; font-size: 24px; font-weight: 600;'>ë¡œë˜ ë²ˆí˜¸ ì¶”ì²œ ?„œë¹„ìŠ¤</h1>" +
               "</td>" +
               "</tr>" +
               // ë³¸ë¬¸
               "<tr>" +
               "<td style='background-color: #f7f8fb; padding: 30px;'>" +
               "<p style='margin: 0 0 15px 0; color: #333333; font-size: 16px; line-height: 1.6;'>?•ˆ?…•?•˜?„¸?š”, <strong style='color: #7c6cff;'>" + username + "</strong>?‹˜!</p>" +
               "<p style='margin: 0 0 15px 0; color: #333333; font-size: 16px; line-height: 1.6;'>ë¡œë˜ ë²ˆí˜¸ ì¶”ì²œ ?„œë¹„ìŠ¤?— ê°??…?•´ì£¼ì…”?„œ ê°ì‚¬?•©?‹ˆ?‹¤.</p>" +
               "<p style='margin: 0 0 25px 0; color: #333333; font-size: 16px; line-height: 1.6;'>?´ë©”ì¼ ?¸ì¦ì„ ?™„ë£Œí•˜?‹œ? ¤ë©? ?•„?˜ ë²„íŠ¼?„ ?´ë¦??•´ì£¼ì„¸?š”:</p>" +
               // ë²„íŠ¼ (?…Œ?´ë¸? ê¸°ë°˜, ?¸?¼?¸ ?Š¤????¼ - ?´ë©”ì¼ ?´?¼?´?–¸?Š¸ ?˜¸?™˜?„± ìµœë???™”)
               "<table role='presentation' cellspacing='0' cellpadding='0' border='0' width='100%'>" +
               "<tr>" +
               "<td align='center' style='padding: 20px 0;'>" +
               "<table role='presentation' cellspacing='0' cellpadding='0' border='0' style='border-collapse: collapse;'>" +
               "<tr>" +
               "<td align='center' bgcolor='#7c6cff' style='background-color: #7c6cff; border-radius: 8px; padding: 0;'>" +
               "<a href='" + verificationUrl + "' style='display: block; padding: 15px 40px; background-color: #7c6cff; color: #ffffff; text-decoration: none; font-size: 16px; font-weight: 600; border-radius: 8px; border: 2px solid #7c6cff;'>?´ë©”ì¼ ?¸ì¦í•˜ê¸?</a>" +
               "</td>" +
               "</tr>" +
               "</table>" +
               "</td>" +
               "</tr>" +
               "</table>" +
               // ì£¼ì˜?‚¬?•­
               "<table role='presentation' cellspacing='0' cellpadding='0' border='0' width='100%' style='margin-top: 30px; padding-top: 20px; border-top: 1px solid #dddddd;'>" +
               "<tr>" +
               "<td>" +
               "<p style='margin: 0 0 10px 0; color: #666666; font-size: 14px; font-weight: 600;'><strong>ì£¼ì˜?‚¬?•­:</strong></p>" +
               "<ul style='margin: 0; padding-left: 20px; color: #666666; font-size: 14px; line-height: 1.8;'>" +
               "<li style='margin-bottom: 5px;'>?´ ë§í¬?Š” 24?‹œê°? ?™?•ˆ ?œ ?š¨?•©?‹ˆ?‹¤.</li>" +
               "<li style='margin-bottom: 5px;'>ë§í¬ê°? ë§Œë£Œ?˜ë©? ?‹¤?‹œ ?šŒ?›ê°??…?„ ì§„í–‰?•´ì£¼ì„¸?š”.</li>" +
               "<li style='margin-bottom: 5px;'>?´ ë§í¬?Š” ?•œ ë²ˆë§Œ ?‚¬?š©?•  ?ˆ˜ ?ˆ?Šµ?‹ˆ?‹¤.</li>" +
               "</ul>" +
               "</td>" +
               "</tr>" +
               "</table>" +
               "</td>" +
               "</tr>" +
               // ?‘¸?„°
               "<tr>" +
               "<td style='padding: 20px 30px; text-align: center; background-color: #ffffff;'>" +
               "<p style='margin: 0; color: #666666; font-size: 14px; line-height: 1.6;'>ê°ì‚¬?•©?‹ˆ?‹¤.<br>ë¡œë˜ ë²ˆí˜¸ ì¶”ì²œ ?„œë¹„ìŠ¤</p>" +
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
