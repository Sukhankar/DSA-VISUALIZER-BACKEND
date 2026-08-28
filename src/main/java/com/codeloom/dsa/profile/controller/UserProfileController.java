package com.codeloom.dsa.profile.controller;

import com.codeloom.dsa.analytics.dto.*;
import com.codeloom.dsa.analytics.entity.Achievement;
import com.codeloom.dsa.analytics.entity.Badge;
import com.codeloom.dsa.analytics.entity.UserAchievement;
import com.codeloom.dsa.analytics.entity.UserBadge;
import com.codeloom.dsa.analytics.repository.AchievementRepository;
import com.codeloom.dsa.analytics.repository.BadgeRepository;
import com.codeloom.dsa.analytics.repository.UserAchievementRepository;
import com.codeloom.dsa.analytics.repository.UserActivityRepository;
import com.codeloom.dsa.analytics.repository.UserBadgeRepository;
import com.codeloom.dsa.analytics.service.LevelService;
import com.codeloom.dsa.analytics.service.StreakService;
import com.codeloom.dsa.profile.dto.UserProfileDto;
import com.codeloom.dsa.profile.dto.UserProfileUpdateRequest;
import com.codeloom.dsa.profile.entity.UserProfile;
import com.codeloom.dsa.profile.service.UserProfileService;
import com.codeloom.dsa.user.entity.User;
import com.codeloom.dsa.user.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users/me")
public class UserProfileController {

    private final UserProfileService userProfileService;
    private final UserRepository userRepository;
    private final UserActivityRepository userActivityRepository;
    private final AchievementRepository achievementRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final LevelService levelService;
    private final StreakService streakService;

    public UserProfileController(
            UserProfileService userProfileService,
            UserRepository userRepository,
            UserActivityRepository userActivityRepository,
            AchievementRepository achievementRepository,
            UserAchievementRepository userAchievementRepository,
            BadgeRepository badgeRepository,
            UserBadgeRepository userBadgeRepository,
            LevelService levelService,
            StreakService streakService
    ) {
        this.userProfileService = userProfileService;
        this.userRepository = userRepository;
        this.userActivityRepository = userActivityRepository;
        this.achievementRepository = achievementRepository;
        this.userAchievementRepository = userAchievementRepository;
        this.badgeRepository = badgeRepository;
        this.userBadgeRepository = userBadgeRepository;
        this.levelService = levelService;
        this.streakService = streakService;
    }

    private User getAuthenticatedUser(UserDetails userDetails) {
        if (userDetails == null) {
            throw new RuntimeException("Unauthorized user access");
        }
        return userRepository.findByUsername(userDetails.getUsername())
                .or(() -> userRepository.findByEmail(userDetails.getUsername()))
                .orElseThrow(() -> new RuntimeException("User not found: " + userDetails.getUsername()));
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        User user = getAuthenticatedUser(userDetails);
        return ResponseEntity.ok(userProfileService.getUserProfileDto(user));
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileDto> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UserProfileUpdateRequest request
    ) {
        User user = getAuthenticatedUser(userDetails);
        return ResponseEntity.ok(userProfileService.updateProfile(user, request));
    }

    @GetMapping("/profile/activity")
    public ResponseEntity<Page<UserActivityDto>> getActivity(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size
    ) {
        User user = getAuthenticatedUser(userDetails);
        Page<UserActivityDto> activities = userActivityRepository
                .findByUserIdOrderByCreatedAtDesc(user.getId(), PageRequest.of(page, size))
                .map(a -> new UserActivityDto(
                        a.getId(),
                        a.getActivityType().name(),
                        a.getReferenceType(),
                        a.getReferenceId(),
                        a.getXpEarned(),
                        a.getMetadata(),
                        a.getCreatedAt().toString()
                ));
        return ResponseEntity.ok(activities);
    }

    @GetMapping("/achievements")
    public ResponseEntity<List<AchievementItemDto>> getAchievements(@AuthenticationPrincipal UserDetails userDetails) {
        User user = getAuthenticatedUser(userDetails);
        UserProfile profile = userProfileService.getOrCreateProfile(user);

        List<Achievement> allAchievements = achievementRepository.findAll();
        List<UserAchievement> userAchievements = userAchievementRepository.findByUserIdOrderByUnlockedAtDesc(user.getId());
        Map<String, String> unlockedMap = userAchievements.stream()
                .collect(Collectors.toMap(ua -> ua.getAchievement().getCode(), ua -> ua.getUnlockedAt().toString(), (v1, v2) -> v1));

        List<AchievementItemDto> dtos = allAchievements.stream().map(a -> {
            boolean isUnlocked = unlockedMap.containsKey(a.getCode());
            String unlockedAt = unlockedMap.get(a.getCode());

            int currentVal = switch (a.getRequirementType()) {
                case PROBLEMS_SOLVED -> profile.getTotalProblemsSolved();
                case ALGORITHMS_COMPLETED -> profile.getTotalAlgorithmsCompleted();
                case CURRENT_STREAK -> profile.getCurrentStreak();
                case LONGEST_STREAK -> profile.getLongestStreak();
                case PRACTICE_SESSIONS -> profile.getTotalPracticeSessions();
                case TOTAL_XP -> profile.getTotalXp();
                case LEVEL -> profile.getCurrentLevel();
                case DAILY_CHALLENGES -> (int) userActivityRepository.countByUserIdAndActivityType(user.getId(), com.codeloom.dsa.analytics.entity.ActivityType.DAILY_CHALLENGE_COMPLETED);
                case VISUALIZATIONS -> (int) userActivityRepository.countByUserIdAndActivityType(user.getId(), com.codeloom.dsa.analytics.entity.ActivityType.ALGORITHM_VISUALIZATION);
            };


            int reqVal = Math.max(1, a.getRequirementValue());
            double percentage = isUnlocked ? 100.0 : Math.min(100.0, Math.round(((double) currentVal / reqVal) * 100.0 * 10.0) / 10.0);

            return new AchievementItemDto(
                    a.getId(),
                    a.getCode(),
                    a.getName(),
                    a.getDescription(),
                    a.getCategory().name(),
                    a.getIcon(),
                    a.getRarity().name(),
                    a.getXpReward(),
                    a.getRequirementType().name(),
                    a.getRequirementValue(),
                    isUnlocked,
                    unlockedAt,
                    currentVal,
                    percentage
            );
        }).toList();

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/badges")
    public ResponseEntity<List<BadgeItemDto>> getBadges(@AuthenticationPrincipal UserDetails userDetails) {
        User user = getAuthenticatedUser(userDetails);
        List<Badge> allBadges = badgeRepository.findAll();
        List<UserBadge> userBadges = userBadgeRepository.findByUserId(user.getId());
        Map<String, String> earnedMap = userBadges.stream()
                .collect(Collectors.toMap(ub -> ub.getBadge().getCode(), ub -> ub.getUnlockedAt().toString(), (v1, v2) -> v1));

        List<BadgeItemDto> dtos = allBadges.stream().map(b -> {
            boolean isEarned = earnedMap.containsKey(b.getCode());
            String earnedAt = earnedMap.get(b.getCode());
            return new BadgeItemDto(
                    b.getId(),
                    b.getCode(),
                    b.getName(),
                    b.getDescription(),
                    b.getIconName(),
                    b.getCategory(),
                    b.getRarity() != null ? b.getRarity().name() : "COMMON",
                    b.getXpReward(),
                    isEarned,
                    earnedAt
            );
        }).toList();

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/gamification")
    public ResponseEntity<GamificationSummaryDto> getGamificationSummary(@AuthenticationPrincipal UserDetails userDetails) {
        User user = getAuthenticatedUser(userDetails);
        UserProfile profile = userProfileService.getOrCreateProfile(user);

        LevelProgressDto levelProgress = levelService.getLevelProgress(profile.getTotalXp());
        StreakStatusDto streakStatus = streakService.getStreakStatus(user);

        long achievementsUnlocked = userAchievementRepository.countByUserId(user.getId());
        long totalAchievements = achievementRepository.count();

        long badgesEarned = userBadgeRepository.countByUserId(user.getId());
        long totalBadges = badgeRepository.count();

        List<UserActivityDto> recentActivity = userActivityRepository.findTop10ByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(a -> new UserActivityDto(
                        a.getId(),
                        a.getActivityType().name(),
                        a.getReferenceType(),
                        a.getReferenceId(),
                        a.getXpEarned(),
                        a.getMetadata(),
                        a.getCreatedAt().toString()
                ))
                .toList();

        return ResponseEntity.ok(new GamificationSummaryDto(
                levelProgress.currentLevel(),
                profile.getTotalXp(),
                levelProgress,
                streakStatus.currentStreak(),
                streakStatus.longestStreak(),
                profile.getTotalProblemsSolved(),
                profile.getTotalAlgorithmsCompleted(),
                profile.getTotalPracticeSessions(),
                achievementsUnlocked,
                totalAchievements,
                badgesEarned,
                totalBadges,
                recentActivity
        ));
    }

    @GetMapping("/streak")
    public ResponseEntity<StreakStatusDto> getStreak(@AuthenticationPrincipal UserDetails userDetails) {
        User user = getAuthenticatedUser(userDetails);
        return ResponseEntity.ok(streakService.getStreakStatus(user));
    }
}
