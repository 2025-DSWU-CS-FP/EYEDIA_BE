package com.eyedia.eyedia.controller;

import com.eyedia.eyedia.dto.ExhibitionDTO;
import com.eyedia.eyedia.dto.PageResponse;
import com.eyedia.eyedia.global.ApiResponse;
import com.eyedia.eyedia.service.ExhibitionQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/exhibitions")
public class ExhibitionController {
    private final ExhibitionQueryService exhibitionService;

    // 메인 섹션용 Top N
    @GetMapping("/popular")
    public ApiResponse<List<ExhibitionDTO.ExhibitionSimpleResponseDTO>> popularTopList(
            @RequestParam(defaultValue = "12") int size
    ) {
        return ApiResponse.onSuccess(exhibitionService.getPopularTopN(size));
    }

    // 페이징 버전
    @GetMapping("/popular/paging")
    public ApiResponse<PageResponse<ExhibitionDTO.ExhibitionSimpleResponseDTO>> popularList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int limit
    ) {
        var result = exhibitionService.getPopularPaged(page, limit);
        return ApiResponse.onSuccess(PageResponse.from(result));
    }

    // 상세 조회
    @GetMapping("/popular/{exhibitionId}")
    public ApiResponse<ExhibitionDTO.ExhibitionDetailResponseDTO> popularDetail(
            @PathVariable Long exhibitionId
    ) {
        return ApiResponse.onSuccess(exhibitionService.getPopularDetailPage(exhibitionId));
    }
    // 글자 단위 검색
    @GetMapping("/exhibitions/suggest")
    public ApiResponse<List<ExhibitionDTO.ExhibitionSimpleResponseDTO>> suggest(
            @RequestParam String q,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ApiResponse.onSuccess(exhibitionService.suggest(q, limit));
    }

}
