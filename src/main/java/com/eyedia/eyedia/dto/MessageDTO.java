package com.eyedia.eyedia.dto;

import com.eyedia.eyedia.domain.enums.ChatType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

public class MessageDTO {

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ChatMessageDTO {
        private String sender;
        private String content;
        private Long paintingId; // 특정 그림에 대한 채팅이라면 포함
        private ChatType chatType;
        private String timestamp;   // ISO 포맷 문자열

    }
    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @Data
    public static class ModelResponseDTO {

        @JsonProperty("full_image_id")
        private String fullImageId;

        @JsonProperty("object_description")
        private String objectDescription;
    }

}
