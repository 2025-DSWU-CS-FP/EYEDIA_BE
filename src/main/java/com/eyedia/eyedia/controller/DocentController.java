package com.eyedia.eyedia.controller;

import com.eyedia.eyedia.dto.MessageDTO;
import com.eyedia.eyedia.service.DocentChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chats")
public class DocentController {

    private final DocentChatService docentChatService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/ask")
    public MessageDTO.ChatAnswerDTO ask(@RequestBody MessageDTO.AskRequest req) {
        var answer = docentChatService.answer(req.getArtId(), req.getText());
        var dto = MessageDTO.ChatAnswerDTO.builder()
                .artId(req.getArtId())
                .answer(answer.text())
                .model(answer.model())
                .build();

        // ✅ 질문/답변 모두 같은 작품 채널로 브로드캐스트
        messagingTemplate.convertAndSend("/topic/chat/art/" + req.getArtId(), dto);
        return dto;
    }
}
