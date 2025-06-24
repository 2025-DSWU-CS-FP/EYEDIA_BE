package com.eyedia.eyedia.dto;

import com.eyedia.eyedia.domain.enums.SenderType;
import lombok.*;

public class ArtObjectDTO {

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
        private SenderType sendingType;

    }
}
