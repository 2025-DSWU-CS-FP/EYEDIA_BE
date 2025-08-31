package com.eyedia.eyedia.controller;

import com.eyedia.eyedia.dto.ScrapRequestDto;
import com.eyedia.eyedia.domain.Scrap;
import com.eyedia.eyedia.dto.ScrapResponseDto;
import com.eyedia.eyedia.service.ScrapService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/scraps")
@RequiredArgsConstructor
@Tag(name = "Scrap API", description = "스크랩 관련 API")
public class ScrapController {

    private final ScrapService scrapService;

    @PostMapping("/save")
    @Operation(summary = "스크랩 저장", description = "유저가 발췌한 정보를 저장합니다.")
    public ResponseEntity<?> saveScrap(
            @AuthenticationPrincipal String userId,
            @RequestBody ScrapRequestDto dto) {
        Long uid = Long.parseLong(userId);

        Scrap saved = scrapService.saveScrap(dto, uid);
        String message = "\"" + saved.getExcerpt() + "\"가 잘 저장되었습니다.";
        return ResponseEntity.ok().body(message);
    }

    @GetMapping("/list")
    @Operation(summary = "스크랩 목록 조회", description = "저장된 모든 스크랩 발췌 정보를 조회합니다.")
    public ResponseEntity<?> getScrapList() {
        List<ScrapResponseDto> list = scrapService.getScrapList();
        return ResponseEntity.ok().body(list);
    }

    @GetMapping("/list/{userId}")
    @Operation(summary = "유저 + 전시별 스크랩 조회", description = "특정 유저가 특정 전시에서 남긴 스크랩 목록을 조회합니다.")
    public ResponseEntity<?> getScrapListByUserAndLocation(
            Principal principal,
            @RequestParam(required = false) String location
    ) {
        Long userId = Long.parseLong(principal.getName());

        if (location != null) {
            List<ScrapResponseDto> filtered = scrapService.getScrapListByUserAndLocation(userId, location);
            return ResponseEntity.ok().body(filtered);
        } else {
            // 기존 유저 전체 목록
            List<ScrapResponseDto> all = scrapService.getScrapListByUserId(userId);
            return ResponseEntity.ok().body(all);
        }
    }


}