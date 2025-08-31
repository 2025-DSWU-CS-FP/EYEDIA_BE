package com.eyedia.eyedia.controller;

import com.eyedia.eyedia.dto.UserDTO;
import com.eyedia.eyedia.global.ApiResponse;
import com.eyedia.eyedia.service.impl.AuthService;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
