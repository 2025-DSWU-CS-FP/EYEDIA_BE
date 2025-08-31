package com.eyedia.eyedia.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "scraps")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Scrap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    // @Column(name = "painting_id")
    private Long paintingId;

    private LocalDate date;

    private String excerpt;

    private String location;

    private String artist;

    private LocalDate createdAt;
}