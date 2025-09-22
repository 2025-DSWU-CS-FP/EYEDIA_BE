package com.eyedia.eyedia.service;

import com.eyedia.eyedia.domain.User;
import com.eyedia.eyedia.domain.enums.ExhibitionCategory;
import com.eyedia.eyedia.dto.RecommendationDTO;
import com.eyedia.eyedia.dto.RecommendedExhibitionsResponse;
import com.eyedia.eyedia.repository.ExhibitionRepository;
import com.eyedia.eyedia.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendationService {
    private final UserRepository userRepository;
    private final ExhibitionRepository exhibitionRepository;

    public RecommendationDTO getMyKeywords(Long usersId) {
        var user = userRepository.getUserByUsersId(usersId);

        return RecommendationDTO.builder()
                .keywords(user.getSelectedKeywords().stream().toList())
                .build();
    }

    public List<RecommendedExhibitionsResponse> getMyRecommendedExhibitions(Long usersId, int size) {
        User user = userRepository.getUserByUsersId(usersId);

        var selected = user.getSelectedKeywords();
        if (selected == null || selected.isEmpty()) {
            return List.of(); // 아무 키워드도 없으면 빈 리스트
        }

        // 키워드마다 응답 생성
        return selected.stream()
                .map(category -> {
                    var exhibitions = exhibitionRepository.findByCategoryOrderByVisitCountDesc(
                            category, PageRequest.of(0, Math.max(size, 1)));

                    var items = exhibitions.stream().map(e ->
                            RecommendedExhibitionsResponse.Item.builder()
                                    .id(e.getExhibitionsId())
                                    .title(e.getTitle())
                                    .artist(e.getArtist())
                                    .location(e.getGallery() != null ? e.getGallery() : e.getLocation())
                                    .thumbnailUrl(e.getPosterUrl())
                                    .build()
                    ).toList();

                    return RecommendedExhibitionsResponse.builder()
                            .keyword(category)
                            .exhibitions(items)
                            .build();
                })
                .toList();
    }
}
