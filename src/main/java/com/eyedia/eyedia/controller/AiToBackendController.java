package com.eyedia.eyedia.controller;

import com.eyedia.eyedia.dto.AiToBackendDTO;
import com.eyedia.eyedia.service.MessageCommandService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/api/vi/ai")
@RequiredArgsConstructor
public class AiToBackendController {


    // ㅎㅐ당 그림이 맞는지 물어봄 yes-> 채팅방 시작 . 모델에서 -> 백엔드로 거치고 -> 프론트

    // ai -> 백엔드. 객체 인식 시, 전체 이미지 id, 크롭 객체 id, llm생성 설명 - o
    private final MessageCommandService messageService;

    @Operation(summary = "AI가 그림을 감지했는지 여부 전달", description = "그림이 올바르게 감지되었는지 여부를 백엔드에 전달합니다.")
    @PostMapping("/painting-detection")
    public ResponseEntity<Void> detectPainting(@RequestBody AiToBackendDTO.PaintingDescriptionRequest request) {

        // 프론트로 이동

        return ResponseEntity.ok().build();
    }
    // no 일 경우 다시 찾아야함
    @Operation(summary = "재촬영 요청", description = "AI가 재인식이 필요하다고 판단하여 재촬영 요청을 보냅니다.")
    @PostMapping("/recapture")
    public ResponseEntity<?> requestReCapture(@RequestBody AiToBackendDTO.RecaptureResponse request) {
        // boolean sent = modelSignalService.sendRecaptureSignal(request.getPaintingId(), request.getReason());

        // 모델에게 재인식 요청
        return ResponseEntity.ok().build();
    }


    // [2] 객체 설명 수신
    // ai -> 백엔드. 객체 인식 시, 전체 이미지 id, 크롭 객체 id, llm생성 설명 - o
    @Operation(summary = "AI가 객체 설명을 전달", description = "AI 모델이 감지한 객체 설명을 백엔드로 전송합니다.")
    @ApiResponse(responseCode = "200", description = "설명이 저장되었습니다.")
    @PostMapping("/object-description")
    public ResponseEntity<Void> receiveObjectDescription(@RequestBody AiToBackendDTO.ObjectDescriptionRequest request) {

        messageService.save(request);

        // 메세지 dto 프론트로 전달

        return ResponseEntity.ok().build();
    }

}

