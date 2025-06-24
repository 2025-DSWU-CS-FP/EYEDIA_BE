package com.eyedia.eyedia.domain;

import com.eyedia.eyedia.domain.common.BaseEntity;
import com.eyedia.eyedia.domain.enums.SenderType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "messages")
public class Message extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "messages_id")  // 컬럼 이름 통일성
    private Long messageId;

    @Enumerated(EnumType.STRING)
    private SenderType sender;

    @Lob
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paintings_id")
    private Painting painting;
}
