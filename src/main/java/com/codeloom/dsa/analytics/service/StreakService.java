package com.codeloom.dsa.analytics.service;

import com.codeloom.dsa.analytics.dto.StreakStatusDto;
import com.codeloom.dsa.analytics.entity.UserStreak;
import com.codeloom.dsa.analytics.repository.UserStreakRepository;
import com.codeloom.dsa.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@Transactional
public class StreakService {

    private final UserStreakRepository streakRepository;

    public StreakService(UserStreakRepository streakRepository) {
        this.streakRepository = streakRepository;
    }

    public UserStreak recordDailyActivity(User user) {
        UserStreak streak = streakRepository.findById(user.getId())
                .orElseGet(() -> new UserStreak(user));

        LocalDate today = LocalDate.now();
        LocalDate lastDate = streak.getLastActivityDate();

        if (lastDate == null) {
            streak.setCurrentStreak(1);
            streak.setLastActivityDate(today);
        } else if (lastDate.equals(today)) {
            // Activity already recorded for today
        } else {
            long daysBetween = ChronoUnit.DAYS.between(lastDate, today);
            if (daysBetween == 1) {
                streak.setCurrentStreak(streak.getCurrentStreak() + 1);
                streak.setLastActivityDate(today);
            } else if (daysBetween == 2 && streak.getStreakFreezeCount() > 0) {
                // Apply streak freeze for 1 missed day
                streak.setStreakFreezeCount(streak.getStreakFreezeCount() - 1);
                streak.setCurrentStreak(streak.getCurrentStreak() + 1);
                streak.setLastActivityDate(today);
            } else {
                // Reset streak
                streak.setCurrentStreak(1);
                streak.setLastActivityDate(today);
            }
        }

        if (streak.getCurrentStreak() > streak.getLongestStreak()) {
            streak.setLongestStreak(streak.getCurrentStreak());
        }

        // Award streak freeze if milestone reached (every 7 days, max 2)
        if (streak.getCurrentStreak() % 7 == 0 && streak.getStreakFreezeCount() < 2) {
            streak.setStreakFreezeCount(streak.getStreakFreezeCount() + 1);
        }

        return streakRepository.save(streak);
    }

    @Transactional(readOnly = true)
    public StreakStatusDto getStreakStatus(User user) {
        UserStreak streak = streakRepository.findById(user.getId())
                .orElseGet(() -> new UserStreak(user));

        LocalDate today = LocalDate.now();
        boolean isActiveToday = streak.getLastActivityDate() != null && streak.getLastActivityDate().equals(today);

        int current = streak.getCurrentStreak();

        // Calculate days until next milestone (3, 7, 14, 30, 100)
        int nextMilestone = 3;
        if (current >= 3) nextMilestone = 7;
        if (current >= 7) nextMilestone = 14;
        if (current >= 14) nextMilestone = 30;
        if (current >= 30) nextMilestone = 100;
        if (current >= 100) nextMilestone = current + 50;

        int daysRemaining = Math.max(0, nextMilestone - current);

        return new StreakStatusDto(
                current,
                streak.getLongestStreak(),
                streak.getLastActivityDate() != null ? streak.getLastActivityDate().toString() : null,
                isActiveToday,
                streak.getStreakFreezeCount(),
                nextMilestone,
                daysRemaining
        );
    }
}
