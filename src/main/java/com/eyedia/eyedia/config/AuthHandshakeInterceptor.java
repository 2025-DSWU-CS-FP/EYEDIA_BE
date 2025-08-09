package com.eyedia.eyedia.config;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
public class AuthHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {

        // 1) SockJS 보조 경로(/ws-stomp/**)와 프리플라이트는 무조건 통과
        String path = request.getURI().getPath(); // e.g. /ws-stomp, /ws-stomp/info, /ws-stomp/xxxx
        if (path.startsWith("/ws-stomp/")) return true;  // /info, /xhr_streaming, /xhr_send 등
        if ("OPTIONS".equalsIgnoreCase(request.getMethod().name())) return true;

        // 2) 네이티브 WS 핸드셰이크(/ws-stomp)에선 토큰을 읽어서 '세션 속성'에만 저장 (차단은 X)
        if (request instanceof ServletServerHttpRequest servletRequest) {
            var req = servletRequest.getServletRequest();

            // 헤더 또는 쿼리 파라미터(access_token)로 받기 (SockJS 일부 클라이언트는 헤더 제약)
            String token = req.getHeader("Authorization");
            if (token == null) token = req.getParameter("Authorization");
            if (token == null) token = req.getParameter("access_token");

            if (token != null && token.startsWith("Bearer ")) token = token.substring(7);

            if (token != null && !token.isBlank()) {
                try {
                    Long userId = JwtUtil.getUserIdFromToken(token); // 구현체 사용
                    if (userId != null) attributes.put("userId", userId);
                } catch (Exception ignore) {
                    // 여기서 막지 않음. 실제 차단은 CONNECT 프레임에서 수행.
                }
            }
        }
        return true; // ★ 핸드셰이크에서 차단하지 말 것 (SockJS 호환)
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) { }
}

