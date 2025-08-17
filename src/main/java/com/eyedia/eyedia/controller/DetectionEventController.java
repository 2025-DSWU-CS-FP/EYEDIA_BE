package com.eyedia.eyedia.controller;

import com.eyedia.eyedia.dto.MessageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/events")
public class DetectionEventController {

    private final SimpMessagingTemplate messagingTemplate;
    /**
     * 25.08.17 <인식된 사진 프론트 전송>
     * request : 방 데이터
     * return : 이미지 id와 url
     * */
    @PostMapping("/mock-detect")
    public ResponseEntity<Void> mockDetect(Principal principal) {
        String userKey = principal.getName(); // <-- WebSocket CONNECT에서 setUser(...) 한 값
        String img = "https://upload.wikimedia.org/wikipedia/commons/b/b7/Edgar_Degas_The_Dance_Class.jpg";
        messagingTemplate.convertAndSendToUser(
                userKey,
                "/queue/events",
                MessageDTO.ChatImageResponseDTO.builder()
                        .url(img)
                        .artId(20001L)
                        .build()
        );
        return ResponseEntity.ok().build();
    }

}