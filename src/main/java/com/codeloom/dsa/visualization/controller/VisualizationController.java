package com.codeloom.dsa.visualization.controller;

import com.codeloom.dsa.visualization.dto.VisualizationRequest;
import com.codeloom.dsa.visualization.dto.VisualizationResponse;
import com.codeloom.dsa.visualization.service.VisualizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/algorithms")
@Tag(name = "Visualization", description = "Algorithm step-by-step execution visualization generator APIs")
public class VisualizationController {

    private final VisualizationService visualizationService;

    public VisualizationController(VisualizationService visualizationService) {
        this.visualizationService = visualizationService;
    }

    @PostMapping("/{slug}/visualize")
    @Operation(
            summary = "Generate visualization steps for an algorithm",
            description = "Generates a sequence of execution steps (COMPARE, SWAP, VISIT, FOUND, etc.) for array sorting, search, tree, or graph algorithms."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Visualization payload depending on algorithm type",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = VisualizationRequest.class),
                    examples = {
                            @ExampleObject(
                                    name = "Array Sorting Example (e.g. bubble-sort)",
                                    summary = "Input array for sorting algorithms",
                                    value = "{\n  \"input\": [5, 1, 4, 2]\n}"
                            ),
                            @ExampleObject(
                                    name = "Searching Example (e.g. binary-search, linear-search)",
                                    summary = "Input array and target for search algorithms",
                                    value = "{\n  \"input\": [1, 3, 5, 7, 9],\n  \"target\": 7\n}"
                            ),
                            @ExampleObject(
                                    name = "Graph Algorithm Example (e.g. bfs, dfs)",
                                    summary = "Graph nodes, edges, and start node",
                                    value = "{\n  \"graph\": {\n    \"nodes\": [\"A\", \"B\", \"C\", \"D\"],\n    \"edges\": [\n      {\"from\": \"A\", \"to\": \"B\"},\n      {\"from\": \"A\", \"to\": \"C\"},\n      {\"from\": \"B\", \"to\": \"D\"}\n    ],\n    \"startNode\": \"A\"\n  }\n}"
                            )
                    }
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Visualization steps generated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid visualization payload or graph structure"),
            @ApiResponse(responseCode = "404", description = "Algorithm not found with given slug")
    })
    public ResponseEntity<VisualizationResponse> visualize(
            @Parameter(description = "Algorithm slug (e.g. 'bubble-sort', 'binary-search', 'bfs', 'dfs')")
            @PathVariable String slug,
            @Valid @RequestBody VisualizationRequest request
    ) {
        VisualizationResponse response = visualizationService.generateVisualization(slug, request);
        return ResponseEntity.ok(response);
    }
}
