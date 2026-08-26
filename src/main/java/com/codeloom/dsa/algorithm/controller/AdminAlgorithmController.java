package com.codeloom.dsa.algorithm.controller;

import com.codeloom.dsa.algorithm.dto.AlgorithmResponse;
import com.codeloom.dsa.algorithm.dto.CreateAlgorithmRequest;
import com.codeloom.dsa.algorithm.dto.UpdateAlgorithmRequest;
import com.codeloom.dsa.algorithm.service.AlgorithmService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/algorithms")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAlgorithmController {

    private final AlgorithmService algorithmService;

    public AdminAlgorithmController(
            AlgorithmService algorithmService
    ) {
        this.algorithmService = algorithmService;
    }

    @PostMapping
    public ResponseEntity<AlgorithmResponse> createAlgorithm(
            @Valid @RequestBody CreateAlgorithmRequest request
    ) {
        AlgorithmResponse response =
                algorithmService.createAlgorithm(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping("/{slug}")
    public ResponseEntity<AlgorithmResponse> updateAlgorithm(
            @PathVariable String slug,
            @Valid @RequestBody UpdateAlgorithmRequest request
    ) {
        AlgorithmResponse response =
                algorithmService.updateAlgorithm(slug, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{slug}")
    public ResponseEntity<Void> deleteAlgorithm(
            @PathVariable String slug
    ) {
        algorithmService.deleteAlgorithm(slug);

        return ResponseEntity.noContent().build();
    }
}
