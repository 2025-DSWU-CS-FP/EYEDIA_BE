package com.eyedia.eyedia.controller;

import com.eyedia.eyedia.domain.Painting;
import com.eyedia.eyedia.dto.MessageDTO;
import com.eyedia.eyedia.global.ApiResponse;
import com.eyedia.eyedia.global.error.exception.GeneralException;
import com.eyedia.eyedia.global.error.status.ErrorStatus;
import com.eyedia.eyedia.global.error.status.SuccessStatus;
import com.eyedia.eyedia.repository.ExhibitionRepository;
import com.eyedia.eyedia.repository.PaintingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor

public class DetectionEventController {
    private final SimpMessageSendingOperations messagingTemplate;
    private final PaintingRepository paintingRepository;
    private final ExhibitionRepository exhibitionRepository;

    @PostMapping("/detect")
    public ApiResponse<?> detect(@RequestBody Long artId) {

        MessageDTO.ChatImageResponseDTO message;

        var list = paintingRepository.findNullUserByArtId(artId);
        if (list.isEmpty()) {
            throw new GeneralException(ErrorStatus.PAINTING_NOT_FOUND);
        } else if (list.size() > 1) {
            throw new GeneralException(ErrorStatus.PAINTING_CONFLICT);
        } else {
            Painting painting = list.get(0);
            var exhibition = exhibitionRepository.findByPaintingsPaintingId(painting.getPaintingId())
                    .orElseThrow(() -> new GeneralException(ErrorStatus.EXHIBITION_NOT_FOUND));

            message = MessageDTO.ChatImageResponseDTO.builder()
                    .paintingId(painting.getPaintingId())
                    .imgUrl("https://s3-eyedia.s3.ap-northeast-2.amazonaws.com/"
                            + exhibition.getExhibitionsId() + "/" + artId + "/" + artId + ".jpg")
                    .title(painting.getTitle())
                    .artist(painting.getArtist())
                    .description(painting.getDescription())
                    .exhibition(exhibition.getTitle())
                    .artId(artId)
                    .build();

            messagingTemplate.convertAndSend("/queue/events", message);
        }
        return ApiResponse.of(SuccessStatus._OK, message);
    }
}