package com.eyedia.eyedia.dto;

import lombok.*;

public class UserDTO {

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class VerifyPasswordDTO {
        String password;

    }

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class VerifyPasswordResponseDTO {

        boolean verified;
        UserInfoDTO userInfo;

    }
    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class UserInfoDTO {
        String username;
        Integer age;
        String gender;
        String id;

    }
}
