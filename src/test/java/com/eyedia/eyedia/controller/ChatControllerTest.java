package com.eyedia.eyedia.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class EchoStompIntegrationTest {

    @LocalServerPort
    int port;

    WebSocketStompClient stompClient;
    StompSession session;

    @AfterEach
    void tearDown() {
        if (session != null && session.isConnected()) session.disconnect();
        if (stompClient != null) stompClient.stop();
    }

    @Test
    void echo_should_broadcast_to_room() throws Exception {
        // 1) STOMP 클라이언트 생성
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        // 하트비트/스케줄러 (ConcurrentTaskScheduler → ThreadPoolTaskScheduler)
        ThreadPoolTaskScheduler ts = new ThreadPoolTaskScheduler();
        ts.setPoolSize(1);
        ts.setThreadNamePrefix("test-ws-");
        ts.initialize();
        stompClient.setTaskScheduler(ts);
        stompClient.setDefaultHeartbeat(new long[]{10000, 10000});

        String url = "ws://localhost:" + port + "/ws-stomp";

        // 2) 연결 (ListenableFuture → CompletableFuture)
        CompletableFuture<StompSession> cf =
                stompClient.connectAsync(url, new StompSessionHandlerAdapter() {});
        session = cf.get(5, TimeUnit.SECONDS);

        // 3) /room 구독
        BlockingQueue<String> queue = new LinkedBlockingQueue<>();
        session.subscribe("/room", new StompFrameHandler() {
            @Override public Type getPayloadType(StompHeaders headers) { return byte[].class; }
            @Override public void handleFrame(StompHeaders h, Object p) {
                String body = (p instanceof byte[]) ? new String((byte[]) p, StandardCharsets.UTF_8) : String.valueOf(p);
                queue.offer(body);
            }
        });

        // 4) /app/echo 전송
        session.send("/app/echo", "hello".getBytes(StandardCharsets.UTF_8));

        // 5) 수신 검증
        String received = queue.poll(5, TimeUnit.SECONDS);
        assertThat(received).isEqualTo("echo: hello");
    }
}
