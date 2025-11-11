package com.lottoweb.repository;

import com.lottoweb.model.SavedLottoNumber;
import com.lottoweb.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedLottoNumberRepository extends JpaRepository<SavedLottoNumber, Long> {

    /**
     * 특정 사용자가 특정 로또 번호 조합을 이미 저장했는지 확인합니다.
     */
    Optional<SavedLottoNumber> findByUserAndNum1AndNum2AndNum3AndNum4AndNum5AndNum6(
            User user, int num1, int num2, int num3, int num4, int num5, int num6);

    /**
     * 사용자 ID로 저장된 로또 번호 목록 조회 (최신순)
     */
    List<SavedLottoNumber> findByUserOrderByCreatedAtDesc(User user);

    /**
     * 사용자 ID로 저장된 로또 번호 개수 조회
     */
    long countByUser(User user);

    /**
     * 사용자 ID와 저장된 로또 번호 ID로 조회
     * (본인이 저장한 번호만 조회 가능하도록)
     */
    Optional<SavedLottoNumber> findByIdAndUser(Long id, User user);
}
