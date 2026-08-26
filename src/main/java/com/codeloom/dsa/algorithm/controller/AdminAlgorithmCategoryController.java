package com.codeloom.dsa.algorithm.controller;

import com.codeloom.dsa.algorithm.dto.AlgorithmCategoryResponse;
import com.codeloom.dsa.algorithm.dto.CreateAlgorithmCategoryRequest;
import com.codeloom.dsa.algorithm.dto.UpdateAlgorithmCategoryRequest;
import com.codeloom.dsa.algorithm.service.AlgorithmService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/admin/categories")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAlgorithmCategoryController {

    private final AlgorithmService algorithmService;

    public AdminAlgorithmCategoryController(AlgorithmService algorithmService) {
        this.algorithmService = algorithmService;
    }

    @PostMapping
    public ResponseEntity<AlgorithmCategoryResponse> createCategory(
            @Valid @RequestBody CreateAlgorithmCategoryRequest request
    ) {
        AlgorithmCategoryResponse response = algorithmService.createCategory(request);
        return ResponseEntity
                .created(URI.create("/api/v1/categories/" + response.slug()))
                .body(response);
    }

    @PutMapping("/{slug}")
    public ResponseEntity<AlgorithmCategoryResponse> updateCategory(
            @PathVariable String slug,
            @Valid @RequestBody UpdateAlgorithmCategoryRequest request
    ) {
        AlgorithmCategoryResponse response = algorithmService.updateCategory(slug, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{slug}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable String slug
    ) {
        algorithmService.deleteCategory(slug);
        return ResponseEntity.noContent().build();
    }
}
