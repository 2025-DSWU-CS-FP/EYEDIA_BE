package com.eyedia.eyedia.service.eval.impl;

import com.eyedia.eyedia.domain.badge.Badge;
import com.eyedia.eyedia.domain.enums.badge.AggregationType;
import com.eyedia.eyedia.dto.BadgeEventDTO;
import com.eyedia.eyedia.service.eval.BadgeEvaluator;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Component
public class CountEvaluator implements BadgeEvaluator {
    @Override public AggregationType supports() { return AggregationType.COUNT; }

    @Override
    public void apply(Badge p, Map<String,Object> params, BadgeEventDTO event) {
        String distinctBy = params == null ? null : (String) params.get("distinctBy");
        if (distinctBy == null) {
            p.setCurrentValue(p.getCurrentValue() + 1);
            return;
        }
        Object key = event.getPayload() == null ? null : event.getPayload().get(distinctBy);
        if (key == null) {
            p.setCurrentValue(p.getCurrentValue() + 1);
            return;
        }
        String k = String.valueOf(key);
        if (!Objects.equals(p.getLastDistinctKey(), k)) {
            p.setCurrentValue(p.getCurrentValue() + 1);
            p.setLastDistinctKey(k);
        }
    }
}

