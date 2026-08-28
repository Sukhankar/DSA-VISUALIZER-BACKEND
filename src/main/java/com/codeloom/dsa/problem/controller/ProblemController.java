package com.codeloom.dsa.problem.controller;

import com.codeloom.dsa.algorithm.entity.Difficulty;
import com.codeloom.dsa.problem.dto.ProblemDetailResponse;
import com.codeloom.dsa.problem.dto.ProblemPageResponse;
import com.codeloom.dsa.problem.service.ProblemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/problems")
@Validated
@Tag(name = "Problems", description = "Public LeetCode-style problem catalog, search, and specification APIs")
public class ProblemController {

    private final ProblemService problemService;

    public ProblemController(ProblemService problemService) {
        this.problemService = problemService;
    }

    @GetMapping
    @Operation(summary = "Get paginated problem list", description = "Filter, search, and paginate through coding practice problems by difficulty, category, or keyword.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Problems retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid page, size, or sort field")
    })
    public ProblemPageResponse getProblems(
            @Parameter(description = "Filter by difficulty level")
            @RequestParam(required = false) Difficulty difficulty,

            @Parameter(description = "Filter by category slug")
            @RequestParam(required = false) String category,

            @Parameter(description = "Keyword search for title or description")
            @RequestParam(required = false) String search,

            @Parameter(description = "Zero-based page index (page >= 0)")
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "Page must be 0 or greater") int page,

            @Parameter(description = "Page size (1 <= size <= 100)")
            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "Size must be at least 1")
            @Max(value = 100, message = "Size must not exceed 100") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "title"));
        return problemService.getProblems(difficulty, category, search, pageable);
    }

    @GetMapping("/{slug}")
    @Operation(summary = "Get problem specification by slug", description = "Retrieves full problem details including description, constraints, examples, hints, and solution explanation.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Problem found"),
            @ApiResponse(responseCode = "404", description = "Problem not found with given slug")
    })
    public ProblemDetailResponse getProblemBySlug(
            @Parameter(description = "Problem slug (e.g. 'two-sum', 'binary-search-problem')")
            @PathVariable String slug
    ) {
        return problemService.getProblemBySlug(slug);
    }
}
