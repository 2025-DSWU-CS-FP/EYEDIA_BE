package com.eyedia.eyedia.controller;

import com.eyedia.eyedia.dto.BadgeDTO;
import com.eyedia.eyedia.dto.BadgeEventDTO;
import com.eyedia.eyedia.global.ApiResponse;
import com.eyedia.eyedia.service.BadgeEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/vi/badge")
@RequiredArgsConstructor
@Validated
public class BadgeEventController {

    private final BadgeEngine engine;

    @PostMapping("/events")
    public ApiResponse<?> ingest(@AuthenticationPrincipal String userId,
                                    @RequestBody BadgeDTO.BadgeEventRequestDTO req) {
        Long uid = Long.parseLong(userId);

        String eventUid = (req.getEventUid() == null || req.getEventUid().isBlank())
                ? "evt-" + UUID.randomUUID()
                : req.getEventUid();

        LocalDateTime occurredAt = (req.getOccurredAt() == null)
                ? LocalDateTime.now()
                : req.getOccurredAt();

        BadgeEventDTO fixed = BadgeEventDTO.builder()
                .userId(uid)
                .eventUid(eventUid)
                .occurredAt(occurredAt)
                .payload(req.getPayload())
                .type(req.getType())
                .build();

        // 이벤트 발생
        engine.process(fixed);

        // 반환
        return ApiResponse.onSuccessWithoutResult();
    }

}