package com.codeloom.dsa.visualization.dto;

import java.util.List;

public record VisualizationAuditDto(
        int totalAlgorithms,
        int readyCount,
        int missingGeneratorCount,
        int missingRendererCount,
        int missingContractCount,
        int invalidDataCount,
        int customizableCount,
        int fixedDemoCount,
        List<VisualizationAuditItem> items
) {
    public record VisualizationAuditItem(
            String algorithmName,
            String algorithmSlug,
            String categoryName,
            String categorySlug,
            String visualizationType,
            String dataStructureType,
            String inputMode,
            String generatorKey,
            String rendererKey,
            boolean supportsCustomInput,
            String status
    ) {}
}
