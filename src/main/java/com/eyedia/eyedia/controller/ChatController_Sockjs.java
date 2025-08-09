package com.eyedia.eyedia.controller;

import com.eyedia.eyedia.config.SecurityUtil;
import com.eyedia.eyedia.domain.Message;
import com.eyedia.eyedia.domain.Painting;
import com.eyedia.eyedia.domain.enums.SenderType;
import com.eyedia.eyedia.dto.AiToBackendDTO;
import com.eyedia.eyedia.dto.MessageDTO;
import com.eyedia.eyedia.dto.PaintingMetadataRequest;
import com.eyedia.eyedia.repository.MessageRepository;
import com.eyedia.eyedia.repository.PaintingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ChatController_Sockjs {

    private final SimpMessagingTemplate messagingTemplate;
    private final RestTemplate restTemplate;
    private static final String MODEL_API_URL = "http://localhost:8000/api/llm/answer";
    private final PaintingRepository paintingRepository;
    private final MessageRepository messageRepository;

    // === 1) REST → 웹소켓 푸시: 세션헤더 대신 인증/파라미터로 userId 결정 ===
    @PostMapping("/paintings-push")
    public PaintingMetadataRequest pushPaintingDetected(@RequestBody PaintingMetadataRequest req /*, 필요시 @AuthenticationPrincipal ... */) {
        // 방법 A) SecurityContext에서 꺼내기 (프로젝트 헬퍼가 있다면)
        Long userId = SecurityUtil.getCurrentUserId(); // 없다면 req에 userId를 포함시키는 방법 B

        if (userId == null) {
            // fallback: 클라이언트가 userId를 바디에 담아 보내게 설계
            // Long userId = req.getUserId();
            throw new IllegalStateException("userId not resolved");
        }

        // 표준: /user/{user}/queue/paintings 로 전송
        messagingTemplate.convertAndSendToUser(
                String.valueOf(userId),
                "/queue/paintings",
                req
        );
        return req;
    }

    // === 2) STOMP sendMessage: Principal 사용 ===
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload MessageDTO.ChatMessageDTO message, Principal principal) {
        if (principal == null) return;
        String userIdStr = principal.getName(); // 채널 인터셉터에서 setUser(principal)한 값

        messagingTemplate.convertAndSendToUser(
                userIdStr,
                "/queue/chat",
                message
        );
    }

    // === 3) STOMP question: Principal 사용 + 저장 + 모델 호출 ===
    @MessageMapping("/chat.question")
    public void handleQuestion(@Payload MessageDTO.ChatMessageDTO req, Principal principal) {
        if (principal == null) return;
        String userIdStr = principal.getName();
        Long userId = parseLongSafe(userIdStr);
        if (userId == null) return;

        Painting painting = paintingRepository.findById(req.getPaintingId()).orElse(null);
        if (painting == null) {
            messagingTemplate.convertAndSendToUser(userIdStr, "/queue/errors", "Invalid paintingId");
            return;
        }

        // 사용자 질문 저장
        Message userMessage = Message.builder()
                .sender(SenderType.USER)
                .content(req.getContent())
                .painting(painting)
                .build();
        messageRepository.save(userMessage);

        // FastAPI 호출 (키 이름은 실제 API 스펙에 맞춰 조정)
        String aiContent;
        try {
            Map<String, Object> payload = Map.of(
                    "paintingId", painting.getPaintingId(),
                    "question", req.getContent(),        // ← 기존 descriptionId 대신 question 권장
                    "sender", SenderType.USER.name()
            );
            ResponseEntity<MessageDTO.ModelResponseDTO> res =
                    restTemplate.postForEntity(MODEL_API_URL, payload, MessageDTO.ModelResponseDTO.class);

            aiContent = (res.getBody() != null && res.getBody().getObjectDescription() != null)
                    ? res.getBody().getObjectDescription()
                    : "AI 응답 없음";
        } catch (Exception e) {
            aiContent = "AI 응답을 불러오는 데 실패했습니다.";
        }

        // AI 응답 저장
        Message aiMessage = Message.builder()
                .sender(SenderType.ASSISTANT)
                .content(aiContent)
                .painting(painting)
                .build();
        messageRepository.save(aiMessage);

        // 프론트 전송
        AiToBackendDTO.ObjectDescriptionRequest dto = AiToBackendDTO.ObjectDescriptionRequest.builder()
                .description(aiMessage.getContent())
                .paintingId(painting.getPaintingId())
                .title(painting.getTitle())
                .artist(painting.getArtist())
                .imageurl(painting.getImageUrl())
                .objectId(painting.getObjectId())
                .build();

        messagingTemplate.convertAndSendToUser(
                userIdStr,
                "/queue/chat",
                dto
        );
    }
    /**
     * 채팅 연결 테스트용 코드
     * 자신이 보낸 메세지 복제
     * */
    @MessageMapping("/chat.echo")
    public void echoToSelf(@Payload MessageDTO.ChatMessageDTO message, Principal principal) {
        if (principal == null) return;
        String userIdStr = principal.getName(); // 채널 인터셉터에서 setUser(principal) 한 값

         //필요하면 메시지에 보낸이/타임스탬프 세팅
         message.builder().sender(String.valueOf(SenderType.USER)).build();
        // 본인 전용 큐로 에코
        messagingTemplate.convertAndSendToUser(
                userIdStr,
                "/queue/chat",   // 프런트는 /user/queue/chat 구독
                message
        );
    }

    private static Long parseLongSafe(String v) {
        try {
            return Long.valueOf(v);
        } catch (Exception e) {
            return null;
        }
    }
}