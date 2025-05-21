package com.eyedia.eyedia.dto;

import lombok.*;

public class UserFacingDTO {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class PaintingConfirmResponse {
        private Long paintingId;
        private boolean confirmed;
        private String message;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class PaintingDescriptionResponse {
        private Long paintingId;
        private String description;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class PaintingArtistResponse {
        private Long paintingId;
        private String artist;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class PaintingBackgroundResponse {
        private Long paintingId;
        private String background;
    }
}