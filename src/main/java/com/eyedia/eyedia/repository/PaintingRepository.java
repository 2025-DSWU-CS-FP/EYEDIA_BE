package com.eyedia.eyedia.repository;

import com.eyedia.eyedia.domain.Painting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaintingRepository extends JpaRepository<Painting, Long> {
    Optional<Painting> findByPaintingsId(Long paintingsId);
}
