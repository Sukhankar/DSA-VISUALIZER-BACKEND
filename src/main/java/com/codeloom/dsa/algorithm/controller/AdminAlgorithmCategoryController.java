package com.codeloom.dsa.algorithm.controller;

import com.codeloom.dsa.algorithm.dto.AlgorithmCategoryResponse;
import com.codeloom.dsa.algorithm.dto.CreateAlgorithmCategoryRequest;
import com.codeloom.dsa.algorithm.dto.UpdateAlgorithmCategoryRequest;
import com.codeloom.dsa.algorithm.service.AlgorithmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/admin/categories")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Operations", description = "Admin CRUD endpoints for category and algorithm management. Requires ROLE_ADMIN authority.")
@SecurityRequirement(name = "bearerAuth")
public class AdminAlgorithmCategoryController {

    private final AlgorithmService algorithmService;

    public AdminAlgorithmCategoryController(AlgorithmService algorithmService) {
        this.algorithmService = algorithmService;
    }

    @PostMapping
    @Operation(summary = "Create algorithm category (Admin only)", description = "Creates a new category. Requires ROLE_ADMIN authority.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Category created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failure or category name/slug already exists"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Requires ROLE_ADMIN authority")
    })
    public ResponseEntity<AlgorithmCategoryResponse> createCategory(
            @Valid @RequestBody CreateAlgorithmCategoryRequest request
    ) {
        AlgorithmCategoryResponse response = algorithmService.createCategory(request);
        return ResponseEntity
                .created(URI.create("/api/v1/categories/" + response.slug()))
                .body(response);
    }

    @PutMapping("/{slug}")
    @Operation(summary = "Update algorithm category (Admin only)", description = "Updates an existing category. Requires ROLE_ADMIN authority.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failure or duplicate name/slug"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Requires ROLE_ADMIN authority"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<AlgorithmCategoryResponse> updateCategory(
            @Parameter(description = "Category slug (e.g. 'sorting')")
            @PathVariable String slug,
            @Valid @RequestBody UpdateAlgorithmCategoryRequest request
    ) {
        AlgorithmCategoryResponse response = algorithmService.updateCategory(slug, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{slug}")
    @Operation(summary = "Delete algorithm category (Admin only)", description = "Deletes an empty category. Restricted if category contains algorithms. Requires ROLE_ADMIN authority.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Category deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Cannot delete category because it contains algorithms"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Requires ROLE_ADMIN authority"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<Void> deleteCategory(
            @Parameter(description = "Category slug (e.g. 'sorting')")
            @PathVariable String slug
    ) {
        algorithmService.deleteCategory(slug);
        return ResponseEntity.noContent().build();
    }
}
