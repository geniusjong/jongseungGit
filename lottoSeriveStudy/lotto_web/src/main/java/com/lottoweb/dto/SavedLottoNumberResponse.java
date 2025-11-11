package com.lottoweb.dto;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 저장된 로또 번호 응답 DTO
 */
public class SavedLottoNumberResponse {
    
    private Long id;                    // 저장된 로또 번호 ID
    private List<Integer> numbers;       // 6개의 로또 번호
    private int bonusNumber;            // 보너스 번호
    private LocalDateTime savedAt;      // 저장일시
    
    // 기본 생성자
    public SavedLottoNumberResponse() {}
    
    // 전체 데이터를 받는 생성자
    public SavedLottoNumberResponse(Long id, int[] numbers, int bonusNumber, LocalDateTime savedAt) {
        this.id = id;
        this.numbers = Arrays.stream(numbers).boxed().collect(Collectors.toList());
        this.bonusNumber = bonusNumber;
        this.savedAt = savedAt;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public List<Integer> getNumbers() {
        return numbers;
    }
    
    public void setNumbers(List<Integer> numbers) {
        this.numbers = numbers;
    }
    
    public int getBonusNumber() {
        return bonusNumber;
    }
    
    public void setBonusNumber(int bonusNumber) {
        this.bonusNumber = bonusNumber;
    }
    
    public LocalDateTime getSavedAt() {
        return savedAt;
    }
    
    public void setSavedAt(LocalDateTime savedAt) {
        this.savedAt = savedAt;
    }
}
