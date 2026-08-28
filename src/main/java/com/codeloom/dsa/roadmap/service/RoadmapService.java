package com.codeloom.dsa.roadmap.service;

import com.codeloom.dsa.analytics.service.GamificationService;
import com.codeloom.dsa.roadmap.dto.*;
import com.codeloom.dsa.roadmap.entity.*;
import com.codeloom.dsa.roadmap.repository.*;
import com.codeloom.dsa.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class RoadmapService {

    private final RoadmapModuleRepository moduleRepository;
    private final RoadmapStepRepository stepRepository;
    private final UserRoadmapProgressRepository progressRepository;
    private final UserAssessmentRepository assessmentRepository;
    private final GamificationService gamificationService;

    public RoadmapService(
            RoadmapModuleRepository moduleRepository,
            RoadmapStepRepository stepRepository,
            UserRoadmapProgressRepository progressRepository,
            UserAssessmentRepository assessmentRepository,
            GamificationService gamificationService) {
        this.moduleRepository = moduleRepository;
        this.stepRepository = stepRepository;
        this.progressRepository = progressRepository;
        this.assessmentRepository = assessmentRepository;
        this.gamificationService = gamificationService;
    }

    @Transactional(readOnly = true)
    public List<RoadmapModuleDto> getUserRoadmap(User user) {
        List<RoadmapModule> modules = moduleRepository.findAllByOrderByOrderIndexAsc();
        List<UserRoadmapProgress> userProgressList = user != null ? progressRepository.findByUserId(user.getId()) : List.of();

        Map<UUID, UserRoadmapProgress> progressMap = userProgressList.stream()
                .collect(Collectors.toMap(p -> p.getModule().getId(), p -> p, (v1, v2) -> v1));

        List<RoadmapModuleDto> dtoList = new ArrayList<>();

        for (RoadmapModule module : modules) {
            UserRoadmapProgress userProgress = progressMap.get(module.getId());
            RoadmapStatus status = determineModuleStatus(module, progressMap);
            int completionPercentage = userProgress != null ? userProgress.getCompletionPercentage() : 0;

            if (status == RoadmapStatus.COMPLETED) {
                completionPercentage = 100;
            }

            RoadmapModuleDto dto = mapToModuleDto(module, status, completionPercentage);
            dtoList.add(dto);
        }

        return dtoList;
    }

    @Transactional(readOnly = true)
    public RoadmapModuleDto getModuleDetails(User user, String slug) {
        RoadmapModule module = moduleRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Roadmap module not found with slug: " + slug));

        List<UserRoadmapProgress> userProgressList = user != null ? progressRepository.findByUserId(user.getId()) : List.of();
        Map<UUID, UserRoadmapProgress> progressMap = userProgressList.stream()
                .collect(Collectors.toMap(p -> p.getModule().getId(), p -> p, (v1, v2) -> v1));

        RoadmapStatus status = determineModuleStatus(module, progressMap);
        UserRoadmapProgress userProgress = progressMap.get(module.getId());
        int completionPercentage = userProgress != null ? userProgress.getCompletionPercentage() : 0;

        RoadmapModuleDto dto = mapToModuleDto(module, status, completionPercentage);

        List<RoadmapStep> steps = stepRepository.findByModuleSlugOrderByStepNumberAsc(slug);
        List<RoadmapStepDto> stepDtos = new ArrayList<>();

        for (RoadmapStep step : steps) {
            boolean isCompleted = completionPercentage >= (step.getStepNumber() * 20);
            RoadmapStepDto stepDto = new RoadmapStepDto(
                    step.getId(),
                    step.getStepNumber(),
                    step.getStepType(),
                    step.getTitle(),
                    step.getDescription(),
                    step.getReferenceSlug(),
                    step.getXpReward(),
                    isCompleted
            );
            stepDtos.add(stepDto);
        }

        dto.setSteps(stepDtos);
        return dto;
    }

    public AssessmentResultDto submitAssessment(User user, AssessmentRequestDto request) {
        String recommendedSlug = "arrays-basics";
        String summary = "We recommend starting with Arrays & Basics to build a rock-solid foundation!";

        if (Boolean.TRUE.equals(request.getKnowsTrees()) || request.getExperienceLevel() == RoadmapTier.ADVANCED) {
            recommendedSlug = "trees-and-traversals";
            summary = "Great background! We recommend starting at Trees & Traversals.";
        } else if (Boolean.TRUE.equals(request.getKnowsSorting()) || request.getExperienceLevel() == RoadmapTier.INTERMEDIATE) {
            recommendedSlug = "sorting-algorithms";
            summary = "Nice! We recommend continuing your path with Sorting Algorithms.";
        }

        UserAssessment assessment = new UserAssessment();
        assessment.setUser(user);
        assessment.setExperienceLevel(request.getExperienceLevel());
        assessment.setPreferredLanguage(request.getPreferredLanguage());
        assessment.setKnowsArrays(request.getKnowsArrays());
        assessment.setKnowsSorting(request.getKnowsSorting());
        assessment.setKnowsTrees(request.getKnowsTrees());
        assessment.setSolvedProblemsBefore(request.getSolvedProblemsBefore());
        assessment.setGoal(request.getGoal());
        assessment.setRecommendedModuleSlug(recommendedSlug);

        assessmentRepository.save(assessment);

        // Unlock recommended module & predecessors
        unlockModuleAndPredecessors(user, recommendedSlug);

        // Award +50 XP onboarding bonus
        gamificationService.awardXp(user, 50, "Completed Beginner Assessment Survey", "ASSESSMENT");

        RoadmapModule recModule = moduleRepository.findBySlug(recommendedSlug).orElse(null);
        String recTitle = recModule != null ? recModule.getTitle() : "Arrays & Basics";

        return new AssessmentResultDto(
                assessment.getId().toString(),
                request.getExperienceLevel(),
                recommendedSlug,
                recTitle,
                summary,
                50
        );
    }

    @Transactional(readOnly = true)
    public NextRecommendationDto getSmartRecommendation(User user) {
        List<RoadmapModuleDto> roadmap = getUserRoadmap(user);

        RoadmapModuleDto targetModule = roadmap.stream()
                .filter(m -> m.getStatus() == RoadmapStatus.IN_PROGRESS)
                .findFirst()
                .orElse(roadmap.isEmpty() ? null : roadmap.get(0));

        if (targetModule == null) {
            return new NextRecommendationDto(
                    "arrays-basics",
                    "Arrays & Basics",
                    "Learn Array Basics",
                    "LEARN",
                    "two-sum",
                    "/roadmap/topics/arrays-basics",
                    "Start your algorithmic journey here!",
                    50
            );
        }

        String actionUrl = "/roadmap/topics/" + targetModule.getSlug();
        String stepTitle = "Continue " + targetModule.getTitle();
        String reason = "You are currently " + targetModule.getCompletionPercentage() + "% completed with this module.";

        return new NextRecommendationDto(
                targetModule.getSlug(),
                targetModule.getTitle(),
                stepTitle,
                "PRACTICE",
                targetModule.getCategorySlug(),
                actionUrl,
                reason,
                targetModule.getXpReward()
        );
    }

    private RoadmapStatus determineModuleStatus(RoadmapModule module, Map<UUID, UserRoadmapProgress> progressMap) {
        UserRoadmapProgress userProgress = progressMap.get(module.getId());

        if (userProgress != null && userProgress.getStatus() != null) {
            return userProgress.getStatus();
        }

        // Module 1 is always unlocked by default
        if (module.getOrderIndex() == 1 || module.getPrerequisiteModule() == null) {
            return RoadmapStatus.IN_PROGRESS;
        }

        // Check if prerequisite module is completed
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
        dto.setTier(module.getTier());
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
