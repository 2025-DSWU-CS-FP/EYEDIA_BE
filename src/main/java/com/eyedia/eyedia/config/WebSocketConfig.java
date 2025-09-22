package com.eyedia.eyedia.config;

import com.eyedia.eyedia.config.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final AuthHandshakeInterceptor authHandshakeInterceptor;
    private final JwtProvider jwtProvider;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        var ts = new org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler();
        ts.setPoolSize(1);
        ts.setThreadNamePrefix("ws-heartbeat-");
        ts.initialize();

        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/topic", "/queue", "/room")
                // topic이 그림을 받기 위해서 기본으로 연결되는 것
                // room은 채팅방
                .setTaskScheduler(ts)
                .setHeartbeatValue(new long[]{10000, 10000});
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-stomp")
                // 필요 Origin만 명시 (Vite 기본 5173 쓴다면 그것도 추가)
                .setAllowedOriginPatterns(
                        "http://localhost:3000",
                        "http://localhost:5173",
                        "https://eyedia.site",
                        "https://eyedia.netlify.app",
                        "http://3.34.240.201:8000"
                )
                .addInterceptors(authHandshakeInterceptor);
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
                    // 헤더 대소문자 모두 대응
                    String auth = accessor.getFirstNativeHeader("Authorization");
                    if (auth == null) auth = accessor.getFirstNativeHeader("authorization");

                    if (auth == null || !auth.startsWith("Bearer ")) {
                        throw new IllegalArgumentException("Missing Authorization header");
                    }

                    String token = auth.substring(7);
                    if (!jwtProvider.validateToken(token)) {
                        throw new IllegalArgumentException("Invalid or expired token");
                    }

                    String userId = jwtProvider.getUserIdFromToken(token); // String으로 반환
                    accessor.setUser(new UsernamePasswordAuthenticationToken(userId, null, List.of()));
                }
                return message; // 절대 null 반환하지 말 것
            }
        });
    }
}
