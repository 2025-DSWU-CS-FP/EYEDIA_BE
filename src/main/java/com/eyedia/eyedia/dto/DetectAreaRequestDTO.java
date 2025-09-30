package com.eyedia.eyedia.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DetectAreaRequestDTO {

    @NotNull
    private Long artId;

    @NotEmpty
    private List<@NotBlank String> q;

    @NotNull
    @Size(min = 1)
    private List<CropItemDTO> list;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CropItemDTO {

        @NotBlank
        private String cropId;

        @NotBlank
        private String cropPath;

        @NotBlank
        private String cropDescription;

        @NotNull
        private String primaryQuadrant; // 주요 사분면

        @NotNull
        private String quadrant; // 포함된 사분면

        @DecimalMin(value = "0.0")
        @DecimalMax(value = "1.0")
        private Double ratio;
    }
}