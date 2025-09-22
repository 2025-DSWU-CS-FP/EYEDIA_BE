package com.eyedia.eyedia.controller;

import com.eyedia.eyedia.dto.UserDTO;
import com.eyedia.eyedia.global.ApiResponse;
import com.eyedia.eyedia.service.impl.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;

    // 비밀번호 인증
    @PostMapping("/verify-password")
    public ApiResponse<UserDTO.VerifyPasswordResponseDTO> verifyPassword(
            @Schema(hidden = true) @AuthenticationPrincipal String userId,
            @RequestBody UserDTO.VerifyPasswordDTO password

    ) {
        var uid = Long.parseLong(userId);
        var userInfo = authService.verifyPassword(uid, password.getPassword());
        return ApiResponse.onSuccess(userInfo);

    }

    // 유저 정보 변경

    // 아이디 변경
    @PatchMapping("/me/login-id")
    public ApiResponse<?> updateLoginId(
            @Schema(hidden = true) @AuthenticationPrincipal String userId,
            @RequestBody @Valid UserDTO.UpdateLoginIdRequest request

    ) {
        var uid = Long.parseLong(userId);
        authService.updateLoginId(uid, request.getLoginId());
        return ApiResponse.onSuccessWithoutResult();

    }

    // 닉네임 변경
    @PatchMapping("/me/nickname")
    public ApiResponse<?> updateNickName(
            @Schema(hidden = true) @AuthenticationPrincipal String userId,
            @RequestBody @Valid UserDTO.UpdateNickNamequest request

    ) {
        var uid = Long.parseLong(userId);
        authService.updateNickName(uid, request.getNickname());
        return ApiResponse.onSuccessWithoutResult();
    }

    // 비밀번호 변경
    @PatchMapping("/me/pw")
    public ApiResponse<?> updatePassword(
            @Schema(hidden = true) @AuthenticationPrincipal String userId,
            @RequestBody @Valid UserDTO.UpdatePassWordRequest request

    ) {
        var uid = Long.parseLong(userId);
        authService.updatePassWord(uid, request.getPassword());
        return ApiResponse.onSuccessWithoutResult();
    }

    // 닉네임, 아이디 조회
    @GetMapping("/me")
    @Operation(
            summary = "내 기본 정보(로그인ID/닉네임) 조회",
            description = "로그인한 사용자의 로그인 아이디와 닉네임을 반환합니다."
    )
    public ApiResponse<UserDTO.MeBriefResponseDTO> getMyBrief(
            @Schema(hidden = true) @AuthenticationPrincipal String userId
    ) {
        long uid = Long.parseLong(userId);
        var res = authService.getMyBrief(uid);
        return ApiResponse.onSuccess(res);
    }

}
