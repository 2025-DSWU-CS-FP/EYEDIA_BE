package com.eyedia.eyedia.dto;

import com.eyedia.eyedia.domain.enums.badge.EventType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class BadgeEventDTO {

    private String eventUid;                // 없으면 엔진에서 UUID 생성 가능
    private EventType type;                 // EXHIBITION_COLLECTED / ART_VIEWED / VISIT_LOGGED
    private Long userId;
    private LocalDateTime occurredAt;
    private Map<String, Object> payload;    // { "exhibitionId":123, "paintingId":456, ... }

}
