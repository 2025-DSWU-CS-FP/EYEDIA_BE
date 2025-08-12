package com.eyedia.eyedia.controller;

import com.eyedia.eyedia.domain.Message;
import com.eyedia.eyedia.domain.enums.SenderType;
import com.eyedia.eyedia.repository.MessageRepository;
import com.eyedia.eyedia.repository.PaintingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;
@Controller
@RequiredArgsConstructor
@Transactional
public class TestChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final RestTemplate restTemplate;
    private static final String MODEL_API_URL = "http://localhost:8000/api/llm/answer"; // FastAPI 실제 API 주소
    private final PaintingRepository paintingRepository;
    private final MessageRepository messageRepository;

    @MessageMapping("/echo/{roomId}")
    @SendTo("/room/{roomId}")
    public String echo(@DestinationVariable Long roomId, String message) {
        System.out.println("Echo received: " + message);
        // 구독 중인 모든 /room에 메시지 전송
        // messagingTemplate.convertAndSend("/room", "echo: " + message);
        var painting = paintingRepository.getReferenceById(roomId);
        var saved = messageRepository.save(
                Message.builder()
                        .content(message)
                        .painting(painting)
                        .sender(SenderType.USER)
                        .build()
        );
        return message + " : savedTo => " + saved.getMessageId();
    }

}
