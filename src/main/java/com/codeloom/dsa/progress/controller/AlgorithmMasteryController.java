package com.codeloom.dsa.progress.controller;

import com.codeloom.dsa.progress.dto.AlgorithmMasteryDto;
import com.codeloom.dsa.progress.service.AlgorithmMasteryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/algorithms/{slug}/mastery")
@Tag(name = "Algorithm Mastery", description = "Endpoints for managing algorithm mastery and XP rewards")
@SecurityRequirement(name = "bearerAuth")
public class AlgorithmMasteryController {

    private final AlgorithmMasteryService masteryService;

    public AlgorithmMasteryController(AlgorithmMasteryService masteryService) {
        this.masteryService = masteryService;
    }

    @GetMapping
    @Operation(summary = "Get algorithm mastery status", description = "Retrieves whether the authenticated user has mastered the algorithm.")
    public ResponseEntity<AlgorithmMasteryDto> getMasteryStatus(
            @PathVariable String slug,
            Authentication authentication
    ) {
        String identifier = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(masteryService.getMasteryStatus(identifier, slug));
    }

    @PostMapping("/toggle")
    @Operation(summary = "Toggle algorithm mastery", description = "Toggles algorithm mastery state and awards +100 XP upon first mastery.")
    public ResponseEntity<AlgorithmMasteryDto> toggleMastery(
            @PathVariable String slug,
            Authentication authentication
    ) {
        String identifier = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(masteryService.toggleMastery(identifier, slug));
    }
}
