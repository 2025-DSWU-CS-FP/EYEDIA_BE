package com.eyedia.eyedia.dto;

import lombok.*;

public class AiToBackendDTO {

    @Getter
    @AllArgsConstructor
    public static class PaintingDescriptionRequest {
        private Long paintingId;
    }

    @Getter
    @AllArgsConstructor
    public static class ObjectDescriptionRequest {
        private Long paintingId;
        private Long objectId;
        private String description;
    }

    @Getter
    @AllArgsConstructor
    public static class RecaptureResponse {
        private boolean isSuccess;
    }
}