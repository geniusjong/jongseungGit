package com.lottoweb.model;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 저장된 로또 번호 Entity 클래스
 * 사용자가 추첨한 로또 번호를 저장하는 테이블과 매핑됩니다.
 */
@Entity
@Table(name = "tb_saved_lotto_number")
public class SavedLottoNumber {
    
    /**
     * 저장된 로또 번호 ID (기본키, 자동 증가)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    /**
     * 사용자 (Many-to-One 관계)
     * 한 사용자는 여러 개의 저장된 로또 번호를 가질 수 있습니다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    /**
     * 로또 번호 1
     */
    @Column(name = "num1", nullable = false)
    private int num1;
    
    /**
     * 로또 번호 2
     */
    @Column(name = "num2", nullable = false)
    private int num2;
    
    /**
     * 로또 번호 3
     */
    @Column(name = "num3", nullable = false)
    private int num3;
    
    /**
     * 로또 번호 4
     */
    @Column(name = "num4", nullable = false)
    private int num4;
    
    /**
     * 로또 번호 5
     */
    @Column(name = "num5", nullable = false)
    private int num5;
    
    /**
     * 로또 번호 6
     */
    @Column(name = "num6", nullable = false)
    private int num6;
    
    /**
     * 보너스 번호
     */
    @Column(name = "bonus_number", nullable = false)
    private int bonusNumber;
    
    /**
     * 생성일시
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * 수정일시
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * JPA에서 사용하기 위한 기본 생성자
     */
    public SavedLottoNumber() {
    }
    
    /**
     * 생성자
     */
    public SavedLottoNumber(User user, int num1, int num2, int num3, int num4, int num5, int num6, int bonusNumber) {
        this.user = user;
        this.num1 = num1;
        this.num2 = num2;
        this.num3 = num3;
        this.num4 = num4;
        this.num5 = num5;
        this.num6 = num6;
        this.bonusNumber = bonusNumber;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Entity 저장 전에 실행되는 메서드 (생성일시, 수정일시 자동 설정)
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Entity 업데이트 전에 실행되는 메서드 (수정일시 자동 설정)
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
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
    
    public int getBonusNumber() {
        return bonusNumber;
    }
    
    public void setBonusNumber(int bonusNumber) {
        this.bonusNumber = bonusNumber;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    /**
     * 로또 번호를 정렬된 배열로 반환
     */
    public int[] getNumbersAsArray() {
        int[] numbers = {num1, num2, num3, num4, num5, num6};
        java.util.Arrays.sort(numbers);
        return numbers;
    }
    
    /**
     * 두 저장된 로또 번호가 동일한 번호 조합인지 확인
     */
    public boolean isSameNumbers(SavedLottoNumber other) {
        int[] thisNumbers = this.getNumbersAsArray();
        int[] otherNumbers = other.getNumbersAsArray();
        return java.util.Arrays.equals(thisNumbers, otherNumbers);
    }
    
    @Override
    public String toString() {
        return "SavedLottoNumber{" +
                "id=" + id +
                ", userId=" + (user != null ? user.getId() : null) +
                ", numbers=[" + num1 + ", " + num2 + ", " + num3 + ", " + num4 + ", " + num5 + ", " + num6 + "]" +
                ", bonusNumber=" + bonusNumber +
                ", createdAt=" + createdAt +
                '}';
    }
}

