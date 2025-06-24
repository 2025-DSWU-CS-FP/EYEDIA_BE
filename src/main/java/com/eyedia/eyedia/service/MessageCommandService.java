package com.eyedia.eyedia.service;

import com.eyedia.eyedia.dto.AiToBackendDTO;
import com.eyedia.eyedia.repository.MessageRepository;
import com.eyedia.eyedia.repository.PaintingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageCommandService {

    private final MessageRepository messageRepository; // DB 저장
    private final PaintingRepository paintingRepository;

    public void save(AiToBackendDTO.ObjectDescriptionRequest request) {

        // 콘솔 출력
        System.out.println("📌 객체: " + request.getObjectId());
        System.out.println("📘 설명: " + request.getDescription());

        /*Long paintingId = Long.valueOf(request.getObjectId());

        Painting painting = paintingRepository.findById(paintingId)
                .orElseThrow(() -> new IllegalArgumentException("그림이 존재하지 않습니다. ID=" + paintingId));

        Message message = Message.builder()
                .painting(painting)
                .sender(SenderType.ASSISTANT)
                .content(request.getDescription())
                .build();

        messageRepository.save(message);


    }*/
    }


}