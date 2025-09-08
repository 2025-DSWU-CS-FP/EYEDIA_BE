package com.eyedia.eyedia.repository;

import com.eyedia.eyedia.domain.badge.BadgeDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BadgeDefinitionRepository extends JpaRepository<BadgeDefinition, Long> {
    List<BadgeDefinition> findAllByEnabledTrueOrderBySortOrderAsc();
    Optional<BadgeDefinition> findByCode(String code);
}
