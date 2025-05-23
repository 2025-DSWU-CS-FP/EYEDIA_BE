package com.eyedia.eyedia.controller;

import com.eyedia.eyedia.dto.UserLoginDTO;
import com.eyedia.eyedia.dto.UserSignupDTO;
import com.eyedia.eyedia.service.impl.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 회원가입 API
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody UserSignupDTO signupDTO) {
        authService.signup(signupDTO);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    // 로그인 API
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLoginDTO loginDTO) {
        boolean success = authService.login(loginDTO);
        if (success) {
            return ResponseEntity.ok("로그인 성공");
        } else {
            return ResponseEntity.status(401).body("로그인 실패: 아이디 또는 비밀번호가 올바르지 않습니다.");
        }
    }
}
