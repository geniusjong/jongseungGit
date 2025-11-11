package com.lottoweb.dto;

/**
 * 로또 번호 저장 요청 DTO
 */
public class SaveLottoNumberRequest {
    
    private int[] numbers;      // 6개의 로또 번호
    private int bonusNumber;    // 보너스 번호
    private String memo;        // 선택 사항
    
    // 기본 생성자
    public SaveLottoNumberRequest() {}
    
    // 전체 데이터를 받는 생성자
    public SaveLottoNumberRequest(int[] numbers, int bonusNumber, String memo) {
        this.numbers = numbers;
        this.bonusNumber = bonusNumber;
        this.memo = memo;
    }
    
    // Getters and Setters
    public int[] getNumbers() {
        return numbers;
    }
    
    public void setNumbers(int[] numbers) {
        this.numbers = numbers;
    }
    
    public int getBonusNumber() {
        return bonusNumber;
    }
    
    public void setBonusNumber(int bonusNumber) {
        this.bonusNumber = bonusNumber;
    }
    
    public String getMemo() {
        return memo;
    }
    
    public void setMemo(String memo) {
        this.memo = memo;
    }
}
