package com.eyedia.eyedia.repository;

import com.eyedia.eyedia.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("SELECT m FROM Message m WHERE m.painting.paintingsId = :paintingId AND m.sender = 'AI' ORDER BY m.createdAt DESC")
    Optional<Message> findLatestAiMessageByPaintingId(@Param("paintingId") Long paintingId);

    List<Message> findByPainting_PaintingsIdOrderByCreatedAtAsc(Long paintingId);
}
