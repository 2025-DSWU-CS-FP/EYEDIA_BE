package com.eyedia.eyedia.dto;

import com.eyedia.eyedia.domain.enums.badge.EventType;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class BadgeDTO {
    @Getter @Setter @Builder
    public static class BadgeSummaryDto {
        private int acquired;
        private int total;
        private BadgeCardDto nextTarget;
        private List<BadgeCardDto> badges;
    }

    @Getter @Setter @Builder
    public static class BadgeCardDto {
        private String code;
        private String title;
        private String description;
        private String status; // ACQUIRED | IN_PROGRESS | LOCKED
        private LocalDateTime awardedAt; // 획득이면 값 존재
        Integer goalValue;
        Integer currentValue;
        boolean newBadge;
    }

    @Getter @Setter
    @Builder
    @NoArgsConstructor @AllArgsConstructor
    public static class BadgeEventRequestDTO {
        private String eventUid;
        private EventType type;
        private Map<String, Object> payload;
        private LocalDateTime occurredAt;

    }
}
