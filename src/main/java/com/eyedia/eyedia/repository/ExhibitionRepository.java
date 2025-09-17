package com.eyedia.eyedia.repository;

import com.eyedia.eyedia.domain.Exhibition;
import com.eyedia.eyedia.domain.enums.ExhibitionCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExhibitionRepository extends JpaRepository<Exhibition, Long> {
    Optional<Exhibition> findByTitle(String title);
    Optional<Exhibition> findByPaintingsPaintingId(Long paintingId);

    // 북마크 등록 순: PK(=생성순) 기준
    @Query("""
           select b.exhibition
           from Bookmark b
           where b.user.id = :userId
           order by b.bookmarkId asc
           """)
    List<Exhibition> findBookmarkedExhibitions(@Param("userId") Long userId);

    /**
     * 전시별 북마크 수를 집계해서 인기순(북마크 카운트 desc)으로 정렬
     * - LEFT JOIN: 북마크 0개 전시도 포함 (원치 않으면 INNER JOIN으로 변경)
     */
    @Query(
            value = """
                select e
                from Exhibition e
                left join e.bookmarks b
                group by e
                order by count(b) desc, coalesce(e.startDate, e.createdAt), e.exhibitionsId desc
                """,
            countQuery = """
                select count(e)
                from Exhibition e
                """
    )
    Page<Exhibition> findPopular(Pageable pageable);
    List<Exhibition> findByTitleStartingWith(String q, Pageable pageable);

    // 여러 필드 시작일치 동시 검색
    List<Exhibition> findByTitleContainingIgnoreCaseOrGalleryContainingIgnoreCase(
            String titlePart, String galleryPart, Pageable pageable
    );

    List<Exhibition> findByCategoryOrderByVisitCountDesc(ExhibitionCategory category, Pageable pageable);

}
