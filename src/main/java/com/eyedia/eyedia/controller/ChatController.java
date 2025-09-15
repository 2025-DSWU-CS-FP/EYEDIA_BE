package com.eyedia.eyedia.controller;

import com.eyedia.eyedia.dto.MessageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload MessageDTO.ChatMessageDTO m) {
        String topic = "/topic/chat/art/" + m.getPaintingId();
        messagingTemplate.convertAndSend(topic, m);
    }
}