package com.eyedia.eyedia.repository;

import com.eyedia.eyedia.domain.Object;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ObjectRepository extends JpaRepository<Object, Long> {
}
