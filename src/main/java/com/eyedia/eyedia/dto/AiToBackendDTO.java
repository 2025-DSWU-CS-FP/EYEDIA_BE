package com.eyedia.eyedia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

public class AiToBackendDTO {

    @Getter
    @Builder
    @AllArgsConstructor
    @Schema(description = "AI가 백엔드에 전달하는 그림 후보 요청 DTO")
    public static class MatchingCandidateRequest {
        @Schema(description = "그림 후보 ID", example = "12")
        private Long candidateId;

        public Long getPaintingId() {
            return candidateId;
        }
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
    public static class RecaptureResponse {
        @Schema(description = "재촬영 요청 여부", example = "true")
        private boolean isSuccess;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Jacksonized
    @ToString
    @Schema(description = "AI가 객체 설명을 전달할 때 사용하는 요청 DTO")
    public static class ObjectDescriptionRequest {

        @JsonProperty("objectId")
        private String objectId;

        @JsonProperty("description")
        private String description;

        @JsonProperty("imageurl")
        private String imageurl;

        @JsonProperty("title")
        private String title;

        @JsonProperty("artist")
        private String artist;

        @JsonProperty("paintingId")
        private Long paintingId;
    }
}
