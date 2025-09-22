package com.eyedia.eyedia.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.*;
import java.security.MessageDigest;

@Slf4j
@Service
@Primary
public class OpenAiTtsService implements TtsService {
    @Value("${eyedia.tts.storageBaseDir:/var/eyedia/cdn/tts}")
    private String storageBaseDir;

    @Value("${eyedia.tts.cdnBaseUrl:http://localhost:8080}")
    private String cdnBaseUrl;

    @Value("${eyedia.tts.openaiApiKey}")
    private String openaiApiKey;

    @Value("${eyedia.tts.defaultVoice:alloy}")
    private String defaultVoice;

    private final HttpClient http = HttpClient.newHttpClient();

    @Override
    public String synthesizeAndGetUrl(String text, String voice) {
        try {
            String v = (voice == null || voice.isBlank()) ? defaultVoice : voice;

            // 1) 캐시 키 (sha256)
            String hash = sha256(text + "|" + v);
            Path dir = Paths.get(storageBaseDir, "tts");
            Path file = dir.resolve(hash + ".mp3");

            // 2) 이미 있으면 캐시된 URL 반환
            if (Files.exists(file)) {
                return cdnBaseUrl + "/tts/" + file.getFileName();
            }

            Files.createDirectories(dir);

            // 3) OpenAI 요청 JSON
            String body = """
            {
              "model": "gpt-4o-mini-tts",
              "voice": "%s",
              "input": %s,
              "format": "mp3"
            }
            """.formatted(v, toJsonString(text));

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/audio/speech"))
                    .header("Authorization", "Bearer " + openaiApiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            // 4) 요청 → mp3 바이트 받기
            HttpResponse<byte[]> resp = http.send(req, HttpResponse.BodyHandlers.ofByteArray());
            if (resp.statusCode() / 100 != 2) {
                throw new RuntimeException("OpenAI TTS failed: " + resp.statusCode() + " / " +
                        new String(resp.body()));
            }

            // 5) 저장
            Files.write(file, resp.body(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            log.info("TTS generated: {}", file);

            // 6) CDN URL 반환
            return cdnBaseUrl + "/tts/" + file.getFileName();

        } catch (Exception e) {
            log.error("OpenAI TTS error", e);
            return cdnBaseUrl + "/tts/sample-fallback.mp3";
        }
    }

    private static String sha256(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] d = md.digest(s.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : d) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String toJsonString(String s) {
        return "\"" + s
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "") + "\"";
    }
}
