package com.lottoweb.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Random;

public final class LuckyNumber {
    private LuckyNumber() {}

    /**
     * 한국시간(KST) 기준 날짜로 시드를 만들어 1~45 사이의 동일한 숫자를 하루 동안 반환한다.
     */
    public static int todayKST() {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        long seed = today.toEpochDay();
        return new Random(seed).nextInt(45) + 1;
    }
}

