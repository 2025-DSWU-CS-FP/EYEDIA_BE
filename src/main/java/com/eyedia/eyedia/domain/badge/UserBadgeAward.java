package com.eyedia.eyedia.domain.badge;

import com.eyedia.eyedia.domain.User;
import com.eyedia.eyedia.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_badge_award")
public class UserBadgeAward extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="users_id", nullable=false)
    private User user;

    /** 어떤 배지(code)를 달성했는지 기록 */
    @Column(nullable = false, length = 64)
    private String code;

    @Column(nullable = false)
    private LocalDateTime achievedAt;

    @Column(length = 120)
    private String achievedReason;
}
