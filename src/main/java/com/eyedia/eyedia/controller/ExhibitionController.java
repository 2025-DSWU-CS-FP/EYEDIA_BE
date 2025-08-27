package com.eyedia.eyedia.controller;

import com.eyedia.eyedia.dto.ExhibitionDTO;
import com.eyedia.eyedia.dto.PageResponse;
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
    public List<ExhibitionDTO.ExhibitionSimpleResponseDTO> popularTopList(
            @RequestParam(defaultValue = "12") int size
    ) {
        return exhibitionService.getPopularTopN(size);
    }

    // 페이징 버전
    @GetMapping("/popular/paging")
    public PageResponse<ExhibitionDTO.ExhibitionSimpleResponseDTO> popularList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int limit
    ) {
        var result = exhibitionService.getPopularPaged(page, limit); // Page<DTO>
        return PageResponse.from(result);
    }

    // 상세 조회
    @GetMapping("/popular/{exhibitionId}")
    public ExhibitionDTO.ExhibitionDetailResponseDTO popularDetail(
            @PathVariable Long exhibitionId
    ) {
        return exhibitionService.getPopularDetailPage(exhibitionId);
    }


}
