package com.codeloom.dsa.visualization.dto;

import com.codeloom.dsa.visualization.entity.ActionType;

import java.util.List;
import java.util.Map;

public record VisualizationStep(
        int step,
        ActionType action,
        List<Integer> indices,
        List<Integer> array,
        String currentNode,
        List<String> visitedNodes,
        List<String> frontier,
        String message,
        Map<String, Integer> codeLineMap,
        String beginnerExplanation,
        String advancedExplanation,
        String whyMessage,
        String complexityImpact
) {
    // 1. Convenience constructor for legacy array-based visualization steps
    public VisualizationStep(int step, ActionType action, List<Integer> indices, List<Integer> array, String message) {
        this(step, action, indices, array, null, null, null, message, Map.of(), null, null, null, null);
    }

    // 2. Convenience constructor for graph-based visualization steps (BFS, DFS, Dijkstra, etc.)
    public VisualizationStep(
            int step,
            ActionType action,
            List<Integer> indices,
            List<Integer> array,
            String currentNode,
            List<String> visitedNodes,
            List<String> frontier,
            String message
    ) {
        this(step, action, indices, array, currentNode, visitedNodes, frontier, message, Map.of(), null, null, null, null);
    }

    // 3. Convenience constructor for array-based visualization steps with line-sync and explanations
    public VisualizationStep(
            int step,
            ActionType action,
            List<Integer> indices,
            List<Integer> array,
            String message,
            Map<String, Integer> codeLineMap,
            String beginnerExplanation,
            String advancedExplanation,
            String whyMessage,
            String complexityImpact
    ) {
        this(step, action, indices, array, null, null, null, message, codeLineMap, beginnerExplanation, advancedExplanation, whyMessage, complexityImpact);
    }
}
