package com.eyedia.eyedia.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ScrapRequestDto {
    private Long userId;
    private Long paintingId;
    private String date;      // ISO 형식 추천: "2025-08-27"
    private String excerpt;   // 발췌 문구
    private String location;  // 전시 장소
    private String artist;
}