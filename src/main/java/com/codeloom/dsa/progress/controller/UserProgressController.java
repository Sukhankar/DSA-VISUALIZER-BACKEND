package com.codeloom.dsa.progress.controller;

import com.codeloom.dsa.progress.dto.LearningDashboardResponse;
import com.codeloom.dsa.progress.dto.ProgressResponse;
import com.codeloom.dsa.progress.dto.UpdateProgressRequest;
import com.codeloom.dsa.progress.service.UserProgressService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/me")
public class UserProgressController {

    private final UserProgressService userProgressService;

    public UserProgressController(UserProgressService userProgressService) {
        this.userProgressService = userProgressService;
    }

    @PostMapping("/progress/{algorithmSlug}/start")
    public ResponseEntity<ProgressResponse> startProgress(
            Authentication authentication,
            @PathVariable String algorithmSlug
    ) {
        ProgressResponse response = userProgressService.startProgress(authentication.getName(), algorithmSlug);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/progress/{algorithmSlug}")
    public ResponseEntity<ProgressResponse> updateProgress(
            Authentication authentication,
            @PathVariable String algorithmSlug,
            @Valid @RequestBody UpdateProgressRequest request
    ) {
        ProgressResponse response = userProgressService.updateProgress(authentication.getName(), algorithmSlug, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/progress/{algorithmSlug}/complete")
    public ResponseEntity<ProgressResponse> completeProgress(
            Authentication authentication,
            @PathVariable String algorithmSlug
    ) {
        ProgressResponse response = userProgressService.completeProgress(authentication.getName(), algorithmSlug);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/progress/{algorithmSlug}")
    public ResponseEntity<ProgressResponse> getProgress(
            Authentication authentication,
            @PathVariable String algorithmSlug
    ) {
        ProgressResponse response = userProgressService.getProgress(authentication.getName(), algorithmSlug);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/progress")
    public ResponseEntity<List<ProgressResponse>> listAllProgress(
            Authentication authentication
    ) {
        List<ProgressResponse> response = userProgressService.listAllProgress(authentication.getName());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<LearningDashboardResponse> getDashboard(
            Authentication authentication
    ) {
        LearningDashboardResponse response = userProgressService.getDashboard(authentication.getName());
        return ResponseEntity.ok(response);
    }
}
