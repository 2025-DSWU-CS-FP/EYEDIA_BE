package com.eyedia.eyedia.domain;

import jakarta.persistence.*;
import lombok.*;

import static org.apache.commons.logging.LogFactory.objectId;

@Entity
@Table(name = "objects")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArtObject {

    @Id
    private String objectId;

    private String imageUrl;

    private String description;



}
