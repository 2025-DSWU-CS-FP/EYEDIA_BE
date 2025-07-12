package com.eyedia.eyedia.controller;

import com.eyedia.eyedia.config.s3.S3Manager;
import com.eyedia.eyedia.dto.PaintingMetadataRequest;
import com.eyedia.eyedia.dto.UserFacingDTO.PaintingConfirmResponse;
import com.eyedia.eyedia.global.ApiResponse;
import com.eyedia.eyedia.global.error.status.SuccessStatus;
import com.eyedia.eyedia.service.PaintingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/v1/paintings")
@RequiredArgsConstructor
@Tag(name = "Painting Controller", description = "사용자가 그림 정보를 확인할 수 있는 API")
public class PaintingController {

    private final PaintingService paintingService;
    private final S3Manager s3Manager;

    @PostMapping("/upload")
    @Operation(summary = "이미지 업로드", description = "모델 서버가 전송한 그림 이미지를 S3에 업로드하고 이미지 URL을 반환합니다.")
    public ApiResponse<String> uploadImage(
            @RequestParam("image") MultipartFile image,
            @RequestParam("exhibition") String exhibition,
            @RequestParam("title") String title) {

        String keyName = s3Manager.generatePaintingKeyName(exhibition, title);
        String imageUrl = s3Manager.uploadFile(keyName, image);

        return ApiResponse.of(SuccessStatus._OK,imageUrl);
    }

    @PostMapping("/save")
    @Operation(summary = "그림 메타데이터 저장", description = "모델 서버가 전송한 그림 메타데이터(objectId, title, artist, description, exhibition, imageUrl)를 DB에 저장합니다.")
    public ApiResponse<?> saveMetadata(@RequestBody PaintingMetadataRequest request) {
        paintingService.saveMetadata(request);
        return ApiResponse.of(SuccessStatus._OK,null);
    }

    @Operation(summary = "그림 확인", description = "사용자가 그림을 확인하고 채팅을 시작여부를 선택합니다.")
    @PostMapping("/{paintingId}/confirm")
    public ResponseEntity<PaintingConfirmResponse> confirmPainting(@PathVariable Long paintingId) {
        // confirm 후 채팅 시작
        return ResponseEntity.ok(paintingService.confirmPainting(paintingId));
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
        return ResponseEntity.ok(paintingService.getChatMessagesByPaintingId(chatRoomId));
    }
    @RequestMapping("/test")
    public String testAPI(){
        return "test";
    }

}
