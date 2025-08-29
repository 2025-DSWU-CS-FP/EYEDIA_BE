package com.eyedia.eyedia.domain.mapping;

import com.eyedia.eyedia.domain.Exhibition;
import com.eyedia.eyedia.domain.User;
import com.eyedia.eyedia.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@DynamicUpdate
@DynamicInsert
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "visit")
public class Visit extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long visitId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "users_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "exhibitions_id")
    private Exhibition exhibition;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime visitedAt;
}
