package com.codeloom.dsa.visualization.dto;

import java.util.List;

public record GraphVisualizationRequest(
        List<GraphNodeDto> nodes,
        List<GraphEdgeDto> edges,
        Boolean directed,
        Boolean weighted,
        String startNode,
        String targetNode
) {
    public GraphVisualizationRequest(List<String> rawNodes, List<GraphEdgeDto> edges, String startNode) {
        this(
                rawNodes == null ? List.of() : rawNodes.stream().map(n -> new GraphNodeDto(n, n)).toList(),
                edges,
                false,
                false,
                startNode,
                null
        );
    }
}
