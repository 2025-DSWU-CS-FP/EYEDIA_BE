package com.eyedia.eyedia.repository;

import com.eyedia.eyedia.domain.badge.UserBadgeAward;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserBadgeAwardRepository extends JpaRepository<UserBadgeAward, Long> {
    boolean existsByUser_UsersIdAndCode(Long userId, String code);
}

