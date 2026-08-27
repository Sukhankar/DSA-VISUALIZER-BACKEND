package com.codeloom.dsa.algorithm.controller;

import com.codeloom.dsa.algorithm.dto.AlgorithmResponse;
import com.codeloom.dsa.algorithm.dto.CreateAlgorithmRequest;
import com.codeloom.dsa.algorithm.dto.UpdateAlgorithmRequest;
import com.codeloom.dsa.algorithm.service.AlgorithmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Admin Operations")
@SecurityRequirement(name = "bearerAuth")
public class AdminAlgorithmController {

    private final AlgorithmService algorithmService;

    public AdminAlgorithmController(
            AlgorithmService algorithmService
    ) {
        this.algorithmService = algorithmService;
    }

    @PostMapping
    @Operation(summary = "Create algorithm (Admin only)", description = "Creates a new algorithm record under an existing category. Requires ROLE_ADMIN authority.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Algorithm created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failure or duplicate algorithm name/slug"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Requires ROLE_ADMIN authority"),
            @ApiResponse(responseCode = "404", description = "Category specified in request not found")
    })
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
    @Operation(summary = "Update algorithm (Admin only)", description = "Updates metadata, complexities, and configuration of an existing algorithm. Requires ROLE_ADMIN authority.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Algorithm updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failure or duplicate name/slug"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Requires ROLE_ADMIN authority"),
            @ApiResponse(responseCode = "404", description = "Algorithm or specified Category not found")
    })
    public ResponseEntity<AlgorithmResponse> updateAlgorithm(
            @Parameter(description = "Algorithm slug (e.g. 'bubble-sort')")
            @PathVariable String slug,
            @Valid @RequestBody UpdateAlgorithmRequest request
    ) {
        AlgorithmResponse response =
                algorithmService.updateAlgorithm(slug, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{slug}")
    @Operation(summary = "Delete algorithm (Admin only)", description = "Deletes an algorithm record. Safe cascades remove associated favorites and user progress. Requires ROLE_ADMIN authority.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Algorithm deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Requires ROLE_ADMIN authority"),
            @ApiResponse(responseCode = "404", description = "Algorithm not found")
    })
    public ResponseEntity<Void> deleteAlgorithm(
            @Parameter(description = "Algorithm slug (e.g. 'bubble-sort')")
            @PathVariable String slug
    ) {
        algorithmService.deleteAlgorithm(slug);

        return ResponseEntity.noContent().build();
    }
}
