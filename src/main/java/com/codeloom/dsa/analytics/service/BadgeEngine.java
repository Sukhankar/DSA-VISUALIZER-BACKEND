package com.codeloom.dsa.analytics.service;

import com.codeloom.dsa.analytics.entity.*;
import com.codeloom.dsa.analytics.repository.*;
import com.codeloom.dsa.profile.entity.UserProfile;
import com.codeloom.dsa.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class BadgeEngine {

    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final UserActivityRepository userActivityRepository;

    public BadgeEngine(
            BadgeRepository badgeRepository,
            UserBadgeRepository userBadgeRepository,
            UserActivityRepository userActivityRepository
    ) {
        this.badgeRepository = badgeRepository;
        this.userBadgeRepository = userBadgeRepository;
        this.userActivityRepository = userActivityRepository;
    }

    public List<Badge> evaluateBadges(User user, UserProfile profile) {
        List<Badge> newlyUnlocked = new ArrayList<>();
        List<Badge> allBadges = badgeRepository.findAll();

        for (Badge badge : allBadges) {
            if (userBadgeRepository.existsByUserIdAndBadgeId(user.getId(), badge.getId())) {
                continue; // Already earned
            }

            boolean unlocked = checkBadgeCondition(profile, badge);
            if (unlocked) {
                UserBadge userBadge = new UserBadge(user, badge);
                userBadgeRepository.save(userBadge);

                // Log Activity
                UserActivity activity = new UserActivity(
                        user,
                        ActivityType.BADGE_UNLOCKED,
                        "BADGE",
                        badge.getCode(),
                        badge.getXpReward(),
                        "Earned badge: " + badge.getName()
                );
                userActivityRepository.save(activity);

                newlyUnlocked.add(badge);
            }
        }

        return newlyUnlocked;
    }

    private boolean checkBadgeCondition(UserProfile profile, Badge badge) {
        String type = badge.getUnlockType() != null ? badge.getUnlockType() : badge.getCode();
        int reqVal = badge.getUnlockValue() != null ? badge.getUnlockValue() : 1;

        if ("FIRST_STEP".equalsIgnoreCase(badge.getCode())) {
            return profile.getTotalAlgorithmsCompleted() >= 1 || profile.getTotalProblemsSolved() >= 1;
        }

        return switch (type) {
            case "CURRENT_STREAK" -> profile.getCurrentStreak() >= reqVal;
            case "PROBLEMS_SOLVED" -> profile.getTotalProblemsSolved() >= reqVal;
            case "PRACTICE_SESSIONS" -> profile.getTotalPracticeSessions() >= reqVal;
            case "LEVEL" -> profile.getCurrentLevel() >= reqVal;
            case "ALGORITHMS_COMPLETED" -> profile.getTotalAlgorithmsCompleted() >= reqVal;
            default -> false;
        };
    }
}
