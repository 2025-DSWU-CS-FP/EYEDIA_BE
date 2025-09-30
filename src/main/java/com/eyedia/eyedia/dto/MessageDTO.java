package com.eyedia.eyedia.dto;

import com.eyedia.eyedia.domain.enums.ChatType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

public class MessageDTO {
    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ChatImageResponseDTO {
        private Long paintingId;
        private String imgUrl;
        private String title;
        private String artist;
        private String description;
        private String exhibition;
        private Long artId;
    }

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

    @Getter @Setter @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AskRequest {
        private Long paintingId;   // 채팅 방 자동 분리에 사용할 키
        private String text;  // 사용자가 보낸 메시지
        private String deviceId; // 젯슨 식별자 (프론트가 같이 보냄)
    }

    @Getter @Setter @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatAnswerDTO {
        private Long paintingId;
        private String answer; // LLM 도슨트 톤 답변
        private String model;  // (옵션) 모델명
        private String imgUrl;
        private String audioUrl; // 선택: 프론트도 재생 가능
    }
}
