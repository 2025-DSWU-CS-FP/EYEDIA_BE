package com.eyedia.eyedia.controller;

import com.eyedia.eyedia.dto.DeviceTask;
import com.eyedia.eyedia.dto.MessageDTO;
import com.eyedia.eyedia.service.DeviceTaskService;
import com.eyedia.eyedia.service.DocentChatService;
import com.eyedia.eyedia.service.TtsService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chats")
public class DocentController {

    private final DocentChatService docentChatService;
    private final SimpMessagingTemplate messagingTemplate;

    private final TtsService ttsService;
    private final DeviceTaskService deviceTaskService;

    @PostMapping("/ask")
    public MessageDTO.ChatAnswerDTO ask(@RequestBody MessageDTO.AskRequest req) {
        var answer = docentChatService.answer(req.getArtId(), req.getText());
        String audioUrl = ttsService.synthesizeAndGetUrl(answer.text(), "alloy");

        // 젯슨에 재생시키도록 작업 큐에 넣기 (폴링 A안)
        if(req.getDeviceId() != null && !req.getDeviceId().isBlank()) {
            var task = DeviceTask.builder()
                    .taskId(UUID.randomUUID().toString())
                    .type("PLAY_AUDIO")
                    .audioUrl(audioUrl)
                    .text(answer.text())
                    .createdAt(System.currentTimeMillis())
                    .build();
            deviceTaskService.enqueue(req.getDeviceId(), task);
        }

        // 프론트로도 브로드캐스트
        var dto = MessageDTO.ChatAnswerDTO.builder()
                .artId(req.getArtId())
                .answer(answer.text())
                .model(answer.model())
                .audioUrl(audioUrl)
                .build();

        // Todo : 메세지 DB 저장 (보내는 거 받는 거 모두) , 메세지에도 userId 저장, painting에도 userId 저장

        // ✅ 질문/답변 모두 같은 작품 채널로 브로드캐스트
        messagingTemplate.convertAndSend("/topic/chat/art/" + req.getArtId(), dto);
        return dto;
    }
}
