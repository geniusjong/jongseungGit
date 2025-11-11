package com.lottoweb.dto;

/**
 * 로또 당첨 번호 정보를 담는 DTO
 * 
 * ⭐ Entity 대신 DTO를 사용하는 이유:
 * 1. 클라이언트가 필요한 필드만 포함
 * 2. 데이터베이스 구조 변경 시 API 영향 최소화
 * 3. 응답 형식 일관성 유지
 * 
 * 예시 응답:
 * {
 *   "postgame": 1234,
 *   "num1": 1, "num2": 2, "num3": 3,
 *   "num4": 4, "num5": 5, "num6": 6,
 *   "bonus": 7,
 *   "firstprizecount": 10
 * }
 */
public class LottoNumberResponse {
    
    private int postgame;           // 회차
    private int num1, num2, num3, num4, num5, num6;  // 당첨 번호 6개
    private int bonus;             // 보너스 번호
    private int firstprizecount;   // 1등 당첨자 수
    
    // 기본 생성자
    public LottoNumberResponse() {}
    
    // 전체 데이터를 받는 생성자
    public LottoNumberResponse(int postgame, int num1, int num2, int num3, 
                              int num4, int num5, int num6, int bonus, int firstprizecount) {
        this.postgame = postgame;
        this.num1 = num1;
        this.num2 = num2;
        this.num3 = num3;
        this.num4 = num4;
        this.num5 = num5;
        this.num6 = num6;
        this.bonus = bonus;
        this.firstprizecount = firstprizecount;
    }
    
    // Getter와 Setter
    public int getPostgame() {
        return postgame;
    }
    
    public void setPostgame(int postgame) {
        this.postgame = postgame;
    }
    
    public int getNum1() {
        return num1;
    }
    
    public void setNum1(int num1) {
        this.num1 = num1;
    }
    
    public int getNum2() {
        return num2;
    }
    
    public void setNum2(int num2) {
        this.num2 = num2;
    }
    
    public int getNum3() {
        return num3;
    }
    
    public void setNum3(int num3) {
        this.num3 = num3;
    }
    
    public int getNum4() {
        return num4;
    }
    
    public void setNum4(int num4) {
        this.num4 = num4;
    }
    
    public int getNum5() {
        return num5;
    }
    
    public void setNum5(int num5) {
        this.num5 = num5;
    }
    
    public int getNum6() {
        return num6;
    }
    
    public void setNum6(int num6) {
        this.num6 = num6;
    }
    
    public int getBonus() {
        return bonus;
    }
    
    public void setBonus(int bonus) {
        this.bonus = bonus;
    }
    
    public int getFirstprizecount() {
        return firstprizecount;
    }
    
    public void setFirstprizecount(int firstprizecount) {
        this.firstprizecount = firstprizecount;
    }
}
