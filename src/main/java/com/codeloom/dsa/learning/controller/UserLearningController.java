package com.codeloom.dsa.learning.controller;

import com.codeloom.dsa.learning.dto.LearningPreferenceRequest;
import com.codeloom.dsa.learning.dto.LearningPreferenceResponse;
import com.codeloom.dsa.learning.dto.LearningRecommendationResponse;
import com.codeloom.dsa.learning.service.LearningRecommendationService;
import com.codeloom.dsa.learning.service.RoadmapProgressService;
import com.codeloom.dsa.roadmap.dto.RoadmapModuleDto;
import com.codeloom.dsa.roadmap.dto.UserRoadmapDto;
import com.codeloom.dsa.user.entity.User;
import com.codeloom.dsa.user.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/me")
public class UserLearningController {

    private final RoadmapProgressService roadmapProgressService;
    private final LearningRecommendationService recommendationService;
    private final UserRepository userRepository;

    public UserLearningController(
            RoadmapProgressService roadmapProgressService,
            LearningRecommendationService recommendationService,
            UserRepository userRepository) {
        this.roadmapProgressService = roadmapProgressService;
        this.recommendationService = recommendationService;
        this.userRepository = userRepository;
    }

    @GetMapping("/roadmap")
    public ResponseEntity<UserRoadmapDto> getUserRoadmap(@AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(roadmapProgressService.getUserRoadmap(user));
    }

    @PostMapping("/roadmap/modules/{slug}/start")
    public ResponseEntity<RoadmapModuleDto> startModule(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String slug) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(roadmapProgressService.startModule(user, slug));
    }

    @GetMapping("/recommendations")
    public ResponseEntity<LearningRecommendationResponse> getRecommendation(@AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(recommendationService.getNextRecommendation(user));
    }

    @GetMapping("/learning-preferences")
    public ResponseEntity<LearningPreferenceResponse> getPreferences(@AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(roadmapProgressService.getPreferences(user));
    }

    @PutMapping("/learning-preferences")
    public ResponseEntity<LearningPreferenceResponse> updatePreferences(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody LearningPreferenceRequest request) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(roadmapProgressService.updatePreferences(user, request));
    }

    private User getUser(UserDetails userDetails) {
        if (userDetails == null) return null;
        return userRepository.findByUsername(userDetails.getUsername()).orElse(null);
    }
}
