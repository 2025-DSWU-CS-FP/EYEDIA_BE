package com.eyedia.eyedia.repository;

import com.eyedia.eyedia.domain.ArtObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArtObjectRepository extends JpaRepository<ArtObject, Long> {

    Optional<ArtObject> findByObjectId(String objectId);

}
