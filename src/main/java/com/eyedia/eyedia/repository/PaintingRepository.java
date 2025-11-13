package com.eyedia.eyedia.repository;

import com.eyedia.eyedia.domain.Exhibition;
import com.eyedia.eyedia.domain.Painting;
import com.eyedia.eyedia.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaintingRepository extends JpaRepository<Painting, Long> {

    @Query("SELECT p FROM Painting p WHERE p.artId = :artId AND p.user.usersId = :userId")
    List<Painting> findByUserAndArtId(@Param("userId") Long userId, @Param("artId") Long artId);

    @Query("SELECT p FROM Painting p WHERE p.user is null AND (p.artId = :artId OR :artId is null)")
    List<Painting> findNullUserByArtId(@Param("artId") Long artId);

    Optional<Painting> findByPaintingId(Long paintingId);
    List<Painting> findByArtId(Long artId);
    List<Painting> findByUserAndExhibition(User user, Exhibition exhibition);
}
