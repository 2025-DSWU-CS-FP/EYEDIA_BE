package com.eyedia.eyedia.domain.common;

import java.time.Year;
import java.time.ZoneId;

public final class AgeUtil {
    private AgeUtil() {}
    public static Integer fromBirthYear(Integer birthYear, ZoneId zone) {
        if (birthYear == null) return null;
        int thisYear = Year.now(zone).getValue();
        if (birthYear < 1900 || birthYear > thisYear) return null;
        return thisYear - birthYear; // (정책) 생일지난 여부 모를 때 상한값
    }
}