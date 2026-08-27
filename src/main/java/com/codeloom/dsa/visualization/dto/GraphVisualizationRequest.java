package com.codeloom.dsa.visualization.dto;

import java.util.List;

public record GraphVisualizationRequest(
        List<String> nodes,
        List<GraphEdgeDto> edges,
        String startNode
) {
}
