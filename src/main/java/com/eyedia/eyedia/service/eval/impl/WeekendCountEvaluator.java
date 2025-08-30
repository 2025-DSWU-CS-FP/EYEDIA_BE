package com.eyedia.eyedia.service.eval.impl;

import com.eyedia.eyedia.domain.badge.Badge;
import com.eyedia.eyedia.domain.enums.badge.AggregationType;
import com.eyedia.eyedia.dto.BadgeEventDTO;
import com.eyedia.eyedia.service.eval.BadgeEvaluator;
import org.springframework.stereotype.Component;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class WeekendCountEvaluator implements BadgeEvaluator {
    @Override public AggregationType supports() { return AggregationType.WEEKEND_COUNT; }

    @Override
    public void apply(Badge p, Map<String,Object> params, BadgeEventDTO event) {
        LocalDate today = event.getOccurredAt().toLocalDate();
        DayOfWeek weekStartDow = DayOfWeek.MONDAY;
        if (params != null && params.get("weekStart") instanceof String s) {
            weekStartDow = DayOfWeek.valueOf(s);
        }
        // 주 시작 계산
        int shift = (7 + (today.getDayOfWeek().getValue() - weekStartDow.getValue())) % 7;
        LocalDate thisWeekStart = today.minusDays(shift);

        if (p.getWeekStart() == null || !p.getWeekStart().equals(thisWeekStart)) {
            p.setWeekStart(thisWeekStart);
            p.setCurrentValue(0);
        }

        // 기본: 토/일
        Set<DayOfWeek> allowed = Set.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
        if (params != null && params.get("days") instanceof List<?> list && !list.isEmpty()) {
            allowed = list.stream().map(Object::toString).map(DayOfWeek::valueOf).collect(java.util.stream.Collectors.toSet());
        }
        if (allowed.contains(today.getDayOfWeek())) {
            p.setCurrentValue(p.getCurrentValue() + 1);
        }
    }
}

