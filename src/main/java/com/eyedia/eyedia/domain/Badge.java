package com.eyedia.eyedia.domain;

import com.eyedia.eyedia.domain.enums.BadgeStatus;
import com.eyedia.eyedia.domain.enums.BadgeType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "badge")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Badge {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(nullable = false, length = 255)
    private String description;

    @Enumerated(EnumType.STRING)
    private BadgeType type;

    private Integer goalValue;

    private Integer currentValue;

    @Enumerated(EnumType.STRING)
    private BadgeStatus status;

    private boolean achieved;

    private LocalDateTime achievedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}

