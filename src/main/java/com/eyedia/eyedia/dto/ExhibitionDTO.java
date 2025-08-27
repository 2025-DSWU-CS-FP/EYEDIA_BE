package com.eyedia.eyedia.dto;

import lombok.*;

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



}
