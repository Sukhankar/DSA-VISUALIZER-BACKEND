package com.codeloom.dsa.algorithm.controller;

import com.codeloom.dsa.algorithm.dto.AlgorithmPageResponse;
import com.codeloom.dsa.algorithm.dto.AlgorithmResponse;
import com.codeloom.dsa.algorithm.entity.Difficulty;
import com.codeloom.dsa.algorithm.service.AlgorithmService;
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
public class AlgorithmController {

    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of(
                    "name",
                    "difficulty",
                    "timeComplexity",
                    "spaceComplexity"
            );

    private final AlgorithmService algorithmService;

    public AlgorithmController(
            AlgorithmService algorithmService
    ) {
        this.algorithmService = algorithmService;
    }

    @GetMapping
    public AlgorithmPageResponse getAlgorithms(

            @RequestParam(required = false)
            String category,

            @RequestParam(required = false)
            Difficulty difficulty,

            @RequestParam(required = false)
            String search,

            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "Page must be 0 or greater")
            int page,

            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "Size must be at least 1")
            @Max(value = 100, message = "Size must not exceed 100")
            int size,

            @RequestParam(defaultValue = "name,asc")
            String[] sort
    ) {

        String sortField = sort[0];

        if (!ALLOWED_SORT_FIELDS.contains(sortField)) {
            throw new IllegalArgumentException(
                    "Invalid sort field: " + sortField
            );
        }

        String sortDirection =
                sort.length > 1 ? sort[1] : "asc";

        Sort.Direction direction;

        if (sortDirection.equalsIgnoreCase("asc")) {
            direction = Sort.Direction.ASC;
        } else if (sortDirection.equalsIgnoreCase("desc")) {
            direction = Sort.Direction.DESC;
        } else {
            throw new IllegalArgumentException(
                    "Sort direction must be asc or desc"
            );
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(direction, sortField)
        );

        return algorithmService.getAlgorithms(
                category,
                difficulty,
                search,
                pageable
        );
    }

    @GetMapping("/{slug}")
    public AlgorithmResponse getAlgorithmBySlug(
            @PathVariable String slug
    ) {
        return algorithmService.getAlgorithmBySlug(slug);
    }
}