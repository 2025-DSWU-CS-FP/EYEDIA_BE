package com.eyedia.eyedia.domain;

import com.eyedia.eyedia.domain.badge.Badge;
import com.eyedia.eyedia.domain.badge.UserBadgeAward;
import com.eyedia.eyedia.domain.common.BaseEntity;
import com.eyedia.eyedia.domain.enums.ExhibitionCategory;
import com.eyedia.eyedia.domain.enums.Gender;
import com.eyedia.eyedia.domain.mapping.Bookmark;
import com.eyedia.eyedia.domain.mapping.Visit;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@DynamicUpdate
@DynamicInsert
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long usersId;

    private String name;

    private Integer age;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String profileImage;

    @Column(nullable = false)
    private String id; // 로그인 ID

    // @Column(nullable = true)
    private String pw;

    private String currentLocation;

    // 소셜 고유키 (provider + providerId 조합이 유니크)
    @Column(unique = true)
    private String oauthKey; // e.g., "naver:abcd1234"

    private String provider;      // "naver"
    private String providerId;    // 네이버의 id

    @Builder.Default
    private boolean isFirstLogin = true;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Exhibition> exhibitions = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Painting> paintings = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Bookmark> bookmarks = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Visit> visits = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Badge> badges = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserBadgeAward> userBadgeAwards = new ArrayList<>();

    // --- 회원가입 시 선택한 이넘 키워드 보관 ---
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "user_selected_keywords",
            joinColumns = @JoinColumn(name = "users_id") // FK 컬럼명 명시
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "keyword", length = 32, nullable = false)
    @Builder.Default
    private java.util.Set<ExhibitionCategory> selectedKeywords = new java.util.HashSet<>();

    // 편의 메서드
    public void setSelectedKeywords(java.util.Collection<ExhibitionCategory> keywords) {
        this.selectedKeywords.clear();
        if (keywords != null) this.selectedKeywords.addAll(keywords);
    }
    public void addKeyword(ExhibitionCategory k) { this.selectedKeywords.add(k); }
    public void removeKeyword(ExhibitionCategory k) { this.selectedKeywords.remove(k); }


    public void setIsFirstLogin(boolean isFirstLogin) {
        this.isFirstLogin = isFirstLogin;
    }

}
