package com.eyedia.eyedia.dto;

import jakarta.persistence.Id;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceTask {

    private String taskId;     // UUID
    private String type;       // "PLAY_AUDIO"
    private String audioUrl;   // mp3 URL
    private String text;       // (선택) 자막/디버깅
    private long createdAt;    // epoch ms
}
