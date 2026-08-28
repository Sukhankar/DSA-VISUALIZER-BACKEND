package com.codeloom.dsa.profile.service;

import com.codeloom.dsa.analytics.dto.LevelProgressDto;
import com.codeloom.dsa.analytics.dto.StreakStatusDto;
import com.codeloom.dsa.analytics.repository.UserAchievementRepository;
import com.codeloom.dsa.analytics.repository.UserBadgeRepository;
import com.codeloom.dsa.analytics.service.LevelService;
import com.codeloom.dsa.analytics.service.StreakService;
import com.codeloom.dsa.profile.dto.UserProfileDto;
import com.codeloom.dsa.profile.dto.UserProfileUpdateRequest;
import com.codeloom.dsa.profile.entity.UserProfile;
import com.codeloom.dsa.profile.repository.UserProfileRepository;
import com.codeloom.dsa.problem.repository.ProblemSubmissionRepository;
import com.codeloom.dsa.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserProfileService {

    private final UserProfileRepository profileRepository;
    private final LevelService levelService;
    private final StreakService streakService;
    private final UserAchievementRepository userAchievementRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final ProblemSubmissionRepository submissionRepository;

    public UserProfileService(
            UserProfileRepository profileRepository,
            LevelService levelService,
            StreakService streakService,
            UserAchievementRepository userAchievementRepository,
            UserBadgeRepository userBadgeRepository,
            ProblemSubmissionRepository submissionRepository
    ) {
        this.profileRepository = profileRepository;
        this.levelService = levelService;
        this.streakService = streakService;
        this.userAchievementRepository = userAchievementRepository;
        this.userBadgeRepository = userBadgeRepository;
        this.submissionRepository = submissionRepository;
    }

    public UserProfile getOrCreateProfile(User user) {
        return profileRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    UserProfile profile = new UserProfile(user);
                    return profileRepository.save(profile);
                });
    }

    @Transactional(readOnly = true)
    public UserProfileDto getUserProfileDto(User user) {
        UserProfile profile = getOrCreateProfile(user);
        LevelProgressDto levelProgress = levelService.getLevelProgress(profile.getTotalXp());
        StreakStatusDto streakStatus = streakService.getStreakStatus(user);

        long achievementsUnlocked = userAchievementRepository.countByUserId(user.getId());
        long badgesEarned = userBadgeRepository.countByUserId(user.getId());

        long totalSubmissions = submissionRepository.countByUserId(user.getId());
        long acceptedSubmissions = submissionRepository.countAcceptedSubmissionsByUser(user.getId());

        double acceptanceRate = totalSubmissions > 0
                ? Math.round(((double) acceptedSubmissions / totalSubmissions) * 100.0 * 10.0) / 10.0
                : 0.0;

        return new UserProfileDto(
                profile.getId(),
                user.getId(),
                user.getUsername(),
                profile.getDisplayName(),
                profile.getBio(),
                profile.getAvatarUrl(),
                profile.getCountry(),
                profile.getGithubUrl(),
                profile.getLinkedinUrl(),
                profile.getTotalXp(),
                levelProgress.currentLevel(),
                streakStatus.currentStreak(),
                streakStatus.longestStreak(),
                profile.getTotalProblemsSolved(),
                profile.getTotalAlgorithmsCompleted(),
                profile.getTotalPracticeSessions(),
                acceptanceRate,
                levelProgress,
                streakStatus,
                achievementsUnlocked,
                badgesEarned,
                profile.getCreatedAt() != null ? profile.getCreatedAt().toString() : user.getCreatedAt().toString()
        );
    }

    public UserProfileDto updateProfile(User user, UserProfileUpdateRequest request) {
        UserProfile profile = getOrCreateProfile(user);

        if (request.displayName() != null && !request.displayName().isBlank()) {
            profile.setDisplayName(request.displayName().trim());
        }
        if (request.bio() != null) {
            profile.setBio(request.bio().trim());
        }
        if (request.avatarUrl() != null) {
            profile.setAvatarUrl(request.avatarUrl().trim());
        }
        if (request.country() != null) {
            profile.setCountry(request.country().trim());
        }
        if (request.githubUrl() != null) {
            profile.setGithubUrl(request.githubUrl().trim());
        }
        if (request.linkedinUrl() != null) {
            profile.setLinkedinUrl(request.linkedinUrl().trim());
        }

        profileRepository.save(profile);
        return getUserProfileDto(user);
    }
}
