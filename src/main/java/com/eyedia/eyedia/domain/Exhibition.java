package com.eyedia.eyedia.domain;

import com.eyedia.eyedia.domain.common.BaseEntity;
import com.eyedia.eyedia.domain.enums.ExhibitionCategory;
import com.eyedia.eyedia.domain.mapping.Bookmark;
import com.eyedia.eyedia.domain.mapping.Visit;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@DynamicUpdate
@DynamicInsert
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "exhibitions")
public class Exhibition extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long exhibitionsId;

    private String title; // 전시 이름 ex) 요시고 사진전
    private String gallery; // 장소 ex) 서울시립미술관 서소문본관

    @Column(length = 500)
    private String description;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private String posterUrl;
    private Integer visitCount;
    private String artist;

     @Enumerated(EnumType.STRING)
     private ExhibitionCategory category;

   // private String category;

    private String location;

    private Integer artCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private User user;

    @OneToMany(mappedBy = "exhibition", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Painting> paintings = new ArrayList<>();

    @OneToMany(mappedBy = "exhibition", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Bookmark> bookmarks = new ArrayList<>();

    @OneToMany(mappedBy = "exhibition", cascade = CascadeType.ALL)
    private List<Visit> visits = new ArrayList<>();

}

