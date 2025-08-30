package com.eyedia.eyedia.dto;

import com.eyedia.eyedia.domain.enums.badge.EventType;
import com.eyedia.eyedia.domain.enums.badge.ProgressStatus;
import com.eyedia.eyedia.domain.enums.badge.BadgeType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class BadgeDTO {

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class BadgeResponseDTO {
        Integer allValue;
        Integer achievedValue;
        String nextGoal;
        List<BadgeDetailDTO> badges;

    }
    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class BadgeDetailDTO {
        Long badgeId;
        String title;
        String description;
        BadgeType type;
        Integer goalValue;
        Integer currentValue;
        ProgressStatus status;

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
