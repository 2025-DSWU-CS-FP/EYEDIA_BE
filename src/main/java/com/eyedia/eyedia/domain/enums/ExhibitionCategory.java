package com.eyedia.eyedia.domain.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum ExhibitionCategory {
    // 시대
    ANCIENT("고대/고전"),
    RENAISSANCE("르네상스"),
    MODERN("근대"),
    CONTEMPORARY("현대"),

    // 색감
    WARM("따뜻한 색감"),
    COOL("차가운 색감"),
    MONOTONE("모노톤/무채색"),
    PASTEL("파스텔톤"),

    // 감정/분위기
    HEALING("힐링되는"),
    HUMOROUS("유머러스한"),
    EMOTIONAL("감성적인"),
    CALM("차분한"),
    PASSIONATE("정열적인"),

    // 표현 방식
    INTERACTIVE("인터랙티브한"),
    OBSERVATIONAL("관찰을 유도하는"),
    REPETITIVE("반복적인"),
    SCARY("무서운");

    private final String koreanName;

    ExhibitionCategory(String koreanName) {
        this.koreanName = koreanName;
    }

    public String getCode() {
        return name();          // 영문 Enum 이름
    }
}
