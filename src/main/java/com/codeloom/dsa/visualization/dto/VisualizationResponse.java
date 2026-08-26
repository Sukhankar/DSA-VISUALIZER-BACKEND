package com.codeloom.dsa.visualization.dto;

import com.codeloom.dsa.visualization.entity.VisualizationType;

import java.util.List;

public record VisualizationResponse(
        String algorithm,
        VisualizationType visualizationType,
        List<VisualizationStep> steps
) {
}
