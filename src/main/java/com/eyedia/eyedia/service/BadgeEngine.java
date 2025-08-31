package com.eyedia.eyedia.service;

import com.eyedia.eyedia.domain.User;
import com.eyedia.eyedia.domain.badge.Badge;
import com.eyedia.eyedia.domain.badge.BadgeDefinition;
import com.eyedia.eyedia.domain.badge.UserBadgeAward;
import com.eyedia.eyedia.domain.enums.badge.ProgressStatus;
import com.eyedia.eyedia.dto.BadgeEventDTO;
import com.eyedia.eyedia.repository.BadgeDefinitionRepository;
import com.eyedia.eyedia.repository.BadgeRepository;
import com.eyedia.eyedia.repository.UserBadgeAwardRepository;
import com.eyedia.eyedia.service.eval.BadgeEvaluator;
import com.eyedia.eyedia.service.eval.BadgeEvaluatorRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class BadgeEngine {

    private final BadgeDefinitionRepository defRepo;
    private final BadgeRepository progressRepo;
    private final UserBadgeAwardRepository awardRepo;
    private final com.eyedia.eyedia.repository.UserRepository userRepo;
    private final BadgeEvaluatorRegistry registry;
    private final ObjectMapper om;

    public void process(BadgeEventDTO event) {
        if (event.getOccurredAt() == null) event.setOccurredAt(LocalDateTime.now());
        if (event.getEventUid() == null || event.getEventUid().isBlank()) {
            event.setEventUid(UUID.randomUUID().toString());
        }

        List<BadgeDefinition> defs = defRepo.findAllByEnabledTrueOrderBySortOrderAsc();
        for (BadgeDefinition def : defs) {
            if (def.getEventType() != event.getType()) continue;
            if (def.getStartAt()!=null && event.getOccurredAt().isBefore(def.getStartAt())) continue;
            if (def.getEndAt()!=null && event.getOccurredAt().isAfter(def.getEndAt())) continue;


            Badge p = progressRepo.findByUser_UsersIdAndCode(event.getUserId(), def.getCode())
                    .orElseGet(() -> {

                        User user = userRepo.getUserByUsersId(event.getUserId());
                        System.out.println("user: " + user.getUsersId());
                        return progressRepo.save(Badge.builder()
                                .user(user)
                                .code(def.getCode())
                                .title(def.getTitle())
                                .description(def.getDescriptionKey())
                                .status(ProgressStatus.LOCKED)
                                .currentValue(0)
                                .goalValue(def.getGoalValue())
                                .build());
                    });

            Map<String,Object> params = parse(def.getParamsJson());
            BadgeEvaluator eval = registry.get(def.getEvaluatorType());
            if (eval == null) continue;

            eval.apply(p, params, event);

            // 상태 갱신
            if (p.getCurrentValue() >= p.getGoalValue()) {
                if (p.getStatus() != ProgressStatus.ACHIEVED) {
                    p.setStatus(ProgressStatus.ACHIEVED);
                    p.setNewBadge(true);
                    if (p.getAchievedAt() == null) p.setAchievedAt(LocalDateTime.now());
                    if (!awardRepo.existsByUser_UsersIdAndCode(Long.valueOf(p.getUser().getUsersId()), p.getCode())) {
                        awardRepo.save(UserBadgeAward.builder()
                                .user(p.getUser())
                                .code(p.getCode())
                                .achievedAt(LocalDateTime.now())
                                .achievedReason("Goal reached: " + p.getCode())
                                .build());
                        // TODO: 웹소켓/알림 푸시
                    }
                }
            } else {
                p.setStatus(p.getCurrentValue()>0 ? ProgressStatus.IN_PROGRESS : ProgressStatus.LOCKED);
            }

        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parse(String json) {
        if (json == null || json.isBlank()) return Collections.emptyMap();
        try { return om.readValue(json, Map.class); }
        catch (Exception e) { return Collections.emptyMap(); }
    }
}