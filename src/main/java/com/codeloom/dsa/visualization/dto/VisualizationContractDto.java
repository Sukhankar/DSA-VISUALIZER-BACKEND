package com.codeloom.dsa.visualization.dto;

public record VisualizationContractDto(
        String algorithmSlug,
        String visualizationType,
        String dataStructureType,
        String inputMode,
        String inputSchema,
        String sampleInput,
        String generatorKey,
        String rendererKey,
        String stepSchema,
        String visualizationConfig,
        String learningVisualizationDescription,
        boolean supportsCustomInput,
        int maxInputSize,
        String status
) {
}
