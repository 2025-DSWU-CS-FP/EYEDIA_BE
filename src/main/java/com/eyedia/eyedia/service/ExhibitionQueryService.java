package com.eyedia.eyedia.service;

import com.eyedia.eyedia.config.jwt.JwtProvider;
import com.eyedia.eyedia.domain.Exhibition;
import com.eyedia.eyedia.domain.enums.ViewedSort;
import com.eyedia.eyedia.dto.ExhibitionDTO;
import com.eyedia.eyedia.global.error.exception.GeneralException;
import com.eyedia.eyedia.global.error.status.ErrorStatus;
import com.eyedia.eyedia.repository.BookmarkRepository;
import com.eyedia.eyedia.repository.ExhibitionRepository;
import com.eyedia.eyedia.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExhibitionQueryService {

    private final ExhibitionRepository popularityRepository;
    private final VisitRepository visitRepository;
    private final BookmarkRepository bookmarkRepository;
    private final JwtProvider jwtProvider;

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
        var entity = popularityRepository.findById(exhibitionId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.INVALID_EXHIBITION_ID));
        // to dto
        var dto = toDetailDTO(entity);
        return dto;
    }

    // 조회
    public List<ExhibitionDTO.ExhibitionSimpleResponseDTO> suggest(String q, int limit) {
        q = q.trim();
        if (q.isEmpty()) return List.of();

        var list = popularityRepository
                .findByTitleContainingIgnoreCaseOrGalleryContainingIgnoreCase(
                        q, q, PageRequest.of(0, limit));

        return list.stream().map(this::toSimpleDto).toList();
    }

    // 유저가 방문한 전시 조회_최신순

    // Service
    public Page<ExhibitionDTO.ExhibitionSimpleResponseDTO>
    getMyVisitedExhibitionsLatest(Long userId, int page, int size) {
        Page<Exhibition> result =
                visitRepository.findVisitedExhibitionsLatestFirst(userId, PageRequest.of(page, size));
        return result.map(this::toSimpleDto);
    }

    // 나의 전시 - 내가 방문한 전시 상세 페이지 & 발췌 기록들 보여주기
    public ExhibitionDTO.MyExhibitionDetailResponseDTO getMyVisitedExhibitionDetail(Long uid, Long exhibitionId) {
        // 전시 조회
        var exhibition = popularityRepository.findById(exhibitionId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.INVALID_EXHIBITION_ID));

        // 전시 방문 날짜 조회
        var visitedAt = visitRepository.getVisitedAt(uid, exhibitionId);
        // 방문 이력 없으면 에러
        if (visitedAt == null) {
            throw new GeneralException(ErrorStatus.VISIT_RECORD_NOT_FOUND);
        }
        // 데이터 꺼내오기
        var dto = toMyDetailDTo(exhibition, visitedAt, isBookmarked(uid, exhibitionId));

        // 발췌 조회

        // return
        return dto;
    }
    // 나의 전시 - 필터링 (전체 or 즐겨찾기) & (최신순 or 날짜순) & (검색어 있음 or 없음)
    @Transactional(readOnly = true)
    public Page<ExhibitionDTO.ExhibitionSimpleResponseDTO> getMyViewed(
            Long userId,
            String keyword,
            boolean isBookmarked,
            ViewedSort sort,
            Pageable pageable
    ) {
        if (userId == null) throw new IllegalArgumentException("userId must not be null");
        final String kw = (keyword == null || keyword.isBlank()) ? null : keyword;

        Page<Exhibition> result;
        if (isBookmarked) {
            result = (sort == ViewedSort.RECENT)
                    ? bookmarkRepository.findBookmarkViewedOrderByRecent(userId, kw, pageable)
                    : bookmarkRepository.findBookmarkViewedOrderByOldest(userId, kw, pageable);
        } else {
            result = (sort == ViewedSort.RECENT)
                    ? visitRepository.findViewedOrderByRecent(userId, kw, pageable)
                    : visitRepository.findViewedOrderByOldest(userId, kw, pageable);
        }
        return result.map(this::toSimpleDto);
    }

    public boolean isBookmarked(Long userId, Long exhibitionId) {
        return bookmarkRepository.existsByUser_UsersIdAndExhibition_ExhibitionsId(userId, exhibitionId);
    }

    // --- Mapper ---

    private ExhibitionDTO.ExhibitionSimpleResponseDTO toSimpleDto(Exhibition e) {
        return ExhibitionDTO.ExhibitionSimpleResponseDTO.builder()
                .exhibitionId(e.getExhibitionsId())
                .exhibitionTitle(e.getTitle())
                .exhibitionImage(e.getPosterUrl())
                .artCount(e.getArtCount())
                .gallery(e.getGallery())
                .build();
    }

    private ExhibitionDTO.ExhibitionDetailResponseDTO toDetailDTO(Exhibition e) {
        return ExhibitionDTO.ExhibitionDetailResponseDTO.builder()
                .exhibitionId(e.getExhibitionsId())
                .exhibitionTitle(e.getTitle() != null ? e.getTitle() : "미정")
                .exhibitionDescription(e.getDescription() != null ? e.getDescription() : "미정")
                .exhibitionDate(formatDateRange(e))
                .exhibitionImage(e.getPosterUrl() != null ? e.getPosterUrl() : "미정")
                .exhibitionAuthor(e.getArtist())
                .location(e.getLocation())
                .gallery(e.getGallery() != null ? e.getGallery() : "미정")
                .build();
    }

    private ExhibitionDTO.MyExhibitionDetailResponseDTO toMyDetailDTo(Exhibition e, LocalDateTime time, boolean isBookmarked) {
        return ExhibitionDTO.MyExhibitionDetailResponseDTO.builder()
                .exhibitionId(e.getExhibitionsId())
                .exhibitionTitle(e.getTitle() != null ? e.getTitle() : "미정")
                .exhibitionDate(formatDateRange(e))
                .exhibitionImage(e.getPosterUrl() != null ? e.getPosterUrl() : "미정")
                .exhibitionAuthor(e.getArtist())
                .scrapCards(new ArrayList<>())
                .visitedAt(time)
                .bookmark(isBookmarked)
                .gallery(e.getGallery() != null ? e.getGallery() : "미정")
                .build();
    }

    private String formatDateRange(Exhibition e) {
        if (e.getStartDate() == null || e.getEndDate() == null) return "";
        return e.getStartDate().toLocalDate() + " ~ " + e.getEndDate().toLocalDate();
    }

    public long getMyVisitedExhibitionCount(Long userId) {
        if (userId == null) throw new IllegalArgumentException("userId must not be null");
        return visitRepository.countDistinctExhibitionsByUserId(userId);
    }
}
