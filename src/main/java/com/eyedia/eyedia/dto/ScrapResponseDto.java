package com.eyedia.eyedia.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ScrapResponseDto {
    private Long id;
    private Long userId;
    private Long paintingId;
    private String date;
    private String excerpt;
    private String location;
    private String imageUrl;
    private String artist;
    private String title;
    // private LocalDateTime createdAt;
}