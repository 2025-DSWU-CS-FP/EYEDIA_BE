package com.eyedia.eyedia.dto;

import lombok.*;

public class MessageDTO {

    @Getter
    public static class MessageRequestDTO {

    }

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class MessageResponseDTO {

    }
}
