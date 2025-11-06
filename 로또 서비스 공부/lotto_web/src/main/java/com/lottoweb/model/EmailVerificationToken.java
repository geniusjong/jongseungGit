package com.lottoweb.model;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 이메일 인증 토큰 Entity
 * 이메일 인증 링크에 사용되는 토큰을 저장합니다.
 */
@Entity
@Table(name = "tb_email_verification_token")
public class EmailVerificationToken {
    
    /**
     * 토큰 ID (기본키, 자동 증가)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    /**
     * 토큰 문자열 (고유, 필수)
     */
    @Column(name = "token", nullable = false, unique = true, length = 255)
    private String token;
    
    /**
     * 사용자 ID (외래키)
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    /**
     * 만료일시
     */
    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;
    
    /**
     * 생성일시
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * JPA에서 사용하기 위한 기본 생성자
     */
    public EmailVerificationToken() {
    }
    
    /**
     * 생성자
     */
    public EmailVerificationToken(String token, Long userId, LocalDateTime expiryDate) {
        this.token = token;
        this.userId = userId;
        this.expiryDate = expiryDate;
        this.createdAt = LocalDateTime.now();
    }
    
    /**
     * Entity 저장 전에 실행되는 메서드 (생성일시 자동 설정)
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    /**
     * 토큰이 만료되었는지 확인
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }
    
    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

