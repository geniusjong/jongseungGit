package com.lottoweb.service;

import com.lottoweb.model.SavedLottoNumber;
import com.lottoweb.model.User;
import com.lottoweb.repository.SavedLottoNumberRepository;
import com.lottoweb.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 저장된 로또 번호 서비스
 * 저장, 조회, 중복 체크 등의 비즈니스 로직을 처리합니다.
 */
@Service
public class SavedLottoNumberService {
    
    private final SavedLottoNumberRepository savedLottoNumberRepository;
    private final UserRepository userRepository;
    
    @Autowired
    public SavedLottoNumberService(
            SavedLottoNumberRepository savedLottoNumberRepository,
            UserRepository userRepository) {
        this.savedLottoNumberRepository = savedLottoNumberRepository;
        this.userRepository = userRepository;
    }
    
    /**
     * 로또 번호 저장
     * 
     * @param username 사용자명
     * @param numbers 6개의 로또 번호 배열 (정렬되지 않아도 됨)
     * @param bonusNumber 보너스 번호
     * @return 저장된 SavedLottoNumber 객체
     * @throws IllegalArgumentException 사용자를 찾을 수 없거나 번호가 유효하지 않은 경우
     */
    @Transactional
    public SavedLottoNumber saveLottoNumber(String username, int[] numbers, int bonusNumber) {
        // 사용자 조회
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + username));
        
        // 번호 유효성 검사
        if (numbers == null || numbers.length != 6) {
            throw new IllegalArgumentException("로또 번호는 6개여야 합니다.");
        }
        
        // 번호 정렬 (중복 체크를 위해)
        int[] sortedNumbers = Arrays.copyOf(numbers, 6);
        Arrays.sort(sortedNumbers);
        
        // 번호 범위 검사 (1~45)
        for (int num : sortedNumbers) {
            if (num < 1 || num > 45) {
                throw new IllegalArgumentException("로또 번호는 1부터 45 사이의 숫자여야 합니다.");
            }
        }
        if (bonusNumber < 1 || bonusNumber > 45) {
            throw new IllegalArgumentException("보너스 번호는 1부터 45 사이의 숫자여야 합니다.");
        }
        
        // 중복 번호 검사
        for (int i = 0; i < sortedNumbers.length - 1; i++) {
            if (sortedNumbers[i] == sortedNumbers[i + 1]) {
                throw new IllegalArgumentException("로또 번호는 중복될 수 없습니다.");
            }
        }
        
        // 보너스 번호가 일반 번호와 중복되는지 검사
        for (int num : sortedNumbers) {
            if (num == bonusNumber) {
                throw new IllegalArgumentException("보너스 번호는 일반 번호와 중복될 수 없습니다.");
            }
        }
        
        // 저장된 로또 번호 생성
        SavedLottoNumber savedLottoNumber = new SavedLottoNumber(
                user,
                sortedNumbers[0],
                sortedNumbers[1],
                sortedNumbers[2],
                sortedNumbers[3],
                sortedNumbers[4],
                sortedNumbers[5],
                bonusNumber
        );
        
        // 저장
        return savedLottoNumberRepository.save(savedLottoNumber);
    }
    
    /**
     * 사용자의 저장된 로또 번호 목록 조회 (최신순)
     * 
     * @param username 사용자명
     * @return 저장된 로또 번호 목록
     */
    @Transactional(readOnly = true)
    public List<SavedLottoNumber> getSavedLottoNumbers(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + username));
        
        return savedLottoNumberRepository.findByUserOrderByCreatedAtDesc(user);
    }
    
    /**
     * 저장된 로또 번호 삭제
     * 
     * @param username 사용자명
     * @param savedLottoNumberId 저장된 로또 번호 ID
     * @return 삭제 성공 여부
     */
    @Transactional
    public boolean deleteSavedLottoNumber(String username, Long savedLottoNumberId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + username));
        
        Optional<SavedLottoNumber> savedLottoNumber = savedLottoNumberRepository.findByIdAndUser(savedLottoNumberId, user);
        
        if (savedLottoNumber.isPresent()) {
            savedLottoNumberRepository.delete(savedLottoNumber.get());
            return true;
        }
        
        return false;
    }
    
    /**
     * 사용자의 저장된 로또 번호 개수 조회
     * 
     * @param username 사용자명
     * @return 저장된 로또 번호 개수
     */
    @Transactional(readOnly = true)
    public long getSavedLottoNumberCount(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + username));
        
        return savedLottoNumberRepository.countByUser(user);
    }
    
    /**
     * 동일한 번호 조합이 이미 저장되어 있는지 확인
     * (같은 사용자가 같은 번호 조합을 저장했는지 확인)
     * 
     * @param username 사용자명
     * @param numbers 6개의 로또 번호 배열
     * @return 중복 여부
     */
    @Transactional(readOnly = true)
    public boolean isDuplicate(String username, int[] numbers) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + username));
        
        if (numbers == null || numbers.length != 6) {
            return false;
        }
        
        // 번호 정렬
        int[] sortedNumbers = Arrays.copyOf(numbers, 6);
        Arrays.sort(sortedNumbers);
        
        // 사용자의 모든 저장된 번호 조회
        List<SavedLottoNumber> savedNumbers = savedLottoNumberRepository.findByUserOrderByCreatedAtDesc(user);
        
        // 각 저장된 번호와 비교
        for (SavedLottoNumber saved : savedNumbers) {
            int[] savedNumbersArray = saved.getNumbersAsArray();
            if (Arrays.equals(sortedNumbers, savedNumbersArray)) {
                return true;
            }
        }
        
        return false;
    }
}

