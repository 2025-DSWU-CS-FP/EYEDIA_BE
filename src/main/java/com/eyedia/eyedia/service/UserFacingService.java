package com.eyedia.eyedia.service;

import com.eyedia.eyedia.domain.Painting;
import com.eyedia.eyedia.dto.UserFacingDTO.*;
import com.eyedia.eyedia.repository.MessageRepository;
import com.eyedia.eyedia.repository.PaintingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserFacingService {
    private final PaintingRepository paintingRepository;
    private final MessageRepository messageRepository;

    public PaintingConfirmResponse confirmPainting(Long id) {
        // TODO: 필요 시 DB에 채팅방 시작 로그를 남길 수 있음
        return PaintingConfirmResponse.builder()
                .paintingId(id)
                .confirmed(true)
                .message("채팅방을 시작합니다.")
                .build();
    }

    public PaintingDescriptionResponse getLatestDescription(Long id) {
        String latest = messageRepository.findLatestAiMessageByPaintingId(id).orElse("설명이 없습니다.");
        return PaintingDescriptionResponse.builder()
                .paintingId(id)
                .description(latest)
                .build();
    }

    public PaintingArtistResponse getArtistInfo(Long id) {
        Painting painting = paintingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Painting not found"));
        return PaintingArtistResponse.builder()
                .paintingId(id)
                .artist(painting.getArtist())
                .build();
    }

    public PaintingBackgroundResponse getBackgroundInfo(Long id) {
        Painting painting = paintingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Painting not found"));
        return PaintingBackgroundResponse.builder()
                .paintingId(id)
                .background(painting.getBackground())
                .build();
    }
}
