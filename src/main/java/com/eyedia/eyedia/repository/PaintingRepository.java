package com.eyedia.eyedia.repository;

import com.eyedia.eyedia.domain.Painting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaintingRepository extends JpaRepository<Painting, Long> {
    @Query("select p from Painting p where p.artId = :artId and p.user is null")
    List<Painting> findNullUserByArtId(@Param("artId") Long artId);
    Optional<Painting> findByPaintingId(Long paintingId);
    List<Painting> findByArtId(Long artId);
}
