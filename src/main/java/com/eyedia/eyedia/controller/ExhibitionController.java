package com.eyedia.eyedia.controller;

import com.eyedia.eyedia.dto.ExhibitionDTO;
import com.eyedia.eyedia.dto.PageResponse;
import com.eyedia.eyedia.global.ApiResponse;
import com.eyedia.eyedia.service.ExhibitionQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/exhibitions")
public class ExhibitionController {
    private final ExhibitionQueryService exhibitionService;

    // 메인 섹션용 Top N
    @Operation(summary = "Top N 개의 인기 전시 조회 API", description = "Top N 개의 인기 전시 조회")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "EXHIBITION400", description = "유효하지 않은 id값입니다.")

    })
    @GetMapping("/popular/top")
    public ApiResponse<List<ExhibitionDTO.ExhibitionSimpleResponseDTO>> popularTopList(
            @RequestParam(defaultValue = "12") int size
    ) {
        return ApiResponse.onSuccess(exhibitionService.getPopularTopN(size));
    }

    // 페이징 버전
    @Operation(summary = "인기 전시 조회 API", description = "인기 전시 조회(페이징)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "EXHIBITION400", description = "유효하지 않은 id값입니다.")

    })
    @GetMapping("/popular")
    public ApiResponse<PageResponse<ExhibitionDTO.ExhibitionSimpleResponseDTO>> popularList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int limit
    ) {
        var result = exhibitionService.getPopularPaged(page, limit);
        return ApiResponse.onSuccess(PageResponse.from(result));
    }

    // 상세 조회
    @Operation(summary = "인기 전시 상세 페이지 조회 API", description = "인기 전시의 상세 페이지 정보를 반환")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "EXHIBITION400", description = "유효하지 않은 id값입니다.")

    })
    @GetMapping("/popular/{exhibitionId}")
    public ApiResponse<ExhibitionDTO.ExhibitionDetailResponseDTO> popularDetail(
            @PathVariable Long exhibitionId
    ) {
        return ApiResponse.onSuccess(exhibitionService.getPopularDetailPage(exhibitionId));
    }
    // 글자 단위 검색
    @Operation(summary = "전시 검색 API", description = "갤러리, 전시명을 기반으로 글자 기준으로 전시회 조회")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "EXHIBITION400", description = "유효하지 않은 id값입니다.")

    })
    @GetMapping("/suggest")
    public ApiResponse<List<ExhibitionDTO.ExhibitionSimpleResponseDTO>> suggest(
            @RequestParam String q,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ApiResponse.onSuccess(exhibitionService.suggest(q, limit));
    }
    // 나의 전시 - 내가 관람한 전시 조회_최신순
    @Operation(summary = "방문한 전시 중 최신순 정렬 API", description = "사용자가 방문한 전시 리스트 중에서 최신순으로 정렬한 리스트 조회")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "EXHIBITION400", description = "유효하지 않은 id값입니다.")

    })
    @GetMapping("/visit/filter-recent")
    public ApiResponse<PageResponse<ExhibitionDTO.ExhibitionSimpleResponseDTO>> visit(
            @AuthenticationPrincipal String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int limit
    ) {
        // user id
        Long uid = Long.valueOf(userId);
        var result = exhibitionService.getMyVisitedExhibitionsLatest(uid, page, limit);
        return ApiResponse.onSuccess(PageResponse.from(result));

    }
    // 나의 전시 - 전시 상세 페이지 조회
    @Operation(summary = "사용자가 방문한 전시의 상세페이지 조회 API", description = "사용자가 방문한 전시의 상세페이지와 발췌 카드 리스트 조회")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "EXHIBITION400", description = "유효하지 않은 id값입니다.")

    })
    @GetMapping("/visit/{exhibitionId}")
    public ApiResponse<ExhibitionDTO.MyExhibitionDetailResponseDTO> visitDetail(
            @AuthenticationPrincipal String userId,
            @PathVariable Long exhibitionId
    ) {
        // user id
        Long uid = Long.valueOf(userId);
        return ApiResponse.onSuccess(exhibitionService.getMyVisitedExhibitionDetail(uid, exhibitionId));
    }

}
