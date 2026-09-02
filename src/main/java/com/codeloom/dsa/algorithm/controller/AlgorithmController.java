package com.codeloom.dsa.algorithm.controller;

import com.codeloom.dsa.algorithm.dto.AlgorithmDetailRichResponse;
import com.codeloom.dsa.algorithm.dto.AlgorithmPageResponse;
import com.codeloom.dsa.algorithm.dto.AlgorithmResponse;
import com.codeloom.dsa.algorithm.entity.Difficulty;
import com.codeloom.dsa.algorithm.service.AlgorithmService;
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

import java.util.Set;

@RestController
@RequestMapping("/api/v1/algorithms")
@Validated
@Tag(name = "Algorithms", description = "Public algorithm discovery, searching, filtering, and pagination APIs")
public class AlgorithmController {

    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of(
                    "name",
                    "difficulty",
                    "timeComplexity",
                    "spaceComplexity"
            );

    private final AlgorithmService algorithmService;

    public AlgorithmController(AlgorithmService algorithmService) {
        this.algorithmService = algorithmService;
    }

    @GetMapping
    @Operation(summary = "Get paginated algorithms", description = "Filter, search, and paginate through public algorithms by category, difficulty, or keyword. Does not require authentication.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Algorithms retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid page, size, or sort field")
    })
    public AlgorithmPageResponse getAlgorithms(
            @Parameter(description = "Filter by category slug (e.g. 'sorting', 'searching')")
            @RequestParam(required = false) String category,

            @Parameter(description = "Filter by difficulty level")
            @RequestParam(required = false) Difficulty difficulty,

            @Parameter(description = "Keyword search for algorithm name or description")
            @RequestParam(required = false) String search,

            @Parameter(description = "Zero-based page index (page >= 0)")
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "Page must be 0 or greater") int page,

            @Parameter(description = "Page size (1 <= size <= 100)")
            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "Size must be at least 1")
            @Max(value = 100, message = "Size must not exceed 100") int size,

            @Parameter(description = "Sort property and direction (e.g. 'name,asc', 'difficulty,desc'). Allowed fields: name, difficulty, timeComplexity, spaceComplexity")
            @RequestParam(defaultValue = "name,asc") String[] sort
    ) {

        String sortField = "name";
        String sortDirection = "asc";

        if (sort != null && sort.length > 0) {
            if (sort.length == 1 && sort[0].contains(",")) {
                String[] parts = sort[0].split(",");
                sortField = parts[0].trim();
                sortDirection = parts.length > 1 ? parts[1].trim() : "asc";
            } else if (sort.length >= 2) {
                sortField = sort[0].trim();
                sortDirection = sort[1].trim();
            } else {
                sortField = sort[0].trim();
            }
        }

        if (!ALLOWED_SORT_FIELDS.contains(sortField)) {
            throw new IllegalArgumentException("Invalid sort field: " + sortField);
        }

        Sort.Direction direction;
        if (sortDirection.equalsIgnoreCase("asc")) {
            direction = Sort.Direction.ASC;
        } else if (sortDirection.equalsIgnoreCase("desc")) {
            direction = Sort.Direction.DESC;
        } else {
            throw new IllegalArgumentException("Sort direction must be asc or desc");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        return algorithmService.getAlgorithms(category, difficulty, search, pageable);
    }


    @GetMapping("/{slug}")
    @Operation(summary = "Get algorithm details by slug", description = "Retrieves full metadata, description, complexity, and configuration for a specific algorithm.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Algorithm found"),
            @ApiResponse(responseCode = "404", description = "Algorithm not found with given slug")
    })
    public AlgorithmResponse getAlgorithmBySlug(
            @Parameter(description = "Algorithm slug (e.g. 'bubble-sort', 'binary-search')")
            @PathVariable String slug
    ) {
        return algorithmService.getAlgorithmBySlug(slug);
    }

    @GetMapping("/{slug}/details")
    @Operation(summary = "Get rich algorithm learning details", description = "Retrieves detailed LeetCode-style algorithm specification including input/output examples, multi-language code implementations (Java, Python, JS, C++), overview, advantages, limitations, and related algorithms.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rich algorithm learning details retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Algorithm not found with given slug")
    })
    public AlgorithmDetailRichResponse getRichAlgorithmDetails(
            @Parameter(description = "Algorithm slug (e.g. 'bubble-sort', 'binary-search')")
            @PathVariable String slug
    ) {
        return algorithmService.getRichAlgorithmDetails(slug);
    }

    @GetMapping("/{slug}/learning")
    @Operation(summary = "Get multi-level algorithm learning engine content", description = "Retrieves structured educational lessons for BEGINNER, INTERMEDIATE, or ADVANCED levels.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Structured learning engine content retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Algorithm not found with given slug")
    })
    public com.codeloom.dsa.algorithm.dto.AlgorithmLearningResponse getLearningContent(
            @Parameter(description = "Algorithm slug (e.g. 'bubble-sort', 'binary-search')")
            @PathVariable String slug,

            @Parameter(description = "Educational depth level (BEGINNER, INTERMEDIATE, ADVANCED)")
            @RequestParam(required = false) com.codeloom.dsa.learning.entity.ExperienceLevel level
    ) {
        return algorithmService.getLearningContent(slug, level);
    }
}