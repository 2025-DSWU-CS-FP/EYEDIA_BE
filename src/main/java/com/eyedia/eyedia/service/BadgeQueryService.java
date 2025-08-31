package com.eyedia.eyedia.service;

import com.eyedia.eyedia.domain.badge.Badge;
import com.eyedia.eyedia.domain.enums.badge.ProgressStatus;
import com.eyedia.eyedia.dto.BadgeDTO;
import com.eyedia.eyedia.repository.BadgeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BadgeQueryService {

    private final BadgeRepository badgeRepository;
    private final BadgeCommandService badgeCommandService;

    public BadgeDTO.BadgeSummaryDto getSummary(Long userId, ProgressStatus statusFilter) {
        var all = badgeRepository.findAllByUsersId(userId);

        // 빈 응답 -> 빈 뱃지
        if (all.isEmpty()) {
            return BadgeDTO.BadgeSummaryDto.builder()
                    .total(0)
                    .acquired(0)
                    .nextTarget(null)
                    .badges(List.of())
                    .build();
        }

        //  기본 - 전체
        var stream = all.stream();

        if (statusFilter != null) {
            stream = stream.filter(b -> b.getStatus().equals(statusFilter) );
        }
        var dtos = stream.map( badge -> {
            var dto = this.toBadgeDTO(badge);
            return dto;
        }
        ).toList();

        int acquiredCount = (int) dtos.stream()
                // 상태로 판단
                .filter(d -> "ACHIEVED".equals(d.getStatus()))
                .count();
        // 다음 미션: 첫번째에 있는 것
        // Todo: 예외처리
        var nextTarget = dtos.stream()
                // 상태로 판단
                .filter(d -> "IN_PROGRESS".equals(d.getStatus()))
                .findFirst().orElse(null);

        int total = dtos.size();

        // 방금 내려준 것 중 "새로 획득된(isNew=true) + ACHIEVED"만 ACK (한 번에)
        var idsToAck = all.stream()
                .filter(b -> b.isNewBadge() && b.getStatus() == ProgressStatus.ACHIEVED)
                .map(Badge::getId)
                .toList();
        // readOnly 트랜잭션 안이지만, 내부에서 REQUIRES_NEW로 실제 업데이트 수행
        badgeCommandService.acknowledgeNewBadges(idsToAck);


        return BadgeDTO.BadgeSummaryDto.builder()
                .total(total)
                .acquired(acquiredCount)
                .nextTarget(nextTarget)
                .badges(dtos)
                .build();

    }

    private BadgeDTO.BadgeCardDto toBadgeDTO(Badge b){
        return BadgeDTO.BadgeCardDto.builder()
                .code(b.getCode())
                .title(b.getTitle())
                .description(b.getDescription())
                .status(b.getStatus().toString())
                .goalValue(b.getGoalValue())
                .currentValue(b.getCurrentValue())
                .newBadge(b.isNewBadge())
                .build();
    }

}