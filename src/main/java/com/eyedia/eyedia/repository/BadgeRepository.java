package com.eyedia.eyedia.repository;

import com.eyedia.eyedia.domain.badge.Badge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BadgeRepository extends JpaRepository<Badge, Long> {
    Optional<Badge> findByUser_UsersIdAndCode(Long userId, String code);

    List<Badge> findAllByUser_UsersId(Long userId);

    @Query("""
        select b
        from Badge b
        where b.user.usersId = :userId
    """)
    List<Badge> findAllByUsersId(Long userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Badge b set b.newBadge = false " +
            "where b.id in :ids and b.newBadge = true")
    int markNotNewByIds(@Param("ids") List<Long> ids);


}