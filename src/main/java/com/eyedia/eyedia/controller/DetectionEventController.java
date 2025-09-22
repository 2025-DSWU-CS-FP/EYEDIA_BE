package com.eyedia.eyedia.controller;

import com.eyedia.eyedia.dto.MessageDTO;
import com.eyedia.eyedia.repository.ExhibitionRepository;
import com.eyedia.eyedia.repository.PaintingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor

public class DetectionEventController {
    private final SimpMessageSendingOperations messagingTemplate;
    private final PaintingRepository paintingRepository;
    private final ExhibitionRepository exhibitionRepository;

    @PostMapping("/detect")
    public ResponseEntity<Void> detect(@RequestBody Long paintingId) {

        var painting = paintingRepository.findByObjectId(String.valueOf(paintingId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "painting not found"));
        var exhibition = exhibitionRepository.findByPaintingsPaintingId(painting.getPaintingId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "exhibition not found"));

        messagingTemplate.convertAndSend(
                "/queue/events",
                MessageDTO.ChatImageResponseDTO.builder()
                        .imgUrl("https://s3-eyedia.s3.ap-northeast-2.amazonaws.com/1/" + paintingId + "/" + paintingId)
                        .title(painting.getTitle())
                        .artist(painting.getArtist())
                        .description(painting.getDescription())
                        .exhibition(exhibition.getTitle())
                        .artId(paintingId)
                        .build()
        );
        return ResponseEntity.ok().build();
    }

    @PostMapping("add-user")
    public ResponseEntity<Void> addUser(Principal principal, Long paintingId) {
        String userKey = principal.getName();
        detect(paintingId);
        return ResponseEntity.ok().build();
    }
}