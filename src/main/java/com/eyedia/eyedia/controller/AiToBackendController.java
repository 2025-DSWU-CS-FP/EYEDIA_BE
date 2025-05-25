package com.eyedia.eyedia.controller;

import com.eyedia.eyedia.dto.AiToBackendDTO;
import com.eyedia.eyedia.service.MessageCommandService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/api/vi/ai")
@RequiredArgsConstructor
public class AiToBackendController {

    private final MessageCommandService messageService;

    @PostMapping("/painting-detection")
    public ResponseEntity<Void> detectPainting(@RequestBody AiToBackendDTO.PaintingDescriptionRequest request) {

        // 프론트로 이동

        return ResponseEntity.ok().build();
    }
    // no 일 경우 다시 찾아야함
    @PostMapping("/recapture")
    public ResponseEntity<?> requestReCapture(@RequestBody AiToBackendDTO.RecaptureResponse request) {
        // boolean sent = modelSignalService.sendRecaptureSignal(request.getPaintingId(), request.getReason());

        // 모델에게 재인식 요청
        return ResponseEntity.ok().build();
    }


    // [2] 객체 설명 수신
    // ai -> 백엔드. 객체 인식 시, 전체 이미지 id, 크롭 객체 id, llm생성 설명 - o
    @PostMapping("/object-description")
    public ResponseEntity<Void> receiveObjectDescription(@RequestBody AiToBackendDTO.ObjectDescriptionRequest request) {

        messageService.save(request);

        // 메세지 dto 프론트로 전달

        return ResponseEntity.ok().build();
    }

}

