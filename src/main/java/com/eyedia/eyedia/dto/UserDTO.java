package com.eyedia.eyedia.dto;

import com.eyedia.eyedia.global.validation.annotation.CheckLoginId;
import com.eyedia.eyedia.global.validation.annotation.CheckNickName;
import com.eyedia.eyedia.global.validation.annotation.CheckPassWord;
import jakarta.validation.constraints.NotBlank;
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

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class MeBriefResponseDTO {
        private String loginId;  // User.id (로그인용)
        private String nickname; // User.name (닉네임)
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
    @CheckPassWord(password = "password", confirmPassword = "confirmPassword")
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class UpdatePassWordRequest {

        @NotBlank(message = "비밀번호는 필수입니다.")
        private String password;
        @NotBlank(message = "비밀번호 확인은 필수입니다.")
        private String confirmPassword;

    }
}
