package com.eyedia.eyedia.service;

import com.eyedia.eyedia.config.SecurityUtil;
import com.eyedia.eyedia.domain.Painting;
import com.eyedia.eyedia.domain.User;
import com.eyedia.eyedia.dto.UserFacingDTO.PaintingArtistResponse;
import com.eyedia.eyedia.dto.UserFacingDTO.PaintingBackgroundResponse;
import com.eyedia.eyedia.dto.UserFacingDTO.PaintingConfirmResponse;
import com.eyedia.eyedia.repository.MessageRepository;
import com.eyedia.eyedia.repository.PaintingRepository;
import com.eyedia.eyedia.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserFacingService {
    private final PaintingRepository paintingRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public PaintingConfirmResponse confirmPainting(Long paintingId) {
        Long userId = SecurityUtil.getCurrentUserId();
        // TODO: error응답 수정
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        // 채팅방(PAINTING) 생성 및 사용자 연결
        Painting paintingRoom = Painting
                .builder()
                .paintingsId(paintingId).user(user).build();
        // TODO: 배경정보, 작가정보 가져오도록 수정
        paintingRepository.save(paintingRoom);

        return PaintingConfirmResponse.builder()
                .paintingId(paintingId)
                .confirmed(true)
                .message("채팅방을 시작합니다.")
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
