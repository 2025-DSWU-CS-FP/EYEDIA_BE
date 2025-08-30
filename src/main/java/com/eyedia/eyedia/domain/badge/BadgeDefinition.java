package com.eyedia.eyedia.domain.badge;

import com.eyedia.eyedia.domain.common.BaseEntity;
import com.eyedia.eyedia.domain.enums.badge.AggregationType;
import com.eyedia.eyedia.domain.enums.badge.BadgeCategory;
import com.eyedia.eyedia.domain.enums.badge.EventType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "badge_definition")
public class BadgeDefinition extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 예: FIRST_COLLECTION, STREAK_3D ... */
    @Column(nullable = false, unique = true, length = 64)
    private String code;

    @Column(name = "title", nullable = false, length = 80)
    private String title;

    @Column(name = "description", nullable = false, length = 80)
    private String descriptionKey;

    /** COLLECTION / VIEW / STREAK / WEEKEND / ETC */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private BadgeCategory category;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    /** COUNT / STREAK / WEEKEND_COUNT */
    @Enumerated(EnumType.STRING)
    @Column(name = "evaluator_type", nullable = false, length = 24)
    private AggregationType evaluatorType;

    /** EXHIBITION_COLLECTED / ART_VIEWED / VISIT_LOGGED */
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 32)
    private EventType eventType;

    @Column(name = "goal_value", nullable = false)
    private Integer goalValue;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "params_json", columnDefinition = "json")
    private String paramsJson;

    @Column(name = "start_at")
    private LocalDateTime startAt;

    @Column(name = "end_at")
    private LocalDateTime endAt;
}