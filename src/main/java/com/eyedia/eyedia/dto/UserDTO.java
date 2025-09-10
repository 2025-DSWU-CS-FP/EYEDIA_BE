package com.eyedia.eyedia.dto;

import com.eyedia.eyedia.global.validation.annotation.CheckLoginId;
import com.eyedia.eyedia.global.validation.annotation.CheckNickName;
import com.eyedia.eyedia.global.validation.annotation.CheckPassWord;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

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
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class UpdateNickNamequest {

        @CheckNickName
        private String nickname;
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class UpdateLoginIdRequest {

        @CheckLoginId
        private String loginId;
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class UpdatePassWorddRequest {

        @CheckPassWord
        private String password;
        private String verify_password;

    }
}
