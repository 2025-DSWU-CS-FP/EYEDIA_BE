package com.eyedia.eyedia.controller;

import com.eyedia.eyedia.domain.enums.badge.ProgressStatus;
import com.eyedia.eyedia.dto.BadgeDTO;
import com.eyedia.eyedia.dto.BadgeEventDTO;
import com.eyedia.eyedia.global.ApiResponse;
import com.eyedia.eyedia.service.BadgeEngine;
import com.eyedia.eyedia.service.BadgeQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/badges")
@RequiredArgsConstructor
@Validated
public class BadgeEventController {

    private final BadgeEngine engine;
    private final BadgeQueryService badgeQueryService;

    @Operation(summary = "뱃지 이벤트 발생 API",
            description = "뱃지 이벤트 발생.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
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

    @Operation(summary = "뱃지 조회 API",
            description = "뱃지 상태 (LOCKED/IN_PROGRESS/ACHIEVED) 조건으로 조회")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    @GetMapping
    public ApiResponse<BadgeDTO.BadgeSummaryDto> getBadgeByFilter(
            @AuthenticationPrincipal String userId,
            @RequestParam(required = false) ProgressStatus status
    ) {
        var uid = Long.parseLong(userId);

        // 필터링
        var badgeSummary = badgeQueryService.getSummary(uid, status);

        // 반환
        return ApiResponse.onSuccess(badgeSummary);

    }

}