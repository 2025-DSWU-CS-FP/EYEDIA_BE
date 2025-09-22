package com.eyedia.eyedia.service;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service

public class DummyTtsService implements TtsService {
    @Override
    public String synthesizeAndGetUrl(String text, String voice){
        return "http://localhost:8080/tts/sample-ok.mp3";
    }
}
