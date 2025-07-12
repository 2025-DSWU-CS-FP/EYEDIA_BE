package com.eyedia.eyedia.repository;

import com.eyedia.eyedia.domain.Exhibition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExhibitionRepository extends JpaRepository<Exhibition, Long> {
    Optional<Exhibition> findByTitle(String title);
}
