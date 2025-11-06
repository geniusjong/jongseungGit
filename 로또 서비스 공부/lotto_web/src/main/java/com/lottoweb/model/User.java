package com.lottoweb.model;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 사용자 Entity 클래스
 * 데이터베이스의 tb_user 테이블과 매핑됩니다.
 */
@Entity
@Table(name = "tb_user")
public class User {
    
    /**
     * 사용자 ID (기본키, 자동 증가)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    /**
     * 사용자명 (고유, 필수)
     */
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;
    
    /**
     * 비밀번호 (필수, 암호화되어 저장)
     */
    @Column(name = "password", nullable = false, length = 255)
    private String password;
    
    /**
     * 이메일 (필수, 고유)
     */
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;
    
    /**
     * 권한 (USER, ADMIN 등, 기본값: USER)
     */
    @Column(name = "role", nullable = false, length = 20)
    private String role = "USER";
    
    /**
     * 이메일 인증 여부 (기본값: false)
     * true: 이메일 인증 완료, false: 이메일 인증 미완료
     */
    @Column(name = "enabled", nullable = false)
    private boolean enabled = false;
    
    /**
     * 생성일시
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * 수정일시
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * JPA에서 사용하기 위한 기본 생성자
     */
    public User() {
    }
    
    /**
     * 생성자
     */
    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = "USER";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Entity 저장 전에 실행되는 메서드 (생성일시, 수정일시 자동 설정)
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Entity 업데이트 전에 실행되는 메서드 (수정일시 자동 설정)
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", enabled=" + enabled +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

