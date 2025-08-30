package com.eyedia.eyedia.service.eval;

import com.eyedia.eyedia.domain.enums.badge.AggregationType;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class BadgeEvaluatorRegistry {

    private final Map<AggregationType, BadgeEvaluator> map;

    public BadgeEvaluatorRegistry(List<BadgeEvaluator> evaluators) {
        Map<AggregationType, BadgeEvaluator> m = new EnumMap<>(AggregationType.class);
        for (var e : evaluators) {
            var prev = m.put(e.supports(), e);
            if (prev != null) {
                throw new IllegalStateException("Duplicated evaluator for " + e.supports());
            }
        }
        this.map = Collections.unmodifiableMap(m);
    }

    public BadgeEvaluator require(AggregationType type) {
        var eval = map.get(type);
        if (eval == null) throw new IllegalStateException("No evaluator for " + type);
        return eval;
    }

    public BadgeEvaluator get(AggregationType type) { return map.get(type); }
}
