package com.eyedia.eyedia.service.eval;


import com.eyedia.eyedia.domain.badge.Badge;
import com.eyedia.eyedia.domain.enums.badge.AggregationType;
import com.eyedia.eyedia.dto.BadgeEventDTO;

import java.util.Map;

public interface BadgeEvaluator {
    AggregationType supports();
    void apply(Badge progress, Map<String,Object> params, BadgeEventDTO event);
}
