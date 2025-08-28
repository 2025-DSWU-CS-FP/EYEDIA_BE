package com.eyedia.eyedia.domain.enums;

import io.swagger.v3.oas.annotations.media.Schema;

public enum ViewedSort {
    @Schema(description = "사용자 방문 최신순")
    RECENT,
    @Schema(description = "사용자 방문 날짜 오래된 순")
    DATE
}
