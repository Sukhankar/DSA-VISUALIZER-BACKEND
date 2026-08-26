package com.codeloom.dsa.visualization.controller;

import com.codeloom.dsa.visualization.dto.VisualizationRequest;
import com.codeloom.dsa.visualization.dto.VisualizationResponse;
import com.codeloom.dsa.visualization.service.VisualizationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/algorithms")
public class VisualizationController {

    private final VisualizationService visualizationService;

    public VisualizationController(VisualizationService visualizationService) {
        this.visualizationService = visualizationService;
    }

    @PostMapping("/{slug}/visualize")
    public ResponseEntity<VisualizationResponse> visualize(
            @PathVariable String slug,
            @Valid @RequestBody VisualizationRequest request
    ) {
        VisualizationResponse response = visualizationService.generateVisualization(slug, request);
        return ResponseEntity.ok(response);
    }
}
