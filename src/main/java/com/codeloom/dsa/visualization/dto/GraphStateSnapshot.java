package com.codeloom.dsa.visualization.dto;

import java.util.List;
import java.util.Map;

public record GraphStateSnapshot(
        List<GraphNodeDto> nodes,
        List<GraphEdgeDto> edges,
        Boolean directed,
        Boolean weighted,
        List<String> activeNodeIds,
        List<String> visitedNodeIds,
        List<String> activeEdgeIds,
        List<String> traversedEdgeIds,
        List<String> queuedNodeIds,
        List<String> stackNodeIds,
        String currentNodeId,
        String sourceNodeId,
        String targetNodeId,
        Map<String, String> shortestDistances,
        Map<String, String> predecessors,
        List<String> mstEdgeIds,
        List<String> rejectedEdgeIds,
        String candidateEdgeId,
        List<String> pathNodeIds,
        List<String> pathEdgeIds,
        Double currentWeight,
        Double totalWeight,
        String explanation
) {
    public GraphStateSnapshot {
        if (nodes == null) nodes = List.of();
        if (edges == null) edges = List.of();
        if (activeNodeIds == null) activeNodeIds = List.of();
        if (visitedNodeIds == null) visitedNodeIds = List.of();
        if (activeEdgeIds == null) activeEdgeIds = List.of();
        if (traversedEdgeIds == null) traversedEdgeIds = List.of();
        if (queuedNodeIds == null) queuedNodeIds = List.of();
        if (stackNodeIds == null) stackNodeIds = List.of();
        if (mstEdgeIds == null) mstEdgeIds = List.of();
        if (rejectedEdgeIds == null) rejectedEdgeIds = List.of();
        if (pathNodeIds == null) pathNodeIds = List.of();
        if (pathEdgeIds == null) pathEdgeIds = List.of();
        if (shortestDistances == null) shortestDistances = Map.of();
        if (predecessors == null) predecessors = Map.of();
    }
}
