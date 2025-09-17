package com.eyedia.eyedia.controller;

import com.eyedia.eyedia.domain.enums.ExhibitionCategory;
import com.eyedia.eyedia.dto.RecommendationDTO;
import com.eyedia.eyedia.dto.RecommendedExhibitionsResponse;
import com.eyedia.eyedia.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Recommendations")
@RestController
@RequestMapping("/api/v1/recommendations")
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    @Operation(
            summary = "내 키워드 조회",
            description = "회원가입 시 사용자가 선택한 ExhibitionCategory 키워드 배열을 반환합니다."
    )
    @ApiResponse(responseCode = "200", description = "OK")
    @GetMapping("/keywords")
    public ResponseEntity<RecommendationDTO> getMyKeywords(
            @Parameter(hidden = true) @AuthenticationPrincipal String usersId
    ) {
        return ResponseEntity.ok(recommendationService.getMyKeywords(Long.valueOf(usersId)));
    }

    @Operation(
            summary = "사용자 맞춤 전시회 추천",
            description = "회원가입 시 사용자가 선택한 ExhibitionCategory 키워드에 맞는 전시 리스트를 반환합니다."
    )
    @ApiResponse(responseCode = "200", description = "OK")
    @GetMapping("/exhibitions")
    public ResponseEntity<List<RecommendedExhibitionsResponse>> getExhibitions(
            @Parameter(hidden = true) @AuthenticationPrincipal String usersId,
            @RequestParam(defaultValue = "10") int size
    ){
        return ResponseEntity.ok(
                recommendationService.getMyRecommendedExhibitions(Long.valueOf(usersId), size)
        );
    }
}
