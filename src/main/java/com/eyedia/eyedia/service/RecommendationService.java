package com.eyedia.eyedia.service;

import com.eyedia.eyedia.dto.RecommendationDTO;
import com.eyedia.eyedia.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendationService {
    private final UserRepository userRepository;

    public RecommendationDTO getMyKeywords(Long usersId) {
        var user = userRepository.getUserByUsersId(usersId);

        return RecommendationDTO.builder()
                .keywords(user.getSelectedKeywords().stream().toList())
                .build();
    }
}
