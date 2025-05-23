package com.eyedia.eyedia.controller;

import com.eyedia.eyedia.dto.UserFacingDTO.PaintingArtistResponse;
import com.eyedia.eyedia.dto.UserFacingDTO.PaintingBackgroundResponse;
import com.eyedia.eyedia.dto.UserFacingDTO.PaintingConfirmResponse;
import com.eyedia.eyedia.service.UserFacingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/paintings")
@RequiredArgsConstructor
@Tag(name = "User Facing Controller", description = "사용자가 그림 정보를 확인할 수 있는 API")
public class UserFacingController {

    private final UserFacingService userFacingService;

    @Operation(summary = "그림 확인", description = "사용자가 그림을 확인하고 채팅을 시작여부를 선택합니다.")
    @PostMapping("/{paintingId}/confirm")
    public ResponseEntity<PaintingConfirmResponse> confirmPainting(@PathVariable Long paintingId) {
        return ResponseEntity.ok(userFacingService.confirmPainting(paintingId));
    }

    @Operation(summary = "그림 설명 조회", description = "DB에서 그림에 대한 전체적인 설명을 조회합니다.")
    @GetMapping("/{paintingId}/db-description")
    public ResponseEntity<?> getDescription(@PathVariable Long paintingId) {
        // userFacingService.getLatestDescription(paintingId)
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "작가 정보 조회", description = "그림 작가의 정보를 조회합니다.")
    @GetMapping("/{paintingId}/artist")
    public ResponseEntity<PaintingArtistResponse> getArtistInfo(@PathVariable Long paintingId) {
        return ResponseEntity.ok(userFacingService.getArtistInfo(paintingId));
    }

    @Operation(summary = "배경 정보 조회", description = "그림의 배경 정보를 조회합니다.")
    @GetMapping("/{paintingId}/background")
    public ResponseEntity<PaintingBackgroundResponse> getBackgroundInfo(@PathVariable Long paintingId) {
        return ResponseEntity.ok(userFacingService.getBackgroundInfo(paintingId));
    }
}
