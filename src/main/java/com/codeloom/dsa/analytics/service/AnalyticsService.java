package com.codeloom.dsa.analytics.service;

import com.codeloom.dsa.algorithm.entity.AlgorithmCategory;
import com.codeloom.dsa.algorithm.repository.AlgorithmCategoryRepository;
import com.codeloom.dsa.algorithm.repository.AlgorithmRepository;
import com.codeloom.dsa.analytics.dto.*;
import com.codeloom.dsa.analytics.entity.*;
import com.codeloom.dsa.analytics.repository.*;
import com.codeloom.dsa.common.exception.ResourceNotFoundException;
import com.codeloom.dsa.problem.entity.SubmissionVerdict;
import com.codeloom.dsa.problem.repository.ProblemSubmissionRepository;
import com.codeloom.dsa.progress.repository.UserAlgorithmProgressRepository;
import com.codeloom.dsa.user.entity.User;
import com.codeloom.dsa.user.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AnalyticsService {

    private final UserRepository userRepository;
    private final UserStreakRepository streakRepository;
    private final UserXpRepository xpRepository;
    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final UserDailyActivityRepository dailyActivityRepository;
    private final AlgorithmCategoryRepository categoryRepository;
    private final UserAlgorithmProgressRepository progressRepository;
    private final ProblemSubmissionRepository submissionRepository;

    public AnalyticsService(
            UserRepository userRepository,
            UserStreakRepository streakRepository,
            UserXpRepository xpRepository,
            BadgeRepository badgeRepository,
            UserBadgeRepository userBadgeRepository,
            UserDailyActivityRepository dailyActivityRepository,
            AlgorithmCategoryRepository categoryRepository,
            UserAlgorithmProgressRepository progressRepository,
            ProblemSubmissionRepository submissionRepository
    ) {
        this.userRepository = userRepository;
        this.streakRepository = streakRepository;
        this.xpRepository = xpRepository;
        this.badgeRepository = badgeRepository;
        this.userBadgeRepository = userBadgeRepository;
        this.dailyActivityRepository = dailyActivityRepository;
        this.categoryRepository = categoryRepository;
        this.progressRepository = progressRepository;
        this.submissionRepository = submissionRepository;
    }

    public AnalyticsOverviewResponse getAnalyticsOverview(String email) {
        User user = getUserByEmail(email);

        UserStreak streak = streakRepository.findById(user.getId())
                .orElseGet(() -> new UserStreak(user));

        UserXp xp = xpRepository.findById(user.getId())
                .orElseGet(() -> new UserXp(user));

        UserStreakDto streakDto = mapStreakDto(streak);
        UserXpDto xpDto = mapXpDto(xp);

        List<Badge> allBadges = badgeRepository.findAll();
        List<UserBadge> userBadges = userBadgeRepository.findByUserId(user.getId());
        Set<UUID> unlockedBadgeIds = userBadges.stream()
                .map(ub -> ub.getBadge().getId())
                .collect(Collectors.toSet());

        Map<UUID, UserBadge> userBadgeMap = userBadges.stream()
                .collect(Collectors.toMap(ub -> ub.getBadge().getId(), ub -> ub));

        List<BadgeDto> recentBadges = userBadges.stream()
                .sorted(Comparator.comparing(UserBadge::getUnlockedAt).reversed())
                .limit(5)
                .map(ub -> new BadgeDto(
                        ub.getBadge().getCode(),
                        ub.getBadge().getName(),
                        ub.getBadge().getDescription(),
                        ub.getBadge().getIconName(),
                        ub.getBadge().getCategory(),
                        ub.getBadge().getXpReward(),
                        true,
                        ub.getUnlockedAt()
                ))
                .toList();

        List<TopicSkillDto> skills = getTopicSkills(user);

        return new AnalyticsOverviewResponse(
                streakDto,
                xpDto,
                unlockedBadgeIds.size(),
                allBadges.size(),
                recentBadges,
                skills
        );
    }

    public List<DailyActivityDto> getActivityHeatmap(String email) {
        User user = getUserByEmail(email);
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(365);

        List<UserDailyActivity> activities = dailyActivityRepository
                .findByUserIdAndActivityDateBetweenOrderByActivityDateAsc(user.getId(), startDate, endDate);

        return activities.stream()
                .map(a -> new DailyActivityDto(
                        a.getActivityDate(),
                        a.getAlgorithmsViewedCount() + a.getProblemsSolvedCount(),
                        a.getXpEarned()
                ))
                .toList();
    }

    public List<BadgeDto> getBadges(String email) {
        User user = getUserByEmail(email);
        List<Badge> allBadges = badgeRepository.findAll();
        List<UserBadge> userBadges = userBadgeRepository.findByUserId(user.getId());

        Map<UUID, UserBadge> userBadgeMap = userBadges.stream()
                .collect(Collectors.toMap(ub -> ub.getBadge().getId(), ub -> ub));

        return allBadges.stream()
                .map(b -> {
                    UserBadge ub = userBadgeMap.get(b.getId());
                    return new BadgeDto(
                            b.getCode(),
                            b.getName(),
                            b.getDescription(),
                            b.getIconName(),
                            b.getCategory(),
                            b.getXpReward(),
                            ub != null,
                            ub != null ? ub.getUnlockedAt() : null
                    );
                })
                .toList();
    }

    public List<LeaderboardUserDto> getLeaderboard(int limit) {
        List<UserXp> topXpList = xpRepository.findTopLeaderboard(PageRequest.of(0, Math.min(limit, 50)));

        List<LeaderboardUserDto> leaderboard = new ArrayList<>();
        int rank = 1;
        for (UserXp xp : topXpList) {
            User user = xp.getUser();
            long solved = submissionRepository.countDistinctSolvedProblemsByUserId(user.getId(), SubmissionVerdict.ACCEPTED);
            leaderboard.add(new LeaderboardUserDto(
                    rank++,
                    user.getUsername(),
                    xp.getCurrentLevel(),
                    xp.getTotalXp(),
                    solved
            ));
        }

        return leaderboard;
    }

    public List<TopicSkillDto> getTopicSkills(String email) {
        User user = getUserByEmail(email);
        return getTopicSkills(user);
    }

    private List<TopicSkillDto> getTopicSkills(User user) {
        List<AlgorithmCategory> categories = categoryRepository.findAll();
        List<TopicSkillDto> topicSkills = new ArrayList<>();

        for (AlgorithmCategory cat : categories) {
            // Dynamic skill score derived from user activity
            long completedAlgos = progressRepository.countCompletedByUserIdAndCategory(user.getId(), cat.getId());
            long solvedProblems = submissionRepository.countSolvedByUserIdAndCategory(user.getId(), cat.getId());

            // Score formula: (completedAlgos * 25) + (solvedProblems * 30), max 100
            int rawScore = (int) ((completedAlgos * 25) + (solvedProblems * 30));
            int score = Math.min(100, Math.max(15, rawScore)); // Base score minimum 15 for visualization radar

            topicSkills.add(new TopicSkillDto(
                    cat.getName(),
                    cat.getSlug(),
                    score
            ));
        }

        return topicSkills;
    }

    public UserStreakDto getUserStreakDto(UUID userId) {
        UserStreak streak = streakRepository.findById(userId)
                .orElseGet(() -> {
                    User u = userRepository.findById(userId).orElse(null);
                    return new UserStreak(u);
                });
        return mapStreakDto(streak);
    }

    public UserXpDto getUserXpDto(UUID userId) {
        UserXp xp = xpRepository.findById(userId)
                .orElseGet(() -> {
                    User u = userRepository.findById(userId).orElse(null);
                    return new UserXp(u);
                });
        return mapXpDto(xp);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }


    private UserStreakDto mapStreakDto(UserStreak streak) {
        return new UserStreakDto(
                streak.getCurrentStreak(),
                streak.getLongestStreak(),
                streak.getLastActivityDate(),
                streak.getStreakFreezeCount()
        );
    }

    private UserXpDto mapXpDto(UserXp xp) {
        int level = xp.getCurrentLevel();
        int currentLevelXp = UserXp.getXpForLevel(level);
        int nextLevelXp = UserXp.getXpForLevel(level + 1);

        int xpInLevel = Math.max(0, xp.getTotalXp() - currentLevelXp);
        int xpNeeded = Math.max(1, nextLevelXp - currentLevelXp);

        double progress = Math.min(100.0, Math.round(((double) xpInLevel / xpNeeded * 100.0) * 10.0) / 10.0);

        return new UserXpDto(
                xp.getTotalXp(),
                level,
                currentLevelXp,
                nextLevelXp,
                progress
        );
    }
}
