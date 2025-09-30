package com.eyedia.eyedia.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PaintingMetadataRequest {

    private Long artId;
    private String title;
    private String artist;
    private String description;
    private Long exhibition;
    private String imageUrl;
}