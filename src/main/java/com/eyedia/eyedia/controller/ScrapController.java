package com.eyedia.eyedia.controller;

import com.eyedia.eyedia.dto.ScrapRequestDto;
import com.eyedia.eyedia.domain.Scrap;
import com.eyedia.eyedia.dto.ScrapResponseDto;
import com.eyedia.eyedia.service.ScrapService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Scrap API", description = "스크랩 관련 API")
public class ScrapController {

    private final ScrapService scrapService;

    @PostMapping("/scraps/save")
    @Operation(summary = "스크랩 저장", description = "유저가 발췌한 정보를 저장합니다.")
    public ResponseEntity<?> saveScrap(
            @AuthenticationPrincipal String userId,
            @RequestBody ScrapRequestDto dto) {
        Long uid = Long.parseLong(userId);

        Scrap saved = scrapService.saveScrap(dto, uid);
        String message = "\"" + saved.getExcerpt() + "\"가 잘 저장되었습니다.";
        return ResponseEntity.ok().body(message);
    }

//    @GetMapping("/scraps/list")
//    @Operation(summary = "스크랩 목록 조회", description = "저장된 모든 스크랩 발췌 정보를 조회합니다.")
//    public ResponseEntity<?> getScrapList() {
//        List<ScrapResponseDto> list = scrapService.getScrapList();
//        return ResponseEntity.ok().body(list);
//    }

    @GetMapping("/scraps/list")
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

    @GetMapping("/artworks/viewed")
    @Operation(
            summary = "마이페이지 스크랩 목록 조회",
            description = """
                로그인 사용자의 스크랩 목록을 페이징하여 반환합니다.
                - page: 페이지 번호 (0부터 시작)
                - limit: 페이지당 개수
                - sort: 정렬 기준 (recent = 최신순, old = 오래된 순)
                """
    )
    public ResponseEntity<Page<ScrapResponseDto>> getMyScrapList(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(defaultValue = "recent") String sort
    ) {
        Long uid = Long.parseLong(principal.getName());

        Sort.Direction direction;
        String sortProperty = "date"; // 기본 정렬 컬럼

        switch (sort.toLowerCase()) {
            case "old":
                direction = Sort.Direction.ASC;
                break;
            case "recent":
            default:
                direction = Sort.Direction.DESC;
                break;
        }

        Pageable pageable = (Pageable) PageRequest.of(page, limit, Sort.by(direction, sortProperty).and(Sort.by(direction, "id")));

        Page<ScrapResponseDto> result = scrapService.getScrapsByUser(uid, pageable);

        return ResponseEntity.ok(result);
    }

}