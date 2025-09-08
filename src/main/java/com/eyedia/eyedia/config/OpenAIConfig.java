package com.eyedia.eyedia.config;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

import java.time.Duration;

@Configuration
public class OpenAIConfig {

    @Bean
    public OpenAIClient openAIClient(
            @Value("${openai.api-key}") String apiKey) {

        return OpenAIOkHttpClient.builder()
                .apiKey(apiKey)
                .timeout(Duration.ofSeconds(20))
                .maxRetries(2)
                .build();
    }
}
