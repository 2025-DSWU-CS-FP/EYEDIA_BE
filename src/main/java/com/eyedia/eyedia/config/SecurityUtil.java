package com.eyedia.eyedia.config;


import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
@NoArgsConstructor
public class SecurityUtil {
    public static Long getCurrentUserId() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("Security Context에 인증 정보가 없습니다.");
        }

        try {
            return Long.parseLong(authentication.getName()); // String 형태의 UserId를 Long 타입으로
        } catch (NumberFormatException e) {
            throw new RuntimeException("User ID가 올바르지 않은 형식입니다.");
        }
    }


}
