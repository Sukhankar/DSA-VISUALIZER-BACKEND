package com.codeloom.dsa.learning.service;

import com.codeloom.dsa.learning.dto.LearningRecommendationResponse;
import com.codeloom.dsa.roadmap.dto.RoadmapModuleDto;
import com.codeloom.dsa.roadmap.dto.UserRoadmapDto;
import com.codeloom.dsa.roadmap.entity.RoadmapStatus;
import com.codeloom.dsa.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LearningRecommendationService {

    private final RoadmapProgressService roadmapProgressService;

    public LearningRecommendationService(RoadmapProgressService roadmapProgressService) {
        this.roadmapProgressService = roadmapProgressService;
    }

    public LearningRecommendationResponse getNextRecommendation(User user) {
        UserRoadmapDto userRoadmap = roadmapProgressService.getUserRoadmap(user);
        RoadmapModuleDto targetModule = userRoadmap.getCurrentModule();

        if (targetModule == null && !userRoadmap.getModules().isEmpty()) {
            targetModule = userRoadmap.getModules().get(0);
        }

        if (targetModule == null) {
            return new LearningRecommendationResponse(
                    "ALGORITHM",
                    "Begin Programming Fundamentals",
                    "Start your algorithmic journey with core memory and variable fundamentals.",
                    "programming-fundamentals",
                    0,
                    100,
                    "Start Learning",
                    "/roadmap/topics/programming-fundamentals"
            );
        }

        if (targetModule.getStatus() == RoadmapStatus.COMPLETED) {
            // Find next incomplete module
            targetModule = userRoadmap.getModules().stream()
                    .filter(m -> m.getStatus() != RoadmapStatus.COMPLETED)
                    .findFirst()
                    .orElse(targetModule);
        }

        String type = "MODULE";
        String actionLabel = "Continue Learning";
        String actionUrl = "/roadmap/topics/" + targetModule.getSlug();
        String title = "Continue " + targetModule.getTitle();
        String description = "You are " + targetModule.getCompletionPercentage() + "% through the " + targetModule.getTitle() + " module.";

        if (targetModule.getCompletionPercentage() < 50) {
            type = "ALGORITHM";
            actionLabel = "Learn Next Algorithm";
            title = "Explore " + targetModule.getTitle();
            description = "Step into interactive visual step Tracing for " + targetModule.getTitle() + ".";
        } else if (targetModule.getCompletionPercentage() < 100) {
            type = "PROBLEM";
            actionLabel = "Solve Practice Problem";
            title = "Practice " + targetModule.getTitle();
            description = "Apply your knowledge on curated LeetCode-style problem challenges.";
        }

        return new LearningRecommendationResponse(
                type,
                title,
                description,
                targetModule.getSlug(),
                targetModule.getCompletionPercentage(),
                targetModule.getXpReward(),
                actionLabel,
                actionUrl
        );
    }
}
