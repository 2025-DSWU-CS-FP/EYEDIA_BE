package com.eyedia.eyedia.repository;

import com.eyedia.eyedia.domain.Scrap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {
    List<Scrap> findByUserId(Long userId);
    List<Scrap> findByUserIdAndLocation(Long userId, String location);
    Page<Scrap> findByUserId(Long userId, Pageable pageable);


}