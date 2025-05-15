package com.eyedia.eyedia.dto;

import lombok.*;

public class UserDTO {

    @Getter
    public static class UserRequestDTO {

    }

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class UserResponseDTO {

    }
}
