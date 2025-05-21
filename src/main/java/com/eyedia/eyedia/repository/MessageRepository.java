package com.eyedia.eyedia.repository;

import com.eyedia.eyedia.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    Optional<String> findLatestAiMessageByPaintingId(Long id);
}
