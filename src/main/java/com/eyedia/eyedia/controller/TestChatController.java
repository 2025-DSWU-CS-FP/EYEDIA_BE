package com.eyedia.eyedia.controller;

import com.eyedia.eyedia.repository.MessageRepository;
import com.eyedia.eyedia.repository.PaintingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;
@Controller
@RequiredArgsConstructor
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
        return message;
    }

}
