package com.codeloom.dsa.roadmap.controller;

import com.codeloom.dsa.roadmap.dto.*;
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
@RequestMapping("/api/v1/roadmap")
public class RoadmapController {

    private final RoadmapService roadmapService;
    private final UserRepository userRepository;

    public RoadmapController(RoadmapService roadmapService, UserRepository userRepository) {
        this.roadmapService = roadmapService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<RoadmapModuleDto>> getUserRoadmap(@AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(roadmapService.getUserRoadmap(user));
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
            @Valid @RequestBody AssessmentRequestDto request) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(roadmapService.submitAssessment(user, request));
    }

    @GetMapping("/recommendations")
    public ResponseEntity<NextRecommendationDto> getSmartRecommendation(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(roadmapService.getSmartRecommendation(user));
    }

    private User getUser(UserDetails userDetails) {
        if (userDetails == null) {
            return null;
        }
        return userRepository.findByUsername(userDetails.getUsername())
                .orElse(null);
    }
}
