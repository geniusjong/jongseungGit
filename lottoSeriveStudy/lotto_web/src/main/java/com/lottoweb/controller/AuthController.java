package com.lottoweb.controller;

import com.lottoweb.model.EmailVerificationToken;
import com.lottoweb.model.User;
import com.lottoweb.repository.EmailVerificationTokenRepository;
import com.lottoweb.repository.UserRepository;
import com.lottoweb.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * 인증 관련 Controller
 * 로그인/회원가입 페이지를 처리합니다.
 */
@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Optional<EmailService> emailService;
    private final EmailVerificationTokenRepository tokenRepository;

    @Autowired
    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder,
                         Optional<EmailService> emailService, EmailVerificationTokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.tokenRepository = tokenRepository;
    }

    /**
     * 로그인 페이지 표시
     * Spring Security가 로그인 처리를 하므로 GET 요청만 처리합니다.
     */
    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    /**
     * 회원가입 페이지 표시
     */
    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";
    }

    /**
     * 회원가입 처리
     */
    @PostMapping("/register")
    public String register(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String email,
            Model model) {

        // 사용자명 검증
        if (username == null || username.trim().isEmpty()) {
            model.addAttribute("error", "사용자명을 입력해주세요.");
            return "auth/register";
        }

        if (password == null || password.length() < 4) {
            model.addAttribute("error", "비밀번호는 최소 4자 이상이어야 합니다.");
            return "auth/register";
        }

        // 이메일 형식 검증
        if (email == null || email.trim().isEmpty()) {
            model.addAttribute("error", "이메일을 입력해주세요.");
            return "auth/register";
        }

        // 이메일 형식 검증 (간단한 형식 검증)
        if (!email.contains("@") || !email.contains(".")) {
            model.addAttribute("error", "올바른 이메일 형식을 입력해주세요.");
            return "auth/register";
        }

        // 사용자명 중복 확인
        if (userRepository.existsByUsername(username)) {
            model.addAttribute("error", "이미 사용 중인 사용자명입니다.");
            return "auth/register";
        }

        // 이메일 중복 확인
        if (userRepository.existsByEmail(email)) {
            model.addAttribute("error", "이미 사용 중인 이메일입니다.");
            return "auth/register";
        }

        try {
            // 비밀번호 암호화
            String encodedPassword = passwordEncoder.encode(password);

            // 새 사용자 생성 (이메일 인증 전까지는 enabled = false)
            User newUser = new User();
            newUser.setUsername(username.trim());
            newUser.setPassword(encodedPassword);
            newUser.setEmail(email.trim());
            newUser.setRole("USER");
            newUser.setEnabled(false); // 이메일 인증 전에는 비활성화

            // 데이터베이스에 저장
            User savedUser = userRepository.save(newUser);

            // 이메일 인증 토큰 생성
            String token = UUID.randomUUID().toString();
            LocalDateTime expiryDate = LocalDateTime.now().plusHours(24); // 24시간 동안 유효
            
            EmailVerificationToken verificationToken = new EmailVerificationToken(
                token, savedUser.getId(), expiryDate
            );
            tokenRepository.save(verificationToken);

            // 이메일 인증 메일 발송
            emailService.ifPresent(service -> {
                try {
                    service.sendVerificationEmail(
                        savedUser.getEmail(),
                        savedUser.getUsername(),
                        token
                    );
                } catch (javax.mail.MessagingException e) {
                    // 이메일 발송 실패 시에도 사용자는 저장됨 (나중에 재발송 가능)
                    // 로그는 출력하되 사용자에게는 성공 메시지 표시
                    System.err.println("이메일 발송 실패: " + e.getMessage());
                    e.printStackTrace();
                }
            });

            // 회원가입 성공 - 이메일 인증 안내 페이지로 리다이렉트
            return "redirect:/register-success?email=" + savedUser.getEmail();
        } catch (Exception e) {
            model.addAttribute("error", "회원가입 중 오류가 발생했습니다. 다시 시도해주세요.");
            return "auth/register";
        }
    }

    /**
     * 회원가입 성공 페이지 표시 (이메일 인증 안내)
     */
    @GetMapping("/register-success")
    public String registerSuccess(@RequestParam String email, Model model) {
        model.addAttribute("email", email);
        return "auth/registerSuccess";
    }

    /**
     * 이메일 인증 처리
     */
    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam String token, Model model) {
        // 토큰 조회
        Optional<EmailVerificationToken> tokenOpt = tokenRepository.findByToken(token);
        
        if (tokenOpt.isEmpty()) {
            model.addAttribute("error", "유효하지 않은 인증 링크입니다.");
            return "auth/verifyEmail";
        }

        EmailVerificationToken verificationToken = tokenOpt.get();
        
        // 토큰 만료 확인
        if (verificationToken.isExpired()) {
            model.addAttribute("error", "인증 링크가 만료되었습니다. 다시 회원가입해주세요.");
            // 만료된 토큰 삭제
            tokenRepository.delete(verificationToken);
            return "auth/verifyEmail";
        }

        // 사용자 조회 후 인증 완료 처리
        Optional<User> userOpt = userRepository.findById(verificationToken.getUserId());
        
        if (userOpt.isEmpty()) {
            model.addAttribute("error", "사용자를 찾을 수 없습니다.");
            return "auth/verifyEmail";
        }

        User user = userOpt.get();
        user.setEnabled(true); // 이메일 인증 완료
        userRepository.save(user);

        // 토큰 삭제 (한 번만 사용 가능)
        tokenRepository.delete(verificationToken);

        model.addAttribute("success", "이메일 인증이 완료되었습니다. 로그인해주세요.");
        return "auth/verifyEmail";
    }
}
