package com.codeloom.dsa.problem.controller;

import com.codeloom.dsa.problem.dto.*;
import com.codeloom.dsa.problem.service.ProblemSubmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Problem Submissions", description = "Endpoints for running code against sample test cases, submitting solutions, viewing submission history and stats")
public class ProblemSubmissionController {

    private final ProblemSubmissionService submissionService;

    public ProblemSubmissionController(ProblemSubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @PostMapping("/problems/{slug}/run")
    @Operation(summary = "Run code sample test cases", description = "Executes source code against sample test cases without persisting a formal submission record.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Code executed successfully"),
            @ApiResponse(responseCode = "404", description = "Problem not found")
    })
    public ResponseEntity<RunCodeResponse> runSampleCode(
            @PathVariable String slug,
            @Valid @RequestBody RunCodeRequest request
    ) {
        RunCodeResponse response = submissionService.runSampleCode(slug, request.language(), request.sourceCode());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/problems/{slug}/submit")
    @Operation(summary = "Submit problem solution", description = "Evaluates source code against full test cases (sample + hidden) and persists a submission record.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Submission processed and verdict recorded"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "404", description = "Problem not found")
    })
    public ResponseEntity<SubmissionResponse> submitSolution(
            @PathVariable String slug,
            @Valid @RequestBody SubmitCodeRequest request,
            Authentication authentication
    ) {
        SubmissionResponse response = submissionService.submitSolution(authentication.getName(), slug, request.language(), request.sourceCode());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/problems/{slug}/submissions")
    @Operation(summary = "Get user submissions for problem", description = "Lists current user's submission history for a specific problem.")
    public ResponseEntity<List<SubmissionResponse>> getProblemSubmissions(
            @PathVariable String slug,
            Authentication authentication
    ) {
        List<SubmissionResponse> history = submissionService.getProblemSubmissions(authentication.getName(), slug);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/users/me/submissions")
    @Operation(summary = "Get current user submission history", description = "Paginated list of all problem submissions made by the user.")
    public ResponseEntity<Page<SubmissionResponse>> getUserSubmissions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication
    ) {
        Page<SubmissionResponse> pageResult = submissionService.getUserSubmissions(authentication.getName(), PageRequest.of(page, size));
        return ResponseEntity.ok(pageResult);
    }

    @GetMapping("/users/me/problem-stats")
    @Operation(summary = "Get user problem practice statistics", description = "Returns total solved counts, difficulty breakdown, and acceptance rate.")
    public ResponseEntity<ProblemUserStatsResponse> getUserProblemStats(Authentication authentication) {
        ProblemUserStatsResponse stats = submissionService.getUserProblemStats(authentication.getName());
        return ResponseEntity.ok(stats);
    }
}
