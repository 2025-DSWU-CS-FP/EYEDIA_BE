package com.eyedia.eyedia.dto;

import com.eyedia.eyedia.domain.enums.SenderType;
import lombok.*;

public class AiToBackendDTO {

    @Getter
    @AllArgsConstructor
    public static class PaintingDescriptionRequest {
        private Long paintingId;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ObjectDescriptionRequest {
        private Long paintingId;
        private Long objectId;
        private String description;
        private SenderType sendingType;
    }

    @Getter
    @AllArgsConstructor
    public static class RecaptureResponse {
        private boolean isSuccess;
    }
}