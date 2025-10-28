package com.eyedia.eyedia.controller;

import com.eyedia.eyedia.dto.UserLoginDTO;
import com.eyedia.eyedia.dto.UserSignupDTO;
import com.eyedia.eyedia.service.impl.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
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
    public ResponseEntity<?> login(@RequestBody UserLoginDTO loginDTO) {
        try {
            var responseDTO = authService.login(loginDTO);
            return ResponseEntity.ok(responseDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body("로그인 실패: " + e.getMessage());
        }
    }

    // Naver 소셜로그인 인가코드 받아오기
    @GetMapping("/login/oauth2/code/naver")
    public String redirectNaverLogin() {
        // 이 URL은 security.oauth2.client.registration.naver.redirect-uri 로 등록되어야 함
        return "redirect:/";
    }

    // 로그인 성공 후 사용자 정보 확인용
    @GetMapping("/social/naver/success")
    @ResponseBody
    public Object loginSuccess(@AuthenticationPrincipal OAuth2User oAuth2User) {
        log.info("[OAuth] 로그인 성공: {}", oAuth2User.getAttributes());
        return oAuth2User.getAttributes(); // 테스트용
    }

    // 로그인 실패 시
    @GetMapping("/auth/fail")
    @ResponseBody
    public String loginFail() {
        return "네이버 로그인 실패";
    }
}
