package com.eyedia.eyedia.config;

import com.eyedia.eyedia.config.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class AuthHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtProvider jwtProvider;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {

        if (request instanceof ServletServerHttpRequest servletRequest) {
            String auth = servletRequest.getServletRequest().getHeader("Authorization");
            if (auth == null) {
                auth = servletRequest.getServletRequest().getHeader("authorization"); // 소문자도 대응
            }
            if (auth != null && auth.startsWith("Bearer ")) {
                String token = auth.substring(7);
                if (jwtProvider.validateToken(token)) {
                    String userId = jwtProvider.getUserIdFromToken(token);
                    attributes.put("userId", userId);
                }
            }
        }
        // 개발 단계: 토큰 없어도 연결은 열어둠(실제 권한 검사는 CONNECT에서)
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
    }
}
