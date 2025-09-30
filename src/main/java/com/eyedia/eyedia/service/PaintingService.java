package com.eyedia.eyedia.service;


import com.eyedia.eyedia.config.SecurityUtil;
import com.eyedia.eyedia.domain.Exhibition;
import com.eyedia.eyedia.domain.Painting;
import com.eyedia.eyedia.domain.User;
import com.eyedia.eyedia.dto.PaintingMetadataRequest;
import com.eyedia.eyedia.dto.UserFacingDTO;
import com.eyedia.eyedia.global.error.exception.GeneralException;
import com.eyedia.eyedia.global.error.status.ErrorStatus;
import com.eyedia.eyedia.repository.ExhibitionRepository;
import com.eyedia.eyedia.repository.MessageRepository;
import com.eyedia.eyedia.repository.PaintingRepository;
import com.eyedia.eyedia.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaintingService {
    private final PaintingRepository paintingRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ExhibitionRepository exhibitionRepository;
    private final ExhibitionCommandService exhibitionCommandService;

    public UserFacingDTO.PaintingConfirmResponse confirmPainting(Long paintingId) {
        Long userId = SecurityUtil.getCurrentUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        // 사용자 연결
        Painting paintingRoom = paintingRepository.findByPaintingId(paintingId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.PAINTING_NOT_FOUND));

        paintingRoom.setUser(user);

        Painting response = paintingRepository.save(paintingRoom);

        // 전시 방문 이력 저장
        exhibitionCommandService.addVisit(response);

        return UserFacingDTO.PaintingConfirmResponse.builder()
                .chatRoomId(response.getPaintingId())
                .paintingId(response.getPaintingId())
                .confirmed(true)
                .artId(response.getArtId())
                .message("채팅방을 시작합니다.")
                .build();
    }


//    public List<MessageDTO.ChatMessageDTO> getChatMessagesByPaintingId(Long paintingId) {
//        return messageRepository.findByPainting_PaintingIdOrderByCreatedAtAsc(paintingId).stream()
//                .map(message -> MessageDTO.ChatMessageDTO.builder()
//                        .sender(message.getSender().name())
//                        .content(message.getContent())
//                        .paintingId(paintingId)
//                        .timestamp(message.getCreatedAt().toString())
//                        .build())
//                .toList();
//        return List.of();
//    }

    public Long saveMetadata(PaintingMetadataRequest request) {
        Exhibition exhibition = exhibitionRepository.findById(request.getExhibition())
                .orElseGet(() -> exhibitionRepository.save(
                        Exhibition.builder()
                                .exhibitionsId(request.getExhibition())
                                .build()
                ));

        Painting painting = Painting.builder()
                .artId(request.getArtId())
                .title(request.getTitle())
                .artist(request.getArtist())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .exhibition(exhibition)
                .build();

        paintingRepository.save(painting);

        return painting.getPaintingId();
    }

    public void deletePainting(Long paintingId) {
        Painting painting = paintingRepository.findByPaintingId(paintingId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.PAINTING_NOT_FOUND));
        if(painting.getMessages() != null){
            painting.getMessages().forEach(message -> messageRepository.delete(message));
        }
        paintingRepository.delete(painting);
    }
}