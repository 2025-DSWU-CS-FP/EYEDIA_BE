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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final RestTemplate restTemplate;
    private static final String MODEL_API_URL = "http://localhost:8000/api/llm/answer"; // FastAPI 실제 API 주소
    private final PaintingRepository paintingRepository;
    private final MessageRepository messageRepository;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload MessageDTO.ChatMessageDTO message,
                            @Header("simpSessionAttributes") Map<String, Object> sessionAttributes) {
        Long userId = (Long) sessionAttributes.get("userId");
        if (userId == null) return;

        messagingTemplate.convertAndSend("/room/user-" + userId, message);
    }

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
        String aiContent;
        try {
            Map<String, Object> payload = Map.of(
                    "paintingId", painting.getPaintingsId(),
                    "sendingType", SenderType.USER.name(),
                    "descriptionId", request.getContent()
            );

            ResponseEntity<MessageDTO.ModelResponseDTO> response = restTemplate.postForEntity(
                    MODEL_API_URL, payload, MessageDTO.ModelResponseDTO.class
            );

            MessageDTO.ModelResponseDTO responseBody = response.getBody();
            aiContent = responseBody != null ? responseBody.getObjectDescription() : "AI 응답 없음";

        } catch (Exception e) {
            aiContent = "AI 응답을 불러오는 데 실패했습니다.";
        }

        // AI 응답 메시지 저장
        Message aiMessage = Message.builder()
                .sender(SenderType.ASSISTANT)
                .content(aiContent)
                .painting(painting)
                .build();
        messageRepository.save(aiMessage);

        // 프론트 전송용 DTO 생성
        AiToBackendDTO.ObjectDescriptionRequest aiMessageDto = AiToBackendDTO.ObjectDescriptionRequest.builder()
                .sendingType(SenderType.ASSISTANT)
                .description(aiMessage.getContent())
                .paintingId(aiMessage.getPainting().getPaintingsId())
                .build();

        messagingTemplate.convertAndSend("/room/user-" + userId, aiMessageDto);
    }
    @PostMapping("/chat/send-ai-message")
    public ResponseEntity<String> sendAiMessage(@RequestBody AiToBackendDTO.ObjectDescriptionRequest request) {
        Long paintingId = request.getPaintingId();
        String description = request.getDescription();

        Painting painting = paintingRepository.findById(paintingId).orElse(null);
        if (painting == null) return ResponseEntity.badRequest().body("Invalid paintingId");

        // 메시지 저장
        Message aiMessage = Message.builder()
                .sender(SenderType.ASSISTANT)
                .content(description)
                .painting(painting)
                .build();
        messageRepository.save(aiMessage);

        // userId 정보를 어떻게 알 것인가?
        // painting 객체와 연결된 user가 존재한다면 그 userId로 채팅방 지정
        Long userId = painting.getUser().getUsersId(); // 예시: Painting에 getUser()가 있다고 가정
        if (userId == null) return ResponseEntity.badRequest().body("User not found for painting");

        // 메시지 전송
        messagingTemplate.convertAndSend("/room/user-" + userId, request);

        return ResponseEntity.ok("Message sent");
    }

}
