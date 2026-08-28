package com.codeloom.dsa.practice.controller;

import com.codeloom.dsa.practice.dto.*;
import com.codeloom.dsa.practice.service.DailyChallengeService;
import com.codeloom.dsa.practice.service.PracticeSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/practice")
@Tag(name = "Practice Arena", description = "Daily challenges, practice sessions, timed modes, and problem challenges")
public class PracticeController {

    private final DailyChallengeService dailyChallengeService;
    private final PracticeSessionService practiceSessionService;

    public PracticeController(
            DailyChallengeService dailyChallengeService,
            PracticeSessionService practiceSessionService
    ) {
        this.dailyChallengeService = dailyChallengeService;
        this.practiceSessionService = practiceSessionService;
    }

    @GetMapping("/arena")
    @Operation(summary = "Get Practice Arena overview", description = "Retrieves active streak, XP, today's daily challenge, active session, and recent history")
    public ResponseEntity<PracticeArenaOverviewResponse> getArenaOverview(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        PracticeArenaOverviewResponse response = practiceSessionService.getArenaOverview(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/daily")
    @Operation(summary = "Get today's Daily Challenge", description = "Retrieves current daily challenge details and completion status")
    public ResponseEntity<DailyChallengeDto> getDailyChallenge(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        DailyChallengeDto challenge = dailyChallengeService.getTodayChallengeDto(userDetails.getUsername());
        return ResponseEntity.ok(challenge);
    }

    @PostMapping("/sessions")
    @Operation(summary = "Start a new practice session", description = "Creates a new practice session by mode (DAILY, QUICK, TOPIC, RANDOM, TIMED, STREAK)")
    public ResponseEntity<PracticeSessionDto> createSession(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreatePracticeSessionRequest request
    ) {
        PracticeSessionDto session = practiceSessionService.createSession(userDetails.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(session);
    }

    @GetMapping("/sessions/{id}")
    @Operation(summary = "Get practice session details", description = "Retrieves problem set and live status of a practice session")
    public ResponseEntity<PracticeSessionDto> getSession(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID id
    ) {
        PracticeSessionDto session = practiceSessionService.getSessionDto(userDetails.getUsername(), id);
        return ResponseEntity.ok(session);
    }

    @PostMapping("/sessions/{id}/submit")
    @Operation(summary = "Submit code solution in session", description = "Evaluates code against test cases within practice session context")
    public ResponseEntity<SessionSubmitResponse> submitInSession(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID id,
            @Valid @RequestBody SessionSubmitRequest request
    ) {
        SessionSubmitResponse response = practiceSessionService.submitInSession(userDetails.getUsername(), id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/sessions/{id}/abandon")
    @Operation(summary = "Abandon practice session", description = "Marks active practice session as abandoned")
    public ResponseEntity<PracticeSessionDto> abandonSession(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID id
    ) {
        PracticeSessionDto session = practiceSessionService.abandonSession(userDetails.getUsername(), id);
        return ResponseEntity.ok(session);
    }

    @GetMapping("/history")
    @Operation(summary = "Get user practice session history", description = "Paginated history of past practice sessions")
    public ResponseEntity<Page<PracticeSessionDto>> getHistory(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<PracticeSessionDto> history = practiceSessionService.getUserSessionHistory(userDetails.getUsername(), page, size);
        return ResponseEntity.ok(history);
    }
}
