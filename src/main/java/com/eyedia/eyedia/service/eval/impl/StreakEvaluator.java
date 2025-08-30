package com.eyedia.eyedia.service.eval.impl;

import com.eyedia.eyedia.domain.badge.Badge;
import com.eyedia.eyedia.domain.enums.badge.AggregationType;
import com.eyedia.eyedia.dto.BadgeEventDTO;
import com.eyedia.eyedia.service.eval.BadgeEvaluator;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Component
public class StreakEvaluator implements BadgeEvaluator {
    @Override public AggregationType supports() { return AggregationType.STREAK; }

    @Override
    public void apply(Badge p, Map<String,Object> params, BadgeEventDTO event) {
        boolean uniquePerDay = params != null && Boolean.TRUE.equals(params.get("uniquePerDay"));
        LocalDate today = event.getOccurredAt().toLocalDate();
        LocalDate last = p.getLastProgressDate();

        if (last == null) {
            p.setCurrentValue(1);
            p.setLastProgressDate(today);
            return;
        }
        long gap = ChronoUnit.DAYS.between(last, today);
        if (gap == 0) { // 동일 날짜 방문
            if (uniquePerDay) return;
            p.setCurrentValue(p.getCurrentValue() + 1);
        } else if (gap == 1) { // 연속 방문
            p.setCurrentValue(p.getCurrentValue() + 1);
            p.setLastProgressDate(today);
        } else if (gap > 1) { // 불연속 방문
            p.setCurrentValue(1); // 오늘부터로 초기화
            p.setLastProgressDate(today);
        } // 과거 날짜(gap<0)는 무시
    }
}
