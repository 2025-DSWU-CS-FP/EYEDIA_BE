package com.eyedia.eyedia.controller;

import com.eyedia.eyedia.dto.PaintingMetadataRequest;
import com.eyedia.eyedia.dto.UserFacingDTO;
import com.eyedia.eyedia.global.ApiResponse;
import com.eyedia.eyedia.global.error.status.SuccessStatus;
import com.eyedia.eyedia.service.PaintingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/paintings")
@RequiredArgsConstructor
@Tag(name = "Painting Controller", description = "사용자가 그림 정보를 확인할 수 있는 API")
public class PaintingController {

    private final PaintingService paintingService;

    @PostMapping("/save")
    @Operation(summary = "그림 메타데이터 저장", description = "모델 서버가 전송한 그림 메타데이터(objectId, title, artist, description, exhibition, imageUrl)를 DB에 저장합니다.")
    public ApiResponse<?> saveMetadata(@RequestBody PaintingMetadataRequest request) {
        paintingService.saveMetadata(request);
        return ApiResponse.of(SuccessStatus._OK,null);
    }

    @Operation(summary = "채팅방 생성", description = "사용자가 그림을 확인하고 채팅을 시작여부를 선택합니다.")
    @PostMapping("/{paintingId}/confirm")
    public ResponseEntity<UserFacingDTO.PaintingConfirmResponse> confirmPainting(@PathVariable Long paintingId) {
        return ResponseEntity.ok(paintingService.confirmPainting(paintingId));
    }

    @Operation(summary = "채팅 메시지 목록 조회", description = "특정 그림에 대한 사용자-AI 대화 내역을 조회합니다.")
    @GetMapping("/{paintingId}/chats")
    public ResponseEntity<?> getChatMessages(@PathVariable Long paintingId) {
        return ResponseEntity.ok(paintingService.getChatMessagesByPaintingId(paintingId));
    }

    @Operation(summary = "작품 삭제", description = "null user인 painting이 삭제됩니다.")
    @DeleteMapping
    public ApiResponse<?> deletePainting() {
        return ApiResponse.of(SuccessStatus._OK,"해당 painting 삭제 완료 : " + paintingService.deletePainting());
    }
}
