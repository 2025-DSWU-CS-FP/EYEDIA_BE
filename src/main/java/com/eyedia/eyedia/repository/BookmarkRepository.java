package com.eyedia.eyedia.repository;

import com.eyedia.eyedia.domain.mapping.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository  extends JpaRepository<Bookmark, Long> {


}
