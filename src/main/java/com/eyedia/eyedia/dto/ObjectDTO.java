package com.eyedia.eyedia.dto;

import lombok.*;

public class ObjectDTO {

    @Getter
    public static class ObjectRequestDTO {

    }

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ObjectResponseDTO {

    }
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaintingDescriptionRequest {
        private Long paintingId;
        private String description;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ObjectDescriptionRequest {
        private Long paintingId;
        private Long objectId;
        private String description;
    }
}
