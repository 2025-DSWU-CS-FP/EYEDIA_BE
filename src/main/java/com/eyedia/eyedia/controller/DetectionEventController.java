package com.eyedia.eyedia.controller;

import com.eyedia.eyedia.domain.Message;
import com.eyedia.eyedia.domain.Painting;
import com.eyedia.eyedia.domain.enums.SenderType;
import com.eyedia.eyedia.dto.DetectAreaRequestDTO;
import com.eyedia.eyedia.dto.MessageDTO;
import com.eyedia.eyedia.global.ApiResponse;
import com.eyedia.eyedia.global.error.exception.GeneralException;
import com.eyedia.eyedia.global.error.status.ErrorStatus;
import com.eyedia.eyedia.global.error.status.SuccessStatus;
import com.eyedia.eyedia.repository.ExhibitionRepository;
import com.eyedia.eyedia.repository.MessageRepository;
import com.eyedia.eyedia.repository.PaintingRepository;
import com.eyedia.eyedia.service.DocentChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor

public class DetectionEventController {
    private final SimpMessageSendingOperations messagingTemplate;
    private final PaintingRepository paintingRepository;
    private final ExhibitionRepository exhibitionRepository;
    private final DocentChatService docentChatService;
    private final MessageRepository messageRepository;

    @PostMapping("/detect")
    public ApiResponse<?> detect(@RequestBody Long artId) {

        MessageDTO.ChatImageResponseDTO message;

        var list = paintingRepository.findNullUserByArtId(artId);
        if (list.isEmpty()) {
            throw new GeneralException(ErrorStatus.PAINTING_NOT_FOUND);
        } else if (list.size() > 1) {
            List<Long> ids = list.stream().map(Painting::getPaintingId).toList();
            throw new GeneralException(ErrorStatus.PAINTING_CONFLICT, Map.of("duplicatedPaintingIds", ids));
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

    @PostMapping("/detect-area")
    public ApiResponse<?> detectArea(@RequestBody DetectAreaRequestDTO request) {

        List<String> descriptions = request.getList().stream()
                .map(DetectAreaRequestDTO.CropItemDTO::getCropDescription)
                .toList();

        String combined = descriptions.stream()
                .map(desc -> "- " + desc)
                .collect(Collectors.joining("\n"));

        List<Painting> paintings = paintingRepository.findByArtId(request.getArtId());

        if (paintings == null || paintings.isEmpty()) {
            throw new GeneralException(ErrorStatus.PAINTING_NOT_FOUND);
        }

        var answer = docentChatService.answer(
                docentChatService.gazeAreaPrompt(
                        paintings.get(0).getTitle(),
                        request.getQ().get(0),
                        combined));

        MessageDTO.ChatAnswerDTO dto = null;
        String imageUrl = "https://s3-eyedia.s3.ap-northeast-2.amazonaws.com/"
                          + paintings.get(0).getExhibition().getExhibitionsId() + "/"
                          + paintings.get(0).getArtId() + "/"
                          + request.getQ().get(0) + ".jpg";

        for (Painting painting : paintings) {
            Message q = Message.builder()
                    .sender(SenderType.USER)
                    .painting(painting)
                    .content(imageUrl)
                    .build();
            messageRepository.save(q);

            Message a = Message.builder()
                    .sender(SenderType.ASSISTANT)
                    .painting(painting)
                    .content(answer.text())
                    .build();
            messageRepository.save(a);

             dto = MessageDTO.ChatAnswerDTO.builder()
                    .paintingId(painting.getPaintingId())
                    .answer(answer.text())
                    .model(answer.model())
                     .imgUrl(imageUrl)
                    .build();

            messagingTemplate.convertAndSend("/room/" + painting.getPaintingId(), dto);
        }
        return ApiResponse.of(SuccessStatus._OK, dto);
    }
}