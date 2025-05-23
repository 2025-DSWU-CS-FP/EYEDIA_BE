package com.eyedia.eyedia.service;

import com.eyedia.eyedia.domain.Message;
import com.eyedia.eyedia.domain.Painting;
import com.eyedia.eyedia.domain.enums.SenderType;
import com.eyedia.eyedia.dto.AiToBackendDTO;
import com.eyedia.eyedia.repository.MessageRepository;
import com.eyedia.eyedia.repository.PaintingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MessageCommandService {

    private final MessageRepository messageRepository;
    private final PaintingRepository paintingRepository;

    public void save(AiToBackendDTO.ObjectDescriptionRequest request) {
        Painting painting = paintingRepository.findById(request.getPaintingId())
                .orElseThrow(() -> new IllegalArgumentException("그림이 존재하지 않습니다. ID=" + request.getPaintingId()));

        Message message = Message.builder()
                .painting(painting)
                .sender(SenderType.ASSISTANT)
                .content(request.getDescription())
                .build();

        messageRepository.save(message);
    }
}
