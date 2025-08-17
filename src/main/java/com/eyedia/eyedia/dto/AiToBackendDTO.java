package com.eyedia.eyedia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

public class AiToBackendDTO {

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
