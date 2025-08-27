package com.eyedia.eyedia.service;

import com.eyedia.eyedia.domain.Exhibition;
import com.eyedia.eyedia.dto.ExhibitionDTO;
import com.eyedia.eyedia.repository.ExhibitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ExhibitionQueryService {

    private final ExhibitionRepository popularityRepository;

    /**
     * 메인페이지 인기 전시 (전역 북마크 순) - DTO 리스트
     */
    public List<ExhibitionDTO.ExhibitionSimpleResponseDTO> getPopularTopN(int size) {
        Page<Exhibition> page = popularityRepository.findPopular(PageRequest.of(0, size));
        return page.getContent().stream().map(this::toSimpleDto).toList();
    }

    /**
     * 메인페이지 인기 전시 (전역 북마크 순) - 페이징
     */
    public Page<ExhibitionDTO.ExhibitionSimpleResponseDTO> getPopularPaged(int page, int size) {
        Page<Exhibition> result = popularityRepository.findPopular(PageRequest.of(page, size));
        List<ExhibitionDTO.ExhibitionSimpleResponseDTO> content = result.getContent()
                .stream().map(this::toSimpleDto).toList();
        return new PageImpl<>(content, result.getPageable(), result.getTotalElements());
    }
    public ExhibitionDTO.ExhibitionDetailResponseDTO getPopularDetailPage(Long exhibitionId) {
        var entity = popularityRepository.findById(exhibitionId).get();
        // to dto
        var dto = toDetailDTO(entity);
        return dto;

        // TODO: 예외 처리: 없는 아이디 조회 등등
    }
    // 조회
    public List<ExhibitionDTO.ExhibitionSimpleResponseDTO> suggest(String q, int limit) {
        var list = popularityRepository.findByTitleStartingWith(q.trim(), PageRequest.of(0, limit));
        return list.stream().map(this::toSimpleDto).toList();
    }



    // --- Mapper ---

    private ExhibitionDTO.ExhibitionSimpleResponseDTO toSimpleDto(Exhibition e) {
        return ExhibitionDTO.ExhibitionSimpleResponseDTO.builder()
                .exhibitionId(e.getExhibitionsId())
                .exhibitionTitle(e.getTitle())
                .exhibitionImage(e.getPosterUrl())
                .artCount(e.getArtCount())
                .build();
    }

    private ExhibitionDTO.ExhibitionDetailResponseDTO toDetailDTO(Exhibition e) {
        return ExhibitionDTO.ExhibitionDetailResponseDTO.builder()
                .exhibitionId(e.getExhibitionsId())
                .exhibitionTitle(e.getTitle()!= null ? e.getTitle() : "미정")
                .exhibitionDescription(e.getDescription()!= null ? e.getDescription() : "미정")
                .exhibitionDate(formatDateRange(e))
                .exhibitionImage(e.getPosterUrl()!= null ? e.getPosterUrl() : "미정")
                .exhibitionAuthor(e.getArtist())
                .location(e.getLocation())
                .gallery(e.getGallery()!= null ? e.getGallery() : "미정")
                .build();
    }

    private String formatDateRange(Exhibition e) {
        if (e.getStartDate() == null || e.getEndDate() == null) return "";
        return e.getStartDate().toLocalDate() + " ~ " + e.getEndDate().toLocalDate();
    }


}
