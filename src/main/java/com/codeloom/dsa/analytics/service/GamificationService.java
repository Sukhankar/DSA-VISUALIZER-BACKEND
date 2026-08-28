package com.codeloom.dsa.analytics.service;

import com.codeloom.dsa.analytics.entity.*;
import com.codeloom.dsa.analytics.repository.*;
import com.codeloom.dsa.profile.entity.UserProfile;
import com.codeloom.dsa.profile.repository.UserProfileRepository;
import com.codeloom.dsa.problem.repository.ProblemSubmissionRepository;
import com.codeloom.dsa.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;


@Service
@Transactional
public class GamificationService {

    private final UserStreakRepository streakRepository;
    private final UserXpRepository xpRepository;
    private final XpLedgerRepository xpLedgerRepository;
    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final UserDailyActivityRepository dailyActivityRepository;
    private final ProblemSubmissionRepository problemSubmissionRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserActivityRepository userActivityRepository;
    private final UserXpTransactionRepository xpTransactionRepository;
    private final StreakService streakService;
    private final LevelService levelService;
    private final AchievementEngine achievementEngine;
    private final BadgeEngine badgeEngine;

    public GamificationService(
            UserStreakRepository streakRepository,
            UserXpRepository xpRepository,
            XpLedgerRepository xpLedgerRepository,
            BadgeRepository badgeRepository,
            UserBadgeRepository userBadgeRepository,
            UserDailyActivityRepository dailyActivityRepository,
            ProblemSubmissionRepository problemSubmissionRepository,
            UserProfileRepository userProfileRepository,
            UserActivityRepository userActivityRepository,
            UserXpTransactionRepository xpTransactionRepository,
            StreakService streakService,
            LevelService levelService,
            AchievementEngine achievementEngine,
            BadgeEngine badgeEngine
    ) {
        this.streakRepository = streakRepository;
        this.xpRepository = xpRepository;
        this.xpLedgerRepository = xpLedgerRepository;
        this.badgeRepository = badgeRepository;
        this.userBadgeRepository = userBadgeRepository;
        this.dailyActivityRepository = dailyActivityRepository;
        this.problemSubmissionRepository = problemSubmissionRepository;
        this.userProfileRepository = userProfileRepository;
        this.userActivityRepository = userActivityRepository;
        this.xpTransactionRepository = xpTransactionRepository;
        this.streakService = streakService;
        this.levelService = levelService;
        this.achievementEngine = achievementEngine;
        this.badgeEngine = badgeEngine;
    }

    public void processActivity(User user, String activityType, int xpAmount, String description) {
        processActivityWithRef(user, activityType, null, null, xpAmount, description);
    }

    public void processActivityWithRef(User user, String rawActivityType, String refType, String refId, int xpAmount, String description) {
        UserProfile profile = userProfileRepository.findByUserId(user.getId())
                .orElseGet(() -> userProfileRepository.save(new UserProfile(user)));

        // 1. Update Streak
        UserStreak streak = streakService.recordDailyActivity(user);
        profile.setCurrentStreak(streak.getCurrentStreak());
        if (streak.getLongestStreak() > profile.getLongestStreak()) {
            profile.setLongestStreak(streak.getLongestStreak());
        }

        // 2. Increment counters in UserProfile based on event
        ActivityType actType = parseActivityType(rawActivityType);
        if (actType == ActivityType.PROBLEM_SOLVED) {
            profile.incrementProblemsSolved();
        } else if (actType == ActivityType.ALGORITHM_COMPLETED) {
            profile.incrementAlgorithmsCompleted();
        } else if (actType == ActivityType.PRACTICE_SESSION_COMPLETED) {
            profile.incrementPracticeSessions();
        }

        // 3. Award XP & Log XP Transaction (Idempotently)
        if (xpAmount > 0) {
            String reason = rawActivityType;
            if (refType != null && refId != null) {
                if (!xpTransactionRepository.existsByUserIdAndReasonAndReferenceTypeAndReferenceId(user.getId(), reason, refType, refId)) {
                    UserXpTransaction transaction = new UserXpTransaction(user, xpAmount, reason, refType, refId);
                    xpTransactionRepository.save(transaction);

                    profile.setTotalXp(profile.getTotalXp() + xpAmount);
                    awardLegacyXp(user, xpAmount, reason, description);
                }
            } else {
                profile.setTotalXp(profile.getTotalXp() + xpAmount);
                awardLegacyXp(user, xpAmount, reason, description);
            }
        }

        // 4. Update Level
        int newLevel = levelService.calculateLevel(profile.getTotalXp());
        if (newLevel > profile.getCurrentLevel()) {
            profile.setCurrentLevel(newLevel);
            UserActivity levelActivity = new UserActivity(user, ActivityType.LEVEL_UP, "LEVEL", String.valueOf(newLevel), 0, "Reached Level " + newLevel);
            userActivityRepository.save(levelActivity);
        }

        // 5. Log Activity
        UserActivity activity = new UserActivity(user, actType, refType, refId, xpAmount, description);
        userActivityRepository.save(activity);
        recordDailyActivity(user, rawActivityType, xpAmount);

        // 6. Save Profile
        userProfileRepository.save(profile);

        // 7. Evaluate Achievements & Badges
        achievementEngine.evaluateAchievements(user, profile);
        badgeEngine.evaluateBadges(user, profile);
        evaluateBadges(user);
    }

    public UserStreak updateStreak(User user) {
        return streakService.recordDailyActivity(user);
    }

    public UserXp awardXp(User user, int amount, String source, String description) {
        if (amount <= 0) {
            return xpRepository.findById(user.getId()).orElseGet(() -> xpRepository.save(new UserXp(user)));
        }

        UserProfile profile = userProfileRepository.findByUserId(user.getId())
                .orElseGet(() -> userProfileRepository.save(new UserProfile(user)));

        profile.setTotalXp(profile.getTotalXp() + amount);
        int newLevel = levelService.calculateLevel(profile.getTotalXp());
        profile.setCurrentLevel(newLevel);
        userProfileRepository.save(profile);

        return awardLegacyXp(user, amount, source, description);
    }

    private UserXp awardLegacyXp(User user, int amount, String source, String description) {
        UserXp userXp = xpRepository.findById(user.getId()).orElseGet(() -> new UserXp(user));
        userXp.addXp(amount);
        xpRepository.save(userXp);

        XpLedger ledgerEntry = new XpLedger(user, amount, source, description);
        xpLedgerRepository.save(ledgerEntry);

        return userXp;
    }

    public void recordDailyActivity(User user, String activityType, int xpEarned) {
        LocalDate today = LocalDate.now();
        UserDailyActivity dailyActivity = dailyActivityRepository.findByUserIdAndActivityDate(user.getId(), today)
                .orElseGet(() -> new UserDailyActivity(user, today));

        if ("ALGORITHM_VISUALIZATION".equalsIgnoreCase(activityType)) {
            dailyActivity.incrementAlgorithmsViewed();
        } else if ("PROBLEM_SOLVED".equalsIgnoreCase(activityType) || activityType.startsWith("PROBLEM_SOLVED_")) {
            dailyActivity.incrementProblemsSolved();
        }

        dailyActivity.addXpEarned(xpEarned);
        dailyActivityRepository.save(dailyActivity);
    }

    public void evaluateBadges(User user) {
        UserStreak streak = streakRepository.findById(user.getId()).orElse(null);
        UserXp userXp = xpRepository.findById(user.getId()).orElse(null);
        long solvedCount = problemSubmissionRepository.countAcceptedSubmissionsByUser(user.getId());

        tryUnlockBadge(user, "FIRST_STEP");
        if (solvedCount >= 1) tryUnlockBadge(user, "CODE_ROOKIE");
        if (streak != null && streak.getCurrentStreak() >= 7) tryUnlockBadge(user, "STREAK_7");
        if (streak != null && streak.getCurrentStreak() >= 30) tryUnlockBadge(user, "STREAK_30");
        if (solvedCount >= 3) tryUnlockBadge(user, "ARRAY_MASTER");
        if (userXp != null && userXp.getTotalXp() >= 1000) tryUnlockBadge(user, "CENTURION");
    }

    private void tryUnlockBadge(User user, String badgeCode) {
        if (!userBadgeRepository.existsByUserIdAndBadgeCode(user.getId(), badgeCode)) {
            badgeRepository.findByCode(badgeCode).ifPresent(badge -> {
                UserBadge userBadge = new UserBadge(user, badge);
                userBadgeRepository.save(userBadge);
                if (badge.getXpReward() > 0) {
                    awardXp(user, badge.getXpReward(), "BADGE_UNLOCK", "Unlocked badge: " + badge.getName());
                }
            });
        }
    }

    private ActivityType parseActivityType(String type) {
        if (type == null) return ActivityType.PROBLEM_SOLVED;
        try {
            return ActivityType.valueOf(type.toUpperCase());
        } catch (Exception e) {
            if (type.startsWith("PROBLEM_SOLVED")) return ActivityType.PROBLEM_SOLVED;
            if (type.startsWith("ALGORITHM")) return ActivityType.ALGORITHM_COMPLETED;
            if (type.startsWith("PRACTICE")) return ActivityType.PRACTICE_SESSION_COMPLETED;
            if (type.startsWith("DAILY")) return ActivityType.DAILY_CHALLENGE_COMPLETED;
            return ActivityType.PROBLEM_SOLVED;
        }
    }
}
