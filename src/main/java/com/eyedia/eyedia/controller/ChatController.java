package com.eyedia.eyedia.controller;

import com.eyedia.eyedia.domain.Message;
import com.eyedia.eyedia.domain.Painting;
import com.eyedia.eyedia.domain.enums.SenderType;
import com.eyedia.eyedia.dto.AiToBackendDTO;
import com.eyedia.eyedia.dto.MessageDTO;
import com.eyedia.eyedia.repository.MessageRepository;
import com.eyedia.eyedia.repository.PaintingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final RestTemplate restTemplate;
    private static final String MODEL_API_URL = "http://localhost:8000/api/llm/answer"; // FastAPI 주소
    private final PaintingRepository paintingRepository;
    private final MessageRepository messageRepository;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload MessageDTO.ChatMessageDTO message,
                            @Header("simpSessionAttributes") Map<String, Object> sessionAttributes) {
        Long userId = (Long) sessionAttributes.get("userId");

        if (userId == null) return; // 인증 실패 처리

        // 사용자 본인의 방에만 메시지 전송
        messagingTemplate.convertAndSend("/room/user-" + userId, message);
    }

    // [1] 사용자 질문 → FastAPI 전달
    @MessageMapping("/chat.question")
    public void handleQuestion(@Payload MessageDTO.ChatMessageDTO request,
                               @Header("simpSessionAttributes") Map<String, Object> attrs) {

        Long userId = (Long) attrs.get("userId");
        if (userId == null) return;

        Painting painting = paintingRepository.findById(request.getPaintingId()).orElse(null);
        if (painting == null) return;

        // 사용자 질문 저장
        Message userMessage = Message.builder()
                .sender(SenderType.USER)
                .content(request.getContent())
                .painting(painting)
                .build();
        messageRepository.save(userMessage);

        // FastAPI 호출
        Map<String, Object> payload = Map.of(
                "question", request.getContent(),
                "painting_id", request.getPaintingId()
        );

        //Todo(채민): 백엔드 -> 모델 전송 부분

        ResponseEntity<String> response = restTemplate.postForEntity(MODEL_API_URL, payload, String.class);
        // AI 응답 메시지 저장
        Message aiMessage = Message.builder()
                .sender(SenderType.ASSISTANT)
                .content(response.getBody())
                .painting(painting).build();
        messageRepository.save(userMessage);

        // AI 응답 DTO
        AiToBackendDTO.ObjectDescriptionRequest aiMessageDto = AiToBackendDTO.ObjectDescriptionRequest.builder()
                .sendingType(SenderType.ASSISTANT)
                .description(response.getBody())
                .paintingId(request.getPaintingId())
                .build();
        messageRepository.save(userMessage);

        messagingTemplate.convertAndSend("/room/user-" + userId, aiMessage);
    }



}


