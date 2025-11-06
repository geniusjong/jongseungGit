package com.lottoweb.model;

import javax.persistence.*;

/**
 * 로또 번호 Entity 클래스
 * 데이터베이스의 tb_lotto_number 테이블과 매핑됩니다.
 */
@Entity
@Table(name = "tb_lotto_number")
public class LottoNumber {
    
    /**
     * 회차 번호 (기본키)
     */
    @Id
    @Column(name = "postgame")
    private int postgame;
    
    /**
     * 로또 번호 1
     */
    @Column(name = "num1")
    private int num1;
    
    /**
     * 로또 번호 2
     */
    @Column(name = "num2")
    private int num2;
    
    /**
     * 로또 번호 3
     */
    @Column(name = "num3")
    private int num3;
    
    /**
     * 로또 번호 4
     */
    @Column(name = "num4")
    private int num4;
    
    /**
     * 로또 번호 5
     */
    @Column(name = "num5")
    private int num5;
    
    /**
     * 로또 번호 6
     */
    @Column(name = "num6")
    private int num6;
    
    /**
     * 보너스 번호
     */
    @Column(name = "bonusnum")
    private int bonusnum;
    
    /**
     * 1등 당첨 금액
     */
    @Column(name = "firstprize")
    private long firstprize;
    
    /**
     * 1등 당첨자 수
     */
    @Column(name = "firstprizecount")
    private int firstprizecount;

    // 기본 생성자 (JPA 필수)
    public LottoNumber() {
    }

    // 모든 필드를 포함하는 생성자
    public LottoNumber(int postgame, int num1, int num2, int num3, int num4, int num5, int num6, int bonusnum, long firstprize, int firstprizecount) {
        this.postgame = postgame;
        this.num1 = num1;
        this.num2 = num2;
        this.num3 = num3;
        this.num4 = num4;
        this.num5 = num5;
        this.num6 = num6;
        this.bonusnum = bonusnum;
        this.firstprize = firstprize;
        this.firstprizecount = firstprizecount;
    }

    // Getter & Setter 메서드
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

    public int getBonusnum() {
        return bonusnum;
    }

    public void setBonusnum(int bonusnum) {
        this.bonusnum = bonusnum;
    }

    public long getFirstprize() {
        return firstprize;
    }

    public void setFirstprize(long firstprize) {
        this.firstprize = firstprize;
    }

    public int getFirstprizecount() {
        return firstprizecount;
    }

    public void setFirstprizecount(int firstprizecount) {
        this.firstprizecount = firstprizecount;
    }

    @Override
    public String toString() {
        return "LottoNumber{" +
                "postgame=" + postgame +
                ", num1=" + num1 +
                ", num2=" + num2 +
                ", num3=" + num3 +
                ", num4=" + num4 +
                ", num5=" + num5 +
                ", num6=" + num6 +
                ", bonusnum=" + bonusnum +
                ", firstprize=" + firstprize +
                ", firstprizecount=" + firstprizecount +
                '}';
    }
}

