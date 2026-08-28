package com.codeloom.dsa.analytics.service;

import com.codeloom.dsa.analytics.entity.*;
import com.codeloom.dsa.analytics.repository.*;
import com.codeloom.dsa.profile.entity.UserProfile;
import com.codeloom.dsa.profile.repository.UserProfileRepository;
import com.codeloom.dsa.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class AchievementEngine {

    private final AchievementRepository achievementRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserActivityRepository userActivityRepository;
    private final UserXpTransactionRepository xpTransactionRepository;
    private final UserXpRepository userXpRepository;
    private final XpLedgerRepository xpLedgerRepository;

    public AchievementEngine(
            AchievementRepository achievementRepository,
            UserAchievementRepository userAchievementRepository,
            UserProfileRepository userProfileRepository,
            UserActivityRepository userActivityRepository,
            UserXpTransactionRepository xpTransactionRepository,
            UserXpRepository userXpRepository,
            XpLedgerRepository xpLedgerRepository
    ) {
        this.achievementRepository = achievementRepository;
        this.userAchievementRepository = userAchievementRepository;
        this.userProfileRepository = userProfileRepository;
        this.userActivityRepository = userActivityRepository;
        this.xpTransactionRepository = xpTransactionRepository;
        this.userXpRepository = userXpRepository;
        this.xpLedgerRepository = xpLedgerRepository;
    }

    public List<Achievement> evaluateAchievements(User user, UserProfile profile) {
        List<Achievement> unlockedAchievements = new ArrayList<>();
        List<Achievement> allAchievements = achievementRepository.findAll();

        for (Achievement achievement : allAchievements) {
            if (userAchievementRepository.existsByUserIdAndAchievementId(user.getId(), achievement.getId())) {
                continue; // Already unlocked
            }

            boolean requirementMet = checkRequirement(profile, achievement);
            if (requirementMet) {
                // Unlock achievement
                UserAchievement userAchievement = new UserAchievement(user, achievement);
                userAchievementRepository.save(userAchievement);

                // Award XP for achievement if reward > 0
                if (achievement.getXpReward() > 0) {
                    awardAchievementXp(user, profile, achievement);
                }

                // Log Activity
                UserActivity activity = new UserActivity(
                        user,
                        ActivityType.ACHIEVEMENT_UNLOCKED,
                        "ACHIEVEMENT",
                        achievement.getCode(),
                        achievement.getXpReward(),
                        "Unlocked achievement: " + achievement.getName()
                );
                userActivityRepository.save(activity);

                unlockedAchievements.add(achievement);
            }
        }

        return unlockedAchievements;
    }

    private boolean checkRequirement(UserProfile profile, Achievement achievement) {
        int currentVal = switch (achievement.getRequirementType()) {
            case PROBLEMS_SOLVED -> profile.getTotalProblemsSolved();
            case ALGORITHMS_COMPLETED -> profile.getTotalAlgorithmsCompleted();
            case CURRENT_STREAK -> profile.getCurrentStreak();
            case LONGEST_STREAK -> profile.getLongestStreak();
            case PRACTICE_SESSIONS -> profile.getTotalPracticeSessions();
            case TOTAL_XP -> profile.getTotalXp();
            case LEVEL -> profile.getCurrentLevel();
            case DAILY_CHALLENGES -> (int) userActivityRepository.countByUserIdAndActivityType(profile.getUser().getId(), ActivityType.DAILY_CHALLENGE_COMPLETED);
            case VISUALIZATIONS -> (int) userActivityRepository.countByUserIdAndActivityType(profile.getUser().getId(), ActivityType.ALGORITHM_VISUALIZATION);

        };

        return currentVal >= achievement.getRequirementValue();
    }

    private void awardAchievementXp(User user, UserProfile profile, Achievement achievement) {
        String reason = "ACHIEVEMENT_" + achievement.getCode();
        if (xpTransactionRepository.existsByUserIdAndReasonAndReferenceTypeAndReferenceId(
                user.getId(), reason, "ACHIEVEMENT", achievement.getCode())) {
            return;
        }

        UserXpTransaction transaction = new UserXpTransaction(
                user,
                achievement.getXpReward(),
                reason,
                "ACHIEVEMENT",
                achievement.getCode()
        );
        xpTransactionRepository.save(transaction);

        // Update profile & legacy UserXp
        profile.setTotalXp(profile.getTotalXp() + achievement.getXpReward());
        userProfileRepository.save(profile);

        UserXp userXp = userXpRepository.findById(user.getId()).orElseGet(() -> new UserXp(user));
        userXp.addXp(achievement.getXpReward());
        userXpRepository.save(userXp);

        XpLedger ledger = new XpLedger(user, achievement.getXpReward(), "ACHIEVEMENT", "Unlocked achievement: " + achievement.getName());
        xpLedgerRepository.save(ledger);
    }
}
