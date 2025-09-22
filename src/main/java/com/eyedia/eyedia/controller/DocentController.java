package com.eyedia.eyedia.controller;

import com.eyedia.eyedia.dto.DeviceTask;
import com.eyedia.eyedia.dto.MessageDTO;
import com.eyedia.eyedia.service.DeviceTaskService;
import com.eyedia.eyedia.service.DocentChatService;
import com.eyedia.eyedia.service.TtsService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
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
    public MessageDTO.ChatAnswerDTO ask(@RequestBody MessageDTO.AskRequest req, Principal principal) {
        var answer = docentChatService.answer(req.getPaintingId(), req.getText());
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
                .paintingId(req.getPaintingId())
                .answer(answer.text())
                .model(answer.model())
                .audioUrl(audioUrl)
                .build();

        messagingTemplate.convertAndSendToUser(principal.getName(),"/room/user-" + principal.getName(), dto);
        return dto;
    }
}
