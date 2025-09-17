package com.eyedia.eyedia.dto;

import lombok.*;
import java.util.List;

@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class RecommendedExhibitionsResponse {
    private String keyword; // 예: "BRIGHT"
    private List<Item> exhibitions;

    @Getter @Setter @Builder
    @AllArgsConstructor @NoArgsConstructor
    public static class Item {
        private Long id;
        private String title;
        private String artist;
        private String location;
        private String thumbnailUrl;
    }
}