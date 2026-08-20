package com.codeloom.dsa.algorithm.controller;

import com.codeloom.dsa.algorithm.dto.AlgorithmPageResponse;
import com.codeloom.dsa.algorithm.dto.AlgorithmResponse;
import com.codeloom.dsa.algorithm.entity.Difficulty;
import com.codeloom.dsa.algorithm.service.AlgorithmService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/algorithms")
public class AlgorithmController {

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
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(defaultValue = "name,asc")
            String[] sort
    ) {

        String sortField = sort[0];
        String sortDirection =
                sort.length > 1 ? sort[1] : "asc";

        Sort.Direction direction =
                sortDirection.equalsIgnoreCase("desc")
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

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