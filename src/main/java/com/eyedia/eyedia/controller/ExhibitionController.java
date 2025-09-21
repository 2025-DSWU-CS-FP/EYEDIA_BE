package com.eyedia.eyedia.controller;

import com.eyedia.eyedia.domain.enums.ViewedSort;
import com.eyedia.eyedia.dto.ExhibitionDTO;
import com.eyedia.eyedia.dto.PageResponse;
import com.eyedia.eyedia.global.ApiResponse;
import com.eyedia.eyedia.service.ExhibitionCommandService;
import com.eyedia.eyedia.service.ExhibitionQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/exhibitions")
public class ExhibitionController {
    private final ExhibitionQueryService exhibitionService;
    private final ExhibitionCommandService exhibitionCommandService;

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
    // 인기 전시 - 글자 단위 검색
    @Operation(summary = "전시 검색 API", description = "갤러리, 전시명을 기반으로 글자 기준으로 전시회 조회")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    @GetMapping("/popular/suggest")
    public ApiResponse<List<ExhibitionDTO.ExhibitionSimpleResponseDTO>> suggestPopularList(
            @RequestParam String q,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ApiResponse.onSuccess(exhibitionService.suggest(q, limit));
    }
    // ------- 나의 전시 ------------

    /** 정렬 기준
     * 나의 전시 - 각 조건별 필터링
     *  - RECENT: 내가 방문한 시각(visitedAt)의 최대값 기준 내림차순
     *  - DATE:   내가 방문한 일자의 오름차순
     */
    @GetMapping("/viewed")
    @Operation(summary = "내가 관람한 전시 목록 조회 및 검색",
            description = "검색어/즐겨찾기/정렬(최신순·날짜순) 조건으로 페이징 조회")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "EXHIBITION400", description = "유효하지 않은 id값입니다.")
    })
    public ApiResponse<PageResponse<ExhibitionDTO.ExhibitionSimpleResponseDTO>> getUserVisitedViewed(
            @Schema(hidden = true) @AuthenticationPrincipal String userId,
            @RequestParam(required = false, name = "keyword")
            @Schema(description = "부분 일치 검색(전시명/갤러리명)", example = "미술관") String keyword,
            @RequestParam(defaultValue = "false", name = "isBookmarked")
            @Schema(description = "즐겨찾기만 보기", example = "true") boolean isBookmarked,
            @RequestParam(defaultValue = "RECENT", name = "sort")
            @Schema(implementation = ViewedSort.class,
                    description = "정렬기준: RECENT=방문 최신순, DATE=방문 오래된순", defaultValue = "RECENT",
                    example = "RECENT") ViewedSort sort,
            @RequestParam(defaultValue = "0") @Schema(example = "0") int page,
            @RequestParam(defaultValue = "12") @Schema(example = "12") int limit
    ) {
        Long uid = Long.valueOf(userId);

        Page<ExhibitionDTO.ExhibitionSimpleResponseDTO> result =
                exhibitionService.getMyViewed(
                        uid,
                        keyword,          // 검색어 (nullable)
                        isBookmarked,     // 즐겨찾기만 여부
                        sort,             // recent | date
                        PageRequest.of(page, limit)
                );

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

    // ------- 전시 북마크 ------------
    @Operation(summary = "북마크 등록 API", description = "사용자가 방문한 전시에서 북마크 등록")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "EXHIBITION400", description = "유효하지 않은 id값입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "BOOKMARK405", description = "이미 즐겨찾기 등록되었습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER404", description = "해당하는 유저 정보가 없습니다.")
    })
    @PostMapping("/{exhibitionId}/bookmark")
    public ApiResponse<?> addBookmark(
            @Schema(hidden = true) @AuthenticationPrincipal String userId,
            @PathVariable Long exhibitionId
    ) {
        Long uid = Long.valueOf(userId);
        exhibitionCommandService.bookmarkVisitedExhibitionByUser(uid, exhibitionId);
        return ApiResponse.onSuccessWithoutResult();

    }
    // 북마크 해제
    @Operation(summary = "북마크 해제 API", description = "사용자가 방문한 전시에서 북마크 해제")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "EXHIBITION400", description = "유효하지 않은 id값입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "BOOKMARK405", description = "이미 즐겨찾기 등록되었습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "BOOKMARK404", description = "즐겨찾기가 되어 있지 않습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER404", description = "해당하는 유저 정보가 없습니다.")
    })
    @DeleteMapping("/{exhibitionId}/bookmark")
    public ApiResponse<?> deleteBookmark(
            @Schema(hidden = true) @AuthenticationPrincipal String userId,
            @PathVariable Long exhibitionId
    ) {
        Long uid = Long.valueOf(userId);
        exhibitionCommandService.deleteBookmarkVisitedExhibitionByUser(uid, exhibitionId);
        return ApiResponse.onSuccessWithoutResult();

    }

    @Operation(
            summary = "내가 방문한 전시 개수 조회",
            description = "JWT 토큰을 통해 로그인한 사용자가 지금까지 방문한 전시의 개수(중복 제외)를 반환합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 (userId 누락 등)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @GetMapping("/my/visited/count")
    public ResponseEntity<CountResponse> getMyVisitedExhibitionCount(
            @Schema(hidden = true) @AuthenticationPrincipal String userId
    ) {

        Long uid = Long.valueOf(userId);

        long count = exhibitionService.getMyVisitedExhibitionCount(uid);
        return ResponseEntity.ok(new CountResponse(count));
    }

    public static class CountResponse {
        private long count;

        public CountResponse(long count) {
            this.count = count;
        }

        public long getCount() {
            return count;
        }
    }

}
