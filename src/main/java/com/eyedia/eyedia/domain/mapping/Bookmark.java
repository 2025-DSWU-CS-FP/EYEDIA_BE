package com.eyedia.eyedia.domain.mapping;

import com.eyedia.eyedia.domain.Exhibition;
import com.eyedia.eyedia.domain.User;
import com.eyedia.eyedia.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@DynamicUpdate
@DynamicInsert
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "bookmark")
public class Bookmark extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookmarkId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exhibitions_id")
    private Exhibition exhibition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private User user;

    public void link(User user, Exhibition exhibition) {
        this.user = user;
        this.exhibition = exhibition;
        user.getBookmarks().add(this);
        exhibition.getBookmarks().add(this);
    }

    public void unlink() {
        if (user != null) user.getBookmarks().remove(this);
        if (exhibition != null) exhibition.getBookmarks().remove(this);
        this.user = null;
        this.exhibition = null;
    }

}
