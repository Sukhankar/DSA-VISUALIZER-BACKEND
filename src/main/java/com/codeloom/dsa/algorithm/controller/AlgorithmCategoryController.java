package com.codeloom.dsa.algorithm.controller;

import com.codeloom.dsa.algorithm.dto.AlgorithmCategoryResponse;
import com.codeloom.dsa.algorithm.service.AlgorithmService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class AlgorithmCategoryController {

    private final AlgorithmService algorithmService;

    public AlgorithmCategoryController(
            AlgorithmService algorithmService
    ) {
        this.algorithmService = algorithmService;
    }

    @GetMapping
    public List<AlgorithmCategoryResponse> getCategories() {
        return algorithmService.getAllCategories();
    }
}