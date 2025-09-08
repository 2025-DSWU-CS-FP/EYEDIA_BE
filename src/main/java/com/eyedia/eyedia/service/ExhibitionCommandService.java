package com.eyedia.eyedia.service;

import com.eyedia.eyedia.domain.Exhibition;
import com.eyedia.eyedia.domain.Painting;
import com.eyedia.eyedia.domain.mapping.Bookmark;
import com.eyedia.eyedia.domain.mapping.Visit;
import com.eyedia.eyedia.global.error.exception.GeneralException;
import com.eyedia.eyedia.global.error.status.ErrorStatus;
import com.eyedia.eyedia.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class ExhibitionCommandService {
    private final ExhibitionRepository popularityRepository;
    private final VisitRepository visitRepository;
    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final ExhibitionRepository exhibitionRepository;
    private final PaintingRepository paintingRepository;


    public void bookmarkVisitedExhibitionByUser(Long userId, Long exhibitionId) {
        // 예외 처리: 유효하지 않은 전시 id, 방문하지 않은 전시 id
        // 전시 조회
        var exhibition = popularityRepository.findById(exhibitionId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.INVALID_EXHIBITION_ID));
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        // 전시 방문 날짜 조회
        var visitedAt = visitRepository.getVisitedAt(userId, exhibitionId);
        // 방문 이력 없으면 에러
        if (visitedAt == null) {
            throw new GeneralException(ErrorStatus.VISIT_RECORD_NOT_FOUND);
        }
        // 이미 북마크 한 경우
        boolean isBookmarked = bookmarkRepository.existsByUser_UsersIdAndExhibition_ExhibitionsId(userId, exhibitionId);
        if (isBookmarked) {
            throw new GeneralException(ErrorStatus.ALREADY_BOOKMARK_EXISTS);

        }

        // 북마크
        var bookmark = Bookmark.builder()
                .exhibition(exhibition)
                .user(user)
                .build();
        bookmark.link(user, exhibition);
        bookmarkRepository.save(bookmark);

    }

    public void deleteBookmarkVisitedExhibitionByUser(Long uid, Long exhibitionId) {
        // 유저, 전시아이디 유효한지 확인
        var exhibition = popularityRepository.findById(exhibitionId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.INVALID_EXHIBITION_ID));
        var user = userRepository.findById(uid)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        var bookmark = bookmarkRepository.findBookmarkByUser_UsersIdAndExhibition_ExhibitionsId(uid, exhibitionId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.BOOKMARK_NOT_FOUND));
        bookmark.unlink();
        bookmarkRepository.delete(bookmark);

    }

    public Integer getMonthlyVisitCount(Long  userId) {

       var visitCount = visitRepository.countVisitsByUserAndMonth(
               userId, LocalDateTime.now().getYear(), LocalDateTime.now().getMonthValue());
       return visitCount == null ? 0 : visitCount;
    }

    @Transactional
    public void addVisit(Painting painting) {

        Exhibition exhibition = painting.getExhibition();
        if (exhibition == null) {
            throw new GeneralException(ErrorStatus.EXHIBITION_NULL_EXCEPTION);
        }
        var user = painting.getUser();
        if (user == null) {
            throw new GeneralException(ErrorStatus.USER_NOT_FOUND);
        }

        Visit visit = Visit.builder()
                .user(user)
                .exhibition(exhibition)
                .build();

        visitRepository.save(visit);
    }

    public Exhibition getExhibitionById(long l) {
        return exhibitionRepository.findById(1L).orElseThrow(() -> new GeneralException(ErrorStatus.INVALID_EXHIBITION_ID));
    }
}
