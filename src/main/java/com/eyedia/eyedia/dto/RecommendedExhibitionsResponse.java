package com.eyedia.eyedia.dto;

import com.eyedia.eyedia.domain.enums.ExhibitionCategory;
import lombok.*;
import java.util.List;

@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class RecommendedExhibitionsResponse {
    private ExhibitionCategory keyword;
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