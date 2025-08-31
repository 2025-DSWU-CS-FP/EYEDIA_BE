package com.eyedia.eyedia.service;

import com.eyedia.eyedia.repository.BadgeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BadgeCommandService {
    private final BadgeRepository badgeRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void acknowledgeNewBadges(List<Long> badgeIds) {
        if (badgeIds == null || badgeIds.isEmpty()) return;
        badgeRepository.markNotNewByIds(badgeIds);
    }
}

