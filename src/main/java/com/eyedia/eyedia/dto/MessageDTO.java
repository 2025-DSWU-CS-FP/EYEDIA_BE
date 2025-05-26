package com.eyedia.eyedia.dto;

import com.eyedia.eyedia.domain.enums.ChatType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

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

}
