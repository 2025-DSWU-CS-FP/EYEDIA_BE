package com.eyedia.eyedia.controller;

import com.eyedia.eyedia.dto.UserFacingDTO.PaintingConfirmResponse;
import com.eyedia.eyedia.service.UserFacingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/paintings")
@RequiredArgsConstructor
@Tag(name = "User Facing Controller", description = "사용자가 그림 정보를 확인할 수 있는 API")
public class UserFacingController {

    private final UserFacingService userFacingService;

    @Operation(summary = "그림 확인", description = "사용자가 그림을 확인하고 채팅을 시작여부를 선택합니다.")
    @PostMapping("/{paintingId}/confirm")
    public ResponseEntity<PaintingConfirmResponse> confirmPainting(@PathVariable Long paintingId) {
        // confirm 후 채팅 시작
        return ResponseEntity.ok(userFacingService.confirmPainting(paintingId));
    }

    @Operation(summary = "그림 설명 조회", description = "DB에서 그림에 대한 전체적인 설명을 조회합니다.")
    @GetMapping("/{paintingId}/db-description")
    public ResponseEntity<?> getDescription(@PathVariable Long paintingId) {
        // userFacingService.getLatestDescription(paintingId)
        return ResponseEntity.ok().build();
    }
    // 채팅 히스토리 가져오기
    @Operation(summary = "채팅 메시지 목록 조회", description = "특정 그림에 대한 사용자-AI 대화 내역을 조회합니다.")
    @GetMapping("/{chatRoomId}/chats")
    public ResponseEntity<?> getChatMessages(@PathVariable Long chatRoomId) {
        return ResponseEntity.ok(userFacingService.getChatMessagesByPaintingId(chatRoomId));
    }
    @RequestMapping("/test")
    public String testAPI(){
        return "test";
    }

}
