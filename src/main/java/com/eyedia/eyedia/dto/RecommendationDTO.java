package com.eyedia.eyedia.dto;

import com.eyedia.eyedia.domain.enums.ExhibitionCategory;
import lombok.*;

@Getter @AllArgsConstructor @NoArgsConstructor @Builder
public class RecommendationDTO {
    private java.util.List<ExhibitionCategory> keywords;
}
