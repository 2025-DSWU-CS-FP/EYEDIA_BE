package com.eyedia.eyedia.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;
import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class AreaDetectDTO {

    @NotNull
    private Long artId;

    @Builder.Default
    private List<String> q = List.of();

    @Valid
    @Size(min = 1)
    private List<Crop> list;

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Crop {
        @NotNull private String cropId;
        @NotNull private String cropPath;
        private String cropDescription;

        @NotNull
        private String primaryQuadrant;
        private String quadrant;

        @DecimalMin("0.0") @DecimalMax("1.0")
        private Double ratio;
    }
}