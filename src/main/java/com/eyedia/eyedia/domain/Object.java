package com.eyedia.eyedia.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "objects")
public class Object {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long objectsId;

    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paintings_id")
    private Painting painting;
}
