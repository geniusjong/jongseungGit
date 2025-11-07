package com.lottoweb.dto;

import java.util.List;

/**
 * 로또 번호 추첨 결과를 담는 DTO (Data Transfer Object)
 * 
 * ? 왜 DTO를 사용하나요?
 * 1. Entity와 분리: 데이터베이스 구조와 API 응답 구조를 분리
 * 2. 필요한 데이터만 전송: 클라이언트가 필요한 정보만 포함
 * 3. 유연성: API 응답 형식을 쉽게 변경 가능
 * 4. 보안: 민감한 정보는 제외하고 필요한 데이터만 전송
 * 
 * 예시 응답:
 * {
 *   "drawnNumbers": [1, 2, 3, 4, 5, 6],
 *   "bonusNumber": 7,
 *   "usedLucky": true,
 *   "luckyNumber": 8
 * }
 */
public class LottoDrawResponse {
    
    private List<Integer> drawnNumbers;  // 추첨된 6개 번호
    private int bonusNumber;              // 보너스 번호
    private boolean usedLucky;            // 행운번호 사용 여부
    private Integer luckyNumber;          // 행운번호 (사용한 경우만)
    
    // 기본 생성자
    public LottoDrawResponse() {}
    
    // 전체 데이터를 받는 생성자
    public LottoDrawResponse(List<Integer> drawnNumbers, int bonusNumber, 
                             boolean usedLucky, Integer luckyNumber) {
        this.drawnNumbers = drawnNumbers;
        this.bonusNumber = bonusNumber;
        this.usedLucky = usedLucky;
        this.luckyNumber = luckyNumber;
    }
    
    // 행운번호 없이 추첨한 경우
    public LottoDrawResponse(List<Integer> drawnNumbers, int bonusNumber) {
        this.drawnNumbers = drawnNumbers;
        this.bonusNumber = bonusNumber;
        this.usedLucky = false;
        this.luckyNumber = null;
    }
    
    // Getter와 Setter
    public List<Integer> getDrawnNumbers() {
        return drawnNumbers;
    }
    
    public void setDrawnNumbers(List<Integer> drawnNumbers) {
        this.drawnNumbers = drawnNumbers;
    }
    
    public int getBonusNumber() {
        return bonusNumber;
    }
    
    public void setBonusNumber(int bonusNumber) {
        this.bonusNumber = bonusNumber;
    }
    
    public boolean isUsedLucky() {
        return usedLucky;
    }
    
    public void setUsedLucky(boolean usedLucky) {
        this.usedLucky = usedLucky;
    }
    
    public Integer getLuckyNumber() {
        return luckyNumber;
    }
    
    public void setLuckyNumber(Integer luckyNumber) {
        this.luckyNumber = luckyNumber;
    }
    
    @Override
    public String toString() {
        return "LottoDrawResponse{" +
                "drawnNumbers=" + drawnNumbers +
                ", bonusNumber=" + bonusNumber +
                ", usedLucky=" + usedLucky +
                ", luckyNumber=" + luckyNumber +
                '}';
    }
}

