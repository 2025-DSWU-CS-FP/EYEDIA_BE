package com.eyedia.eyedia.controller;

import com.eyedia.eyedia.domain.Painting;
import com.eyedia.eyedia.dto.AiToBackendDTO;
import com.eyedia.eyedia.repository.PaintingRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AiToBackendController {

    private final PaintingRepository paintingRepository;

    @Operation(
            summary = "AI가 객체 설명을 전달",
            description = "AI 모델이 감지한 객체 설명을 백엔드로 전송합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = AiToBackendDTO.ObjectDescriptionRequest.class)
                    )
            )
    )
    @ApiResponse(responseCode = "200", description = "설명이 저장되었습니다.")
    @PostMapping("/object-description")
    public ResponseEntity<Void> receiveObjectDescription(@RequestBody AiToBackendDTO.ObjectDescriptionRequest request) {

        try {
            Long paintingId = request.getPaintingId();
            Painting painting = paintingRepository.findById(paintingId).orElse(null);

            if (painting != null) {
                // 🔁 기존 그림 업데이트
                painting.setDescription(request.getDescription());
                painting.setTitle(request.getTitle());
                painting.setArtist(request.getArtist());
                painting.setImageUrl(request.getImageurl());
                painting.setObjectId(request.getObjectId());
                log.info("🛠 기존 Painting 업데이트 - ID: {}", paintingId);
            } else {
                // 🆕 새 그림 생성
                painting = Painting.builder()
                        .description(request.getDescription())
                        .title(request.getTitle())
                        .artist(request.getArtist())
                        .imageUrl(request.getImageurl())
                        .objectId(request.getObjectId())
                        .build();
                log.info("🎨 새로운 Painting 생성 - ID: {}", paintingId);
            }

            Painting saved = paintingRepository.save(painting);

            log.info("✅ Painting 저장 완료 - ID: {}", saved.getPaintingId());
            log.info("🔍 저장된 내용: {}", request);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("❌ Painting 저장 실패: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
