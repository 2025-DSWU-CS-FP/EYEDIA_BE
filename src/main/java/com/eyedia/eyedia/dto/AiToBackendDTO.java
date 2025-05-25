package com.eyedia.eyedia.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

public class AiToBackendDTO {

    @Getter
    @Builder
    @AllArgsConstructor
    @Schema(description = "AI가 백엔드에 전달하는 그림 후보 요청 DTO")
    public static class MatchingCandidateRequest {
        @Schema(description = "그림 후보 ID", example = "12")
        private Long candidateId;
    }


    @Getter
    @Builder
    @AllArgsConstructor
    public static class MatchingCandidateResponse {
        private Long id;
        private String title;
        private String artist;
        private String imageUrl;
    }


    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PaintingDescriptionRequest {
        @Schema(description = "그림 ID", example = "1")
        private Long paintingId;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ObjectDescriptionRequest {
        @Schema(description = "그림 ID", example = "1")
        private Long paintingId;

        @Schema(description = "객체 ID", example = "10")
        private Long objectId;

        @Schema(description = "AI가 생성한 설명", example = "오른쪽 아래에 위치한 곡선형 주전자입니다.")
        private String description;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RecaptureResponse {
        @Schema(description = "재촬영 요청 여부", example = "true")
        private boolean isSuccess;
    }
}
