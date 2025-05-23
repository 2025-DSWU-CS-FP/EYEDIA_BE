package com.eyedia.eyedia.controller;

import com.eyedia.eyedia.dto.UserFacingDTO.*;
import com.eyedia.eyedia.service.UserFacingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/paintings")
@RequiredArgsConstructor
public class UserFacingController {

    private final UserFacingService userFacingService;

    // [1] 사용자 그림 확인 후 채팅 시작
    @PostMapping("/{paintingId}/confirm")
    public ResponseEntity<PaintingConfirmResponse> confirmPainting(@PathVariable Long paintingId) {
        return ResponseEntity.ok(userFacingService.confirmPainting(paintingId)); // paintingId로 나머지 데이터 가져오기(추가적으로)
    }

    // [2] 그림에 대한 전체적인 설명을 DB에서 조회(객체 아님)
//    @GetMapping("/{paintingId}/db-description")
//    public ResponseEntity<PaintingDescriptionResponse> getDescription(@PathVariable Long paintingId) {
//        return ResponseEntity.ok(userFacingService.getLatestDescription(paintingId));
//    }

    // [3] 작가 정보
    @GetMapping("/{paintingId}/artist")
    public ResponseEntity<PaintingArtistResponse> getArtistInfo(@PathVariable Long paintingId) {
        return ResponseEntity.ok(userFacingService.getArtistInfo(paintingId));
    }

    // [4] 배경 정보
    @GetMapping("/{paintingId}/background")
    public ResponseEntity<PaintingBackgroundResponse> getBackgroundInfo(@PathVariable Long paintingId) {
        return ResponseEntity.ok(userFacingService.getBackgroundInfo(paintingId));
    }
}
