package com.eyedia.eyedia.repository;

import com.eyedia.eyedia.domain.Message;
import com.eyedia.eyedia.domain.Painting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByPainting(Painting painting);

    Optional<Message> findTopByPaintingOrderByCreatedAtDesc(Painting painting);

//    Optional<String> findLatestAiMessageByPaintingId(Long id);
}
