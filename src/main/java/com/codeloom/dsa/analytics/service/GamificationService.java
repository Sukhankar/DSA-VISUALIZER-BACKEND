package com.codeloom.dsa.analytics.service;

import com.codeloom.dsa.analytics.entity.*;
import com.codeloom.dsa.analytics.repository.*;
import com.codeloom.dsa.problem.repository.ProblemSubmissionRepository;
import com.codeloom.dsa.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

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

    public GamificationService(
            UserStreakRepository streakRepository,
            UserXpRepository xpRepository,
            XpLedgerRepository xpLedgerRepository,
            BadgeRepository badgeRepository,
            UserBadgeRepository userBadgeRepository,
            UserDailyActivityRepository dailyActivityRepository,
            ProblemSubmissionRepository problemSubmissionRepository
    ) {
        this.streakRepository = streakRepository;
        this.xpRepository = xpRepository;
        this.xpLedgerRepository = xpLedgerRepository;
        this.badgeRepository = badgeRepository;
        this.userBadgeRepository = userBadgeRepository;
        this.dailyActivityRepository = dailyActivityRepository;
        this.problemSubmissionRepository = problemSubmissionRepository;
    }

    public void processActivity(User user, String activityType, int xpAmount, String description) {
        updateStreak(user);
        awardXp(user, xpAmount, activityType, description);
        recordDailyActivity(user, activityType, xpAmount);
        evaluateBadges(user);
    }

    public UserStreak updateStreak(User user) {
        UserStreak streak = streakRepository.findById(user.getId())
                .orElseGet(() -> new UserStreak(user));

        LocalDate today = LocalDate.now();
        LocalDate lastDate = streak.getLastActivityDate();

        if (lastDate == null) {
            streak.setCurrentStreak(1);
            streak.setLastActivityDate(today);
        } else if (lastDate.equals(today)) {
            // Already logged activity today
        } else {
            long daysBetween = ChronoUnit.DAYS.between(lastDate, today);
            if (daysBetween == 1) {
                streak.setCurrentStreak(streak.getCurrentStreak() + 1);
                streak.setLastActivityDate(today);
            } else if (daysBetween == 2 && streak.getStreakFreezeCount() > 0) {
                // Streak freeze applied for missed day
                streak.setStreakFreezeCount(streak.getStreakFreezeCount() - 1);
                streak.setCurrentStreak(streak.getCurrentStreak() + 1);
                streak.setLastActivityDate(today);
            } else {
                // Reset streak
                streak.setCurrentStreak(1);
                streak.setLastActivityDate(today);
            }
        }

        // Award streak freeze if milestone reached (every 7 days)
        if (streak.getCurrentStreak() % 7 == 0 && streak.getStreakFreezeCount() < 2) {
            streak.setStreakFreezeCount(streak.getStreakFreezeCount() + 1);
        }

        return streakRepository.save(streak);
    }

    public UserXp awardXp(User user, int amount, String source, String description) {
        if (amount <= 0) {
            return xpRepository.findById(user.getId()).orElseGet(() -> xpRepository.save(new UserXp(user)));
        }

        UserXp userXp = xpRepository.findById(user.getId())
                .orElseGet(() -> new UserXp(user));

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

        // 1. FIRST_STEP badge
        tryUnlockBadge(user, "FIRST_STEP");

        // 2. CODE_ROOKIE badge
        if (solvedCount >= 1) {
            tryUnlockBadge(user, "CODE_ROOKIE");
        }

        // 3. STREAK_7 badge
        if (streak != null && streak.getCurrentStreak() >= 7) {
            tryUnlockBadge(user, "STREAK_7");
        }

        // 4. STREAK_30 badge
        if (streak != null && streak.getCurrentStreak() >= 30) {
            tryUnlockBadge(user, "STREAK_30");
        }

        // 5. ARRAY_MASTER badge
        if (solvedCount >= 3) {
            tryUnlockBadge(user, "ARRAY_MASTER");
        }

        // 6. CENTURION badge
        if (userXp != null && userXp.getTotalXp() >= 1000) {
            tryUnlockBadge(user, "CENTURION");
        }
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
}
