package com.codeloom.dsa.visualization.controller;

import com.codeloom.dsa.visualization.dto.VisualizationAuditDto;
import com.codeloom.dsa.visualization.dto.VisualizationContractDto;
import com.codeloom.dsa.visualization.service.VisualizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Visualization Contract & Audit", description = "Contract retrieval and live audit endpoints")
public class VisualizationContractController {

    private final VisualizationService visualizationService;

    public VisualizationContractController(VisualizationService visualizationService) {
        this.visualizationService = visualizationService;
    }

    @GetMapping("/algorithms/{slug}/visualization-contract")
    @Operation(summary = "Get visualization contract for an algorithm")
    public ResponseEntity<VisualizationContractDto> getVisualizationContract(
            @Parameter(description = "Algorithm slug (e.g. 'bubble-sort')")
            @PathVariable String slug
    ) {
        VisualizationContractDto contract = visualizationService.getVisualizationContract(slug);
        return ResponseEntity.ok(contract);
    }

    @GetMapping("/admin/visualizations/audit")
    @Operation(summary = "Get live visualization audit report for all algorithms")
    public ResponseEntity<VisualizationAuditDto> getAuditReport() {
        VisualizationAuditDto auditReport = visualizationService.getAuditReport();
        return ResponseEntity.ok(auditReport);
    }
}
