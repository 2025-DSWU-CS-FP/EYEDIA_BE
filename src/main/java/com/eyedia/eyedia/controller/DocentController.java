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
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chats")
public class DocentController {

    private final DocentChatService docentChatService;
    private final SimpMessagingTemplate messagingTemplate;

    private final TtsService ttsService;
    private final PaintingRepository paintingRepository;
    private final MessageRepository messageRepository;

    @PostMapping("/ask")
    public MessageDTO.ChatAnswerDTO ask(@RequestBody MessageDTO.AskRequest req, Principal principal) {
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

        String audioUrl = ttsService.synthesizeAndGetUrl(answer.text(), "alloy");

        // 젯슨에 재생시키도록 작업 큐에 넣기 (폴링 A안)
        /*
        if(req.getDeviceId() != null && !req.getDeviceId().isBlank()) {
            var task = DeviceTask.builder()
                    .taskId(UUID.randomUUID().toString())
                    .type("PLAY_AUDIO")
                    .audioUrl(audioUrl)
                    .text(answer.text())
                    .createdAt(System.currentTimeMillis())
                    .build();
            deviceTaskService.enqueue(req.getDeviceId(), task);
        }**/

        // 프론트로도 브로드캐스트
        var dto = MessageDTO.ChatAnswerDTO.builder()
                .paintingId(req.getPaintingId())
                .answer(answer.text())
                .model(answer.model())
                .audioUrl(audioUrl)
                .build();

        messagingTemplate.convertAndSendToUser(principal.getName(),"/room/" + req.getPaintingId(), dto);
        return dto;
    }
}
