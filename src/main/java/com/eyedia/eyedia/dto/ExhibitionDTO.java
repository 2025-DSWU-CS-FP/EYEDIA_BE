package com.eyedia.eyedia.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public class ExhibitionDTO {

    @Getter
    public static class ExhibitionRequestDTO {

    }

    /**
     * 전시의 썸네일 리스트 반환
     *
     * 사용: 인기 전시 리스트 반환
     * */
    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ExhibitionSimpleResponseDTO {
        // 전시 타이틀, 장소, 작가, 썸네일 이미지만 넣어주세요!

        Long exhibitionId;
        String exhibitionTitle;
        String exhibitionImage;
        Integer artCount;
        String gallery;

    }
    /**
     * 전시의 상세 내용 반환
     *
     * 사용: 인기 전시 리스트 반환
     * */
    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ExhibitionDetailResponseDTO {
        // 전시 타이틀, 장소, 작가, 썸네일 이미지만 넣어주세요!

        Long exhibitionId;
        String exhibitionTitle;
        String gallery;
        String exhibitionDescription;
        String exhibitionDate;
        String exhibitionImage;
        String exhibitionAuthor;
        String location;

    }
    /**
     * 사용자의 전시의 상세 내용 반환
     *
     * 사용: 상세 전시 내용 & 스크랩들
     * */
    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class MyExhibitionDetailResponseDTO {
        // 전시 타이틀, 갤러리, 작품 갯수, 썸네일, 마지막 감상일, 북마크 여, 발췌 리스트

        Long exhibitionId;
        String exhibitionTitle;
        String gallery;
        String exhibitionDate;
        String exhibitionImage;
        String exhibitionAuthor;
        LocalDateTime visitedAt;

        public boolean bookmark;

        List<ScrapCard> scrapCards;
    }

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ScrapCard {
        String data;
    }

}
