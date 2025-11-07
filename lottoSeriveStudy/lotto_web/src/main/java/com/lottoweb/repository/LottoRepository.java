package com.lottoweb.repository;

import com.lottoweb.model.LottoNumber;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 로또 번호 Repository 인터페이스
 * JpaRepository를 상속받아 기본 CRUD 메서드를 자동으로 제공받습니다.
 * 
 * JpaRepository<엔티티타입, 기본키타입>
 */
@Repository
public interface LottoRepository extends JpaRepository<LottoNumber, Integer> {
    
    /**
     * 최신 로또 번호 조회 (회차 내림차순 정렬 후 첫 번째)
     * 메서드 이름만으로 쿼리가 자동 생성됩니다!
     * 
     * SQL: SELECT * FROM tb_lotto_number ORDER BY postgame DESC LIMIT 1
     */
    Optional<LottoNumber> findFirstByOrderByPostgameDesc();
    
    /**
     * 회차 번호로 조회
     * 
     * SQL: SELECT * FROM tb_lotto_number WHERE postgame = ?
     */
    Optional<LottoNumber> findByPostgame(int postgame);
    
    /**
     * 회차 범위로 조회 (페이징 포함)
     * 
     * SQL: SELECT * FROM tb_lotto_number WHERE postgame BETWEEN ? AND ? ORDER BY postgame DESC
     */
    Page<LottoNumber> findByPostgameBetweenOrderByPostgameDesc(
        int startPostgame, int endPostgame, Pageable pageable);
    
    /**
     * 전체 조회 (회차 내림차순, 페이징)
     */
    Page<LottoNumber> findAllByOrderByPostgameDesc(Pageable pageable);
    
    /**
     * 전체 개수 조회
     * 
     * SQL: SELECT COUNT(*) FROM tb_lotto_number
     */
    // count() 메서드는 JpaRepository에서 이미 제공됨!
    
    /**
     * 특정 번호를 포함하는 로또 번호 조회
     * 복잡한 쿼리는 @Query 어노테이션으로 작성
     * 
     * SQL: SELECT * FROM tb_lotto_number 
     *      WHERE num1=? OR num2=? OR num3=? OR num4=? OR num5=? OR num6=? OR bonusnum=?
     */
    @Query("SELECT l FROM LottoNumber l WHERE " +
           "l.num1 = :number OR l.num2 = :number OR l.num3 = :number OR " +
           "l.num4 = :number OR l.num5 = :number OR l.num6 = :number OR l.bonusnum = :number")
    List<LottoNumber> findByIncludeNumber(@Param("number") int number);
    
    /**
     * 회차 범위와 특정 번호 포함 조건으로 조회
     * 복잡한 조건은 @Query로 작성
     */
    @Query("SELECT l FROM LottoNumber l WHERE " +
           "(:startPostgame IS NULL OR l.postgame >= :startPostgame) AND " +
           "(:endPostgame IS NULL OR l.postgame <= :endPostgame) AND " +
           "(:includeNumber IS NULL OR " +
           "l.num1 = :includeNumber OR l.num2 = :includeNumber OR " +
           "l.num3 = :includeNumber OR l.num4 = :includeNumber OR " +
           "l.num5 = :includeNumber OR l.num6 = :includeNumber OR " +
           "l.bonusnum = :includeNumber) " +
           "ORDER BY l.postgame DESC")
    List<LottoNumber> findWithFilters(
        @Param("startPostgame") Integer startPostgame,
        @Param("endPostgame") Integer endPostgame,
        @Param("includeNumber") Integer includeNumber,
        Pageable pageable);
    
    /**
     * 필터 조건에 맞는 개수 조회
     */
    @Query("SELECT COUNT(l) FROM LottoNumber l WHERE " +
           "(:startPostgame IS NULL OR l.postgame >= :startPostgame) AND " +
           "(:endPostgame IS NULL OR l.postgame <= :endPostgame) AND " +
           "(:includeNumber IS NULL OR " +
           "l.num1 = :includeNumber OR l.num2 = :includeNumber OR " +
           "l.num3 = :includeNumber OR l.num4 = :includeNumber OR " +
           "l.num5 = :includeNumber OR l.num6 = :includeNumber OR " +
           "l.bonusnum = :includeNumber)")
    long countWithFilters(
        @Param("startPostgame") Integer startPostgame,
        @Param("endPostgame") Integer endPostgame,
        @Param("includeNumber") Integer includeNumber);
}

