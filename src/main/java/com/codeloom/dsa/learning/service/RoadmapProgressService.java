package com.codeloom.dsa.learning.service;

import com.codeloom.dsa.analytics.service.GamificationService;
import com.codeloom.dsa.learning.dto.LearningPreferenceRequest;
import com.codeloom.dsa.learning.dto.LearningPreferenceResponse;
import com.codeloom.dsa.learning.dto.OnboardingAssessmentRequest;
import com.codeloom.dsa.learning.entity.ExperienceLevel;
import com.codeloom.dsa.learning.entity.PrimaryGoal;
import com.codeloom.dsa.learning.entity.UserLearningPreference;
import com.codeloom.dsa.learning.repository.UserLearningPreferenceRepository;
import com.codeloom.dsa.roadmap.dto.AssessmentResultDto;
import com.codeloom.dsa.roadmap.dto.RoadmapModuleDto;
import com.codeloom.dsa.roadmap.dto.UserRoadmapDto;
import com.codeloom.dsa.roadmap.entity.RoadmapModule;
import com.codeloom.dsa.roadmap.entity.RoadmapStatus;
import com.codeloom.dsa.roadmap.entity.UserRoadmapProgress;
import com.codeloom.dsa.roadmap.repository.RoadmapModuleRepository;
import com.codeloom.dsa.roadmap.repository.UserRoadmapProgressRepository;
import com.codeloom.dsa.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class RoadmapProgressService {

    private final RoadmapModuleRepository moduleRepository;
    private final UserRoadmapProgressRepository progressRepository;
    private final UserLearningPreferenceRepository preferenceRepository;
    private final GamificationService gamificationService;

    public RoadmapProgressService(
            RoadmapModuleRepository moduleRepository,
            UserRoadmapProgressRepository progressRepository,
            UserLearningPreferenceRepository preferenceRepository,
            GamificationService gamificationService) {
        this.moduleRepository = moduleRepository;
        this.progressRepository = progressRepository;
        this.preferenceRepository = preferenceRepository;
        this.gamificationService = gamificationService;
    }

    @Transactional(readOnly = true)
    public UserRoadmapDto getUserRoadmap(User user) {
        List<RoadmapModule> modules = moduleRepository.findAllByOrderByOrderIndexAsc();
        List<UserRoadmapProgress> userProgressList = user != null ? progressRepository.findByUserId(user.getId()) : List.of();

        Map<UUID, UserRoadmapProgress> progressMap = userProgressList.stream()
                .collect(Collectors.toMap(p -> p.getModule().getId(), p -> p, (v1, v2) -> v1));

        List<RoadmapModuleDto> moduleDtos = new ArrayList<>();
        int totalModules = modules.size();
        int completedCount = 0;
        int sumProgress = 0;

        RoadmapModuleDto currentModuleDto = null;

        for (RoadmapModule module : modules) {
            UserRoadmapProgress userProgress = progressMap.get(module.getId());
            RoadmapStatus status = determineModuleStatus(module, progressMap);
            int completionPercentage = userProgress != null ? userProgress.getCompletionPercentage() : 0;

            if (status == RoadmapStatus.COMPLETED) {
                completionPercentage = 100;
                completedCount++;
            }
            sumProgress += completionPercentage;

            RoadmapModuleDto dto = mapToModuleDto(module, status, completionPercentage);
            moduleDtos.add(dto);

            if (currentModuleDto == null && status == RoadmapStatus.IN_PROGRESS) {
                currentModuleDto = dto;
            }
        }

        int overallProgress = totalModules > 0 ? sumProgress / totalModules : 0;
        if (currentModuleDto == null && !moduleDtos.isEmpty()) {
            currentModuleDto = moduleDtos.get(0);
        }

        UserRoadmapDto response = new UserRoadmapDto();
        com.codeloom.dsa.learning.dto.LearningPathResponse pathInfo = new com.codeloom.dsa.learning.dto.LearningPathResponse();
        pathInfo.setSlug("dsa-beginner");
        pathInfo.setName("DSA Beginner Path");
        pathInfo.setDescription("Structured learning path from Arrays to DP");


        response.setPath(pathInfo);
        response.setOverallProgress(overallProgress);
        response.setCurrentModule(currentModuleDto);
        response.setModules(moduleDtos);


        return response;
    }

    public RoadmapModuleDto startModule(User user, String slug) {
        RoadmapModule module = moduleRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Roadmap module not found with slug: " + slug));

        UserRoadmapProgress progress = progressRepository.findByUserIdAndModuleId(user.getId(), module.getId())
                .orElseGet(() -> new UserRoadmapProgress(user, module, RoadmapStatus.IN_PROGRESS));

        if (progress.getStatus() == RoadmapStatus.LOCKED) {
            progress.setStatus(RoadmapStatus.IN_PROGRESS);
        }
        if (progress.getStartedAt() == null) {
            progress.setStartedAt(LocalDateTime.now());
        }
        progress.setLastActivityAt(LocalDateTime.now());
        progressRepository.save(progress);

        return mapToModuleDto(module, progress.getStatus(), progress.getCompletionPercentage());
    }

    public AssessmentResultDto processOnboardingAssessment(User user, OnboardingAssessmentRequest request) {
        UserLearningPreference pref = preferenceRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    UserLearningPreference newPref = new UserLearningPreference();
                    newPref.setUser(user);
                    return newPref;
                });

        pref.setExperienceLevel(request.getExperienceLevel());
        pref.setPreferredLanguage(request.getPreferredLanguage());
        pref.setDailyLearningMinutes(request.getDailyLearningMinutes());
        pref.setPrimaryGoal(request.getPrimaryGoal());
        pref.setCompletedAssessment(true);
        preferenceRepository.save(pref);

        String recommendedSlug = "arrays-and-complexity";
        String summary = "Based on your assessment, we recommend starting with Arrays & Complexity!";

        if (request.getExperienceLevel() == ExperienceLevel.INTERMEDIATE) {
            recommendedSlug = "searching";
            summary = "Great background! We recommend starting at Searching Algorithms.";
        } else if (request.getExperienceLevel() == ExperienceLevel.ADVANCED) {
            recommendedSlug = "trees";
            summary = "Advanced background! We recommend starting at Trees & Traversals.";
        }

        // Unlock starting module & prerequisites
        unlockModuleAndPredecessors(user, recommendedSlug);

        // Award +50 XP onboarding bonus
        gamificationService.awardXp(user, 50, "Completed Beginner Onboarding Assessment", "ASSESSMENT");

        RoadmapModule recModule = moduleRepository.findBySlug(recommendedSlug).orElse(null);
        String recTitle = recModule != null ? recModule.getTitle() : "Arrays & Complexity";

        com.codeloom.dsa.roadmap.entity.RoadmapTier tier = com.codeloom.dsa.roadmap.entity.RoadmapTier.BEGINNER;
        try {
            tier = com.codeloom.dsa.roadmap.entity.RoadmapTier.valueOf(request.getExperienceLevel().name());
        } catch (Exception ignored) {}

        return new AssessmentResultDto(
                pref.getId().toString(),
                tier,
                recommendedSlug,
                recTitle,
                summary,
                50
        );

    }

    @Transactional(readOnly = true)
    public LearningPreferenceResponse getPreferences(User user) {
        UserLearningPreference pref = preferenceRepository.findByUserId(user.getId())
                .orElseGet(() -> new UserLearningPreference(user, ExperienceLevel.BEGINNER, "Java", 30, PrimaryGoal.LEARN_DSA));

        return new LearningPreferenceResponse(
                pref.getExperienceLevel(),
                pref.getPreferredLanguage(),
                pref.getDailyLearningMinutes(),
                pref.getPrimaryGoal(),
                pref.getCompletedAssessment()
        );
    }

    public LearningPreferenceResponse updatePreferences(User user, LearningPreferenceRequest request) {
        UserLearningPreference pref = preferenceRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    UserLearningPreference newPref = new UserLearningPreference();
                    newPref.setUser(user);
                    return newPref;
                });

        pref.setExperienceLevel(request.getExperienceLevel());
        pref.setPreferredLanguage(request.getPreferredLanguage());
        pref.setDailyLearningMinutes(request.getDailyLearningMinutes());
        pref.setPrimaryGoal(request.getPrimaryGoal());
        preferenceRepository.save(pref);

        return new LearningPreferenceResponse(
                pref.getExperienceLevel(),
                pref.getPreferredLanguage(),
                pref.getDailyLearningMinutes(),
                pref.getPrimaryGoal(),
                pref.getCompletedAssessment()
        );
    }

    private RoadmapStatus determineModuleStatus(RoadmapModule module, Map<UUID, UserRoadmapProgress> progressMap) {
        UserRoadmapProgress userProgress = progressMap.get(module.getId());

        if (userProgress != null && userProgress.getStatus() != null) {
            return userProgress.getStatus();
        }

        if (module.getOrderIndex() <= 1 || module.getPrerequisiteModule() == null) {
            return RoadmapStatus.IN_PROGRESS;
        }

        RoadmapModule prereq = module.getPrerequisiteModule();
        UserRoadmapProgress prereqProgress = progressMap.get(prereq.getId());

        if (prereqProgress != null && prereqProgress.getStatus() == RoadmapStatus.COMPLETED) {
            return RoadmapStatus.IN_PROGRESS;
        }

        return RoadmapStatus.LOCKED;
    }

    private void unlockModuleAndPredecessors(User user, String targetSlug) {
        RoadmapModule target = moduleRepository.findBySlug(targetSlug).orElse(null);
        if (target == null) return;

        RoadmapModule curr = target;
        while (curr != null) {
            final UUID currentModuleId = curr.getId();
            UserRoadmapProgress progress = progressRepository.findByUserIdAndModuleId(user.getId(), currentModuleId)
                    .orElseGet(() -> new UserRoadmapProgress(user, moduleRepository.getReferenceById(currentModuleId), RoadmapStatus.IN_PROGRESS));

            if (progress.getStatus() == RoadmapStatus.LOCKED) {
                progress.setStatus(RoadmapStatus.IN_PROGRESS);
                progress.setUnlockedAt(LocalDateTime.now());
                progressRepository.save(progress);
            }
            curr = curr.getPrerequisiteModule();
        }
    }

    private RoadmapModuleDto mapToModuleDto(RoadmapModule module, RoadmapStatus status, int completionPercentage) {
        RoadmapModuleDto dto = new RoadmapModuleDto();
        dto.setId(module.getId());
        dto.setSlug(module.getSlug());
        dto.setTitle(module.getTitle());
        dto.setDescription(module.getDescription());
        dto.setOrderIndex(module.getOrderIndex());
        dto.setTier(module.getTier() != null ? module.getTier() : com.codeloom.dsa.roadmap.entity.RoadmapTier.BEGINNER);

        dto.setIconName(module.getIconName());
        dto.setCategorySlug(module.getCategorySlug());
        dto.setXpReward(module.getXpReward());
        dto.setStatus(status);
        dto.setCompletionPercentage(completionPercentage);

        if (module.getPrerequisiteModule() != null) {
            dto.setPrerequisiteModuleSlug(module.getPrerequisiteModule().getSlug());
            dto.setPrerequisiteModuleTitle(module.getPrerequisiteModule().getTitle());
        }

        return dto;
    }
}
