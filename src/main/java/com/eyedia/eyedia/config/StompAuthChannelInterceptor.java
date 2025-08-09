package com.eyedia.eyedia.config;

import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null || accessor.getCommand() == null) return message;

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            // 1) CONNECT 네이티브 헤더에서 토큰 꺼내기(여러 키 대응)
            String auth = firstNonNull(
                    accessor.getFirstNativeHeader("Authorization"),
                    accessor.getFirstNativeHeader("authorization"),
                    accessor.getFirstNativeHeader("access_token")
            );

            // 2) 공백/따옴표 제거
            if (auth != null) auth = auth.trim().replaceAll("^\"|\"$", "");

            // 3) Bearer 접두어(대소문자 무시) 제거
            if (auth != null && auth.toLowerCase().startsWith("bearer ")) {
                auth = auth.substring(7).trim();
            }

            // 4) 토큰 누락 처리
            if (auth == null || auth.isBlank()) {
                throw new AccessDeniedException("Missing Authorization Bearer token in CONNECT");
            }

            // 5) 검증 및 Principal 세팅
            try {
                Long userId = JwtUtil.getUserIdFromToken(auth); // 프로젝트의 구현체 사용
                if (userId == null) throw new AccessDeniedException("Invalid token (null userId)");

                // principal.getName() 사용을 위해 문자열로 저장
                var principal = new UsernamePasswordAuthenticationToken(String.valueOf(userId), null, List.of());
                accessor.setUser(principal);
            } catch (Exception e) {
                throw new AccessDeniedException("JWT validation failed: " + e.getMessage());
            }
        }

        return message;
    }

    private static String firstNonNull(String... vals) {
        for (String v : vals) if (v != null) return v;
        return null;
    }
}
