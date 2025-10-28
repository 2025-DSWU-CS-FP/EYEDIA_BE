package com.eyedia.eyedia.controller;

import com.eyedia.eyedia.domain.Message;
import com.eyedia.eyedia.domain.enums.SenderType;
import com.eyedia.eyedia.dto.MessageDTO;
import com.eyedia.eyedia.global.error.exception.GeneralException;
import com.eyedia.eyedia.global.error.status.ErrorStatus;
import com.eyedia.eyedia.repository.MessageRepository;
import com.eyedia.eyedia.repository.PaintingRepository;
import com.eyedia.eyedia.service.DocentChatService;
import com.eyedia.eyedia.service.TtsService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final DocentChatService docentChatService;
    private final PaintingRepository paintingRepository;
    private final MessageRepository messageRepository;

    @MessageMapping("/ask")
    public void sendMessage(@Payload MessageDTO.AskRequest req, Principal principal) {
        var p = paintingRepository.findByPaintingId(req.getPaintingId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.PAINTING_NOT_FOUND));

        Message q = Message.builder()
                .sender(SenderType.USER)
                .painting(p)
                .content(req.getText())
                .build();
        messageRepository.save(q);

        var answer = docentChatService.answer(docentChatService.basePrompt(p, req.getText()));

        Message a = Message.builder()
                .sender(SenderType.ASSISTANT)
                .painting(p)
                .content(answer.text())
                .build();
        messageRepository.save(a);

        // 프론트로도 브로드캐스트
        var dto = MessageDTO.ChatAnswerDTO.builder()
                .paintingId(req.getPaintingId())
                .answer(answer.text())
                .model(answer.model())
                .build();

        messagingTemplate.convertAndSendToUser(principal.getName(),"/room/" + req.getPaintingId(), dto);
    }
}