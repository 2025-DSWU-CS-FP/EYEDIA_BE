package com.eyedia.eyedia.repository;

import com.eyedia.eyedia.domain.Exhibition;
import com.eyedia.eyedia.domain.mapping.Bookmark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BookmarkRepository  extends JpaRepository<Bookmark, Long> {

    @Query("""
    select e
    from Bookmark b
      join b.exhibition e
    where b.user.usersId = :userId
      and (:keyword is null or :keyword = ''
           or lower(e.title) like lower(concat('%', :keyword, '%'))
           or lower(e.gallery) like lower(concat('%', :keyword, '%')))
    order by (
        select max(v.visitedAt)
        from Visit v
        where v.user.usersId = :userId and v.exhibition = e
    ) desc
""")
    Page<Exhibition> findBookmarkViewedOrderByRecent(
            @Param("userId") Long userId,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Query("""
    select e
    from Bookmark b
      join b.exhibition e
    where b.user.usersId = :userId
      and (:keyword is null or :keyword = ''
           or lower(e.title) like lower(concat('%', :keyword, '%'))
           or lower(e.gallery) like lower(concat('%', :keyword, '%')))
    order by (
        select min(v.visitedAt)
        from Visit v
        where v.user.usersId = :userId and v.exhibition = e
    ) asc
""")
    Page<Exhibition> findBookmarkViewedOrderByOldest(
            @Param("userId") Long userId,
            @Param("keyword") String keyword,
            Pageable pageable
    );
    // 이전에 북마크 여부 확인
    boolean existsByUser_UsersIdAndExhibition_ExhibitionsId(Long usersId, Long exhibitionsId);
    // 사용자 아이디, 전시 아이디로 북마크 조회
    Optional<Bookmark> findBookmarkByUser_UsersIdAndExhibition_ExhibitionsId(Long usersId, Long exhibitionsId);

}
