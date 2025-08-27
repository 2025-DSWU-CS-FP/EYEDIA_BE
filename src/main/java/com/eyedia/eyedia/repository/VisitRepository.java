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

}