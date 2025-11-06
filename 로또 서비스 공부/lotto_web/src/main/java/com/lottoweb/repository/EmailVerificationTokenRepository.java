package com.lottoweb.repository;

import com.lottoweb.model.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 이메일 인증 토큰 Repository 인터페이스
 */
@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {
    
    /**
     * 토큰 문자열로 토큰 조회
     */
    Optional<EmailVerificationToken> findByToken(String token);
    
    /**
     * 사용자 ID로 토큰 조회
     */
    Optional<EmailVerificationToken> findByUserId(Long userId);
    
    /**
     * 사용자 ID로 토큰 삭제
     */
    @Modifying
    @Query("DELETE FROM EmailVerificationToken e WHERE e.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}

