package com.eyedia.eyedia.config;

import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        // 메시지에서 STOMP 헤더 접근자 꺼내기 (권장 방식)
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) {
            return message; // STOMP 메시지가 아니면 패스
        }

        StompCommand cmd = accessor.getCommand();
        if (cmd == null) {
            return message;
        }

        if (StompCommand.CONNECT.equals(cmd)) {
            // 프런트에서 보낸 CONNECT 헤더 읽기
            String auth = firstNonNull(
                    accessor.getFirstNativeHeader("Authorization"),
                    accessor.getFirstNativeHeader("authorization"),
                    accessor.getFirstNativeHeader("access_token")
            );

            if (auth != null && auth.startsWith("Bearer ")) {
                auth = auth.substring(7);
            }

            if (auth == null || auth.isBlank()) {
                throw new org.springframework.security.access.AccessDeniedException("Missing token");
            }

            Long userId = JwtUtil.getUserIdFromToken(auth); // 검증 실패 시 예외
            if (userId == null) {
                throw new org.springframework.security.access.AccessDeniedException("Invalid token");
            }

            // 필요 시 Principal 심기
            var principal = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                    userId, null, java.util.List.of()
            );
            accessor.setUser(principal);
        }

        return message;
    }

    private static String firstNonNull(String... vals) {
        for (String v : vals) if (v != null) return v;
        return null;
    }
}
