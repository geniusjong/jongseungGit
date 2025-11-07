package com.lottoweb.repository;

import com.lottoweb.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 사용자 Repository 인터페이스
 * JpaRepository를 상속받아 기본 CRUD 메서드를 자동으로 제공받습니다.
 * 
 * JpaRepository<엔티티타입, 기본키타입>
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * 사용자명으로 사용자 조회
     * 메서드 이름만으로 쿼리가 자동 생성됩니다!
     * 
     * SQL: SELECT * FROM tb_user WHERE username = ?
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 이메일로 사용자 조회
     * 
     * SQL: SELECT * FROM tb_user WHERE email = ?
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 사용자명 또는 이메일로 사용자 존재 여부 확인
     * 
     * SQL: SELECT COUNT(*) > 0 FROM tb_user WHERE username = ? OR email = ?
     */
    boolean existsByUsernameOrEmail(String username, String email);
    
    /**
     * 사용자명 존재 여부 확인
     * 
     * SQL: SELECT COUNT(*) > 0 FROM tb_user WHERE username = ?
     */
    boolean existsByUsername(String username);
    
    /**
     * 이메일 존재 여부 확인
     * 
     * SQL: SELECT COUNT(*) > 0 FROM tb_user WHERE email = ?
     */
    boolean existsByEmail(String email);
}

