package com.eyedia.eyedia.domain.badge;

import com.eyedia.eyedia.domain.User;
import com.eyedia.eyedia.domain.common.BaseEntity;
import com.eyedia.eyedia.domain.enums.badge.ProgressStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "badge")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Badge extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "users_id", nullable = false)
    private User user;

    /** 배지 코드. 새 배지 추가해도 코드 변경 불필요 */
    @Column(nullable = false, length = 64)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private ProgressStatus status;

    @Column(nullable = false)
    private int currentValue;

    @Column(nullable = false)
    private int goalValue;

    /** 스트릭/주간 카운트용 보조 필드 */
    private LocalDate lastProgressDate; // 최근 반영 일
    private LocalDate weekStart;        // 주 시작일(월요일 기준 등)

    /** COUNT evaluator용 중복 방지 보조 키 */
    @Column(length = 128)
    private String lastDistinctKey;

    /** 달성 시각 */
    private LocalDateTime achievedAt;

    @Column(columnDefinition = "json")
    private String metaJson;

    @Column(nullable = false, length = 64)
    private String title;

    @Column(nullable = false, length = 64)
    private String description;

    @Column(name = "is_new", nullable = false)
    boolean newBadge = false;

}