package com.codeloom.dsa.progress.controller;

import com.codeloom.dsa.progress.dto.LearningDashboardResponse;
import com.codeloom.dsa.progress.dto.ProgressResponse;
import com.codeloom.dsa.progress.dto.UpdateProgressRequest;
import com.codeloom.dsa.progress.service.UserProgressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/me")
@Tag(name = "User Progress & Dashboard", description = "User algorithm learning progress, completion tracking, and dashboard metrics")
@SecurityRequirement(name = "bearerAuth")
public class UserProgressController {

    private final UserProgressService userProgressService;

    public UserProgressController(UserProgressService userProgressService) {
        this.userProgressService = userProgressService;
    }

    @PostMapping("/progress/{algorithmSlug}/start")
    @Operation(summary = "Start learning an algorithm", description = "Marks algorithm learning progress as IN_PROGRESS. Idempotent call.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Progress started or existing progress retained"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Algorithm not found")
    })
    public ResponseEntity<ProgressResponse> startProgress(
            Authentication authentication,
            @Parameter(description = "Algorithm slug (e.g. 'quick-sort')")
            @PathVariable String algorithmSlug
    ) {
        ProgressResponse response = userProgressService.startProgress(authentication.getName(), algorithmSlug);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/progress/{algorithmSlug}")
    @Operation(summary = "Update learning progress", description = "Updates progress percentage (0-100%) and optional last step index for an algorithm.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Progress updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid progress percentage (must be 0-100)"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Algorithm not found")
    })
    public ResponseEntity<ProgressResponse> updateProgress(
            Authentication authentication,
            @Parameter(description = "Algorithm slug (e.g. 'quick-sort')")
            @PathVariable String algorithmSlug,
            @Valid @RequestBody UpdateProgressRequest request
    ) {
        ProgressResponse response = userProgressService.updateProgress(authentication.getName(), algorithmSlug, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/progress/{algorithmSlug}/complete")
    @Operation(summary = "Mark algorithm as completed", description = "Sets status to COMPLETED and progress percentage to 100%.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Algorithm marked as completed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Algorithm not found")
    })
    public ResponseEntity<ProgressResponse> completeProgress(
            Authentication authentication,
            @Parameter(description = "Algorithm slug (e.g. 'quick-sort')")
            @PathVariable String algorithmSlug
    ) {
        ProgressResponse response = userProgressService.completeProgress(authentication.getName(), algorithmSlug);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/progress/{algorithmSlug}")
    @Operation(summary = "Get progress for a specific algorithm", description = "Returns progress state. If not yet started, returns clean NOT_STARTED default response.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Progress retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Algorithm not found")
    })
    public ResponseEntity<ProgressResponse> getProgress(
            Authentication authentication,
            @Parameter(description = "Algorithm slug (e.g. 'quick-sort')")
            @PathVariable String algorithmSlug
    ) {
        ProgressResponse response = userProgressService.getProgress(authentication.getName(), algorithmSlug);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/progress")
    @Operation(summary = "List all progress records", description = "Returns all algorithm progress records for the user ordered by recent activity.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Progress history list retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<ProgressResponse>> listAllProgress(
            Authentication authentication
    ) {
        List<ProgressResponse> response = userProgressService.listAllProgress(authentication.getName());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get user learning dashboard", description = "Returns summary metrics: total algorithms, started, completed, favorites, completion percentage, and recent activity.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dashboard summary metrics retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<LearningDashboardResponse> getDashboard(
            Authentication authentication
    ) {
        LearningDashboardResponse response = userProgressService.getDashboard(authentication.getName());
        return ResponseEntity.ok(response);
    }
}
