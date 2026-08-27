package com.codeloom.dsa.algorithm.controller;

import com.codeloom.dsa.algorithm.dto.AlgorithmCategoryResponse;
import com.codeloom.dsa.algorithm.service.AlgorithmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@Tag(name = "Categories", description = "Public algorithm category discovery APIs")
public class AlgorithmCategoryController {

    private final AlgorithmService algorithmService;

    public AlgorithmCategoryController(
            AlgorithmService algorithmService
    ) {
        this.algorithmService = algorithmService;
    }

    @GetMapping
    @Operation(summary = "Get all categories", description = "Retrieves all public algorithm categories (Sorting, Searching, Trees, Graphs, etc.). Does not require authentication.")
    @ApiResponse(responseCode = "200", description = "Categories retrieved successfully")
    public List<AlgorithmCategoryResponse> getCategories() {
        return algorithmService.getAllCategories();
    }
}