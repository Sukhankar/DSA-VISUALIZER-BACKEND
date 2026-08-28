package com.codeloom.dsa.learning.controller;

import com.codeloom.dsa.learning.dto.LearningPathResponse;
import com.codeloom.dsa.learning.dto.OnboardingAssessmentRequest;
import com.codeloom.dsa.learning.service.LearningPathService;
import com.codeloom.dsa.learning.service.RoadmapProgressService;
import com.codeloom.dsa.roadmap.dto.AssessmentResultDto;
import com.codeloom.dsa.roadmap.dto.RoadmapModuleDto;
import com.codeloom.dsa.roadmap.service.RoadmapService;
import com.codeloom.dsa.user.entity.User;
import com.codeloom.dsa.user.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/learning")
public class LearningController {

    private final LearningPathService learningPathService;
    private final RoadmapProgressService roadmapProgressService;
    private final RoadmapService roadmapService;
    private final UserRepository userRepository;

    public LearningController(
            LearningPathService learningPathService,
            RoadmapProgressService roadmapProgressService,
            RoadmapService roadmapService,
            UserRepository userRepository) {
        this.learningPathService = learningPathService;
        this.roadmapProgressService = roadmapProgressService;
        this.roadmapService = roadmapService;
        this.userRepository = userRepository;
    }

    @GetMapping("/paths")
    public ResponseEntity<List<LearningPathResponse>> getLearningPaths() {
        return ResponseEntity.ok(learningPathService.getAllActivePaths());
    }

    @GetMapping("/paths/{slug}")
    public ResponseEntity<LearningPathResponse> getLearningPathBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(learningPathService.getPathBySlug(slug));
    }

    @GetMapping("/modules/{slug}")
    public ResponseEntity<RoadmapModuleDto> getModuleDetails(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String slug) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(roadmapService.getModuleDetails(user, slug));
    }

    @PostMapping("/assessment")
    public ResponseEntity<AssessmentResultDto> submitAssessment(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody OnboardingAssessmentRequest request) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(roadmapProgressService.processOnboardingAssessment(user, request));
    }

    private User getUser(UserDetails userDetails) {
        if (userDetails == null) return null;
        return userRepository.findByUsername(userDetails.getUsername()).orElse(null);
    }
}
