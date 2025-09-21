package com.eyedia.eyedia.repository;

import com.eyedia.eyedia.domain.Exhibition;
import com.eyedia.eyedia.domain.mapping.Visit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface VisitRepository extends JpaRepository<Visit, Long> {

    // 유저가 방문한 전시의 "개수" (중복 없이)
    @Query("SELECT COUNT(DISTINCT v.exhibition.exhibitionsId) " +
            "FROM Visit v WHERE v.user.usersId = :userId")
    long countDistinctExhibitionsByUserId(@Param("userId") Long userId);

    // 사용자가 방문한 전시 목록 반환(distinct)
    @Query(
            value = """
                select distinct v.exhibition
                from Visit v
                where v.user.id = :userId
            """,
            countQuery = """
                select count(distinct v.exhibition.exhibitionsId)
                from Visit v
                where v.user.id = :userId
            """
    )
    Page<Exhibition> findDistinctVisitedExhibitions(
            @Param("userId") Long userId,
            Pageable pageable
    );
    // 최신방문순
    @Query(
            value = """
                select v.exhibition
                from Visit v
                where v.user.usersId = :userId
                group by v.exhibition
                order by max(v.visitedAt) desc
            """,
            countQuery = """
                select count(distinct v.exhibition.exhibitionsId)
                from Visit v
                where v.user.usersId = :userId
            """
    )
    Page<Exhibition> findVisitedExhibitionsLatestFirst(
            @Param("userId") Long userId,
            Pageable pageable
    );

    @Query("""
            select max(v.visitedAt)
                from Visit v
                where v.user.usersId = :userId and v.exhibition.exhibitionsId = :exhibitionId
           

            """)
    public LocalDateTime getVisitedAt(
            @Param("userId") Long userId,
            @Param("exhibitionId") Long exhibitionId
    );

    // 전체 + 최신순 (MAX(visitedAt) DESC) + keyword 필터
    @Query("""
    select e
    from Visit v
      join v.exhibition e
    where v.user.usersId = :userId
      and (
        :keyword is null or :keyword = '' or
        lower(e.title)   like lower(concat('%', :keyword, '%')) or
        lower(e.gallery) like lower(concat('%', :keyword, '%')))
    group by e
    order by max(v.visitedAt) desc
    """)
    Page<Exhibition> findViewedOrderByRecent(
            @Param("userId") Long userId,
            @Param("keyword") String keyword,
            Pageable pageable
    );
    // 전체 + 오래된순 (MAX(visitedAt) DESC) + keyword 필터
    @Query("""
        select e
        from Visit v join v.exhibition e
        where v.user.usersId = :userId
          and (:keyword is null or :keyword = '' 
               or lower(e.title) like lower(concat('%', :keyword, '%'))
               or lower(e.gallery) like lower(concat('%', :keyword, '%')))

        group by e
        order by min(v.visitedAt) asc
        """)
    Page<Exhibition> findViewedOrderByOldest(
            @Param("userId") Long userId,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Query("""
        select count(v)
        from Visit v
        where v.user.usersId = :userId
            and YEAR(v.visitedAt) = :year
            and MONTH(v.visitedAt) = :month
    """)
    Integer countVisitsByUserAndMonth(@Param("userId") Long userId,
                                   @Param("year") int year,
                                   @Param("month") int month);

}