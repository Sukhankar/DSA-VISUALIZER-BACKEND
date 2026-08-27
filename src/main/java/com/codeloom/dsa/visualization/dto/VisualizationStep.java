package com.codeloom.dsa.visualization.dto;

import com.codeloom.dsa.visualization.entity.ActionType;

import java.util.List;

public record VisualizationStep(
        int step,
        ActionType action,
        List<Integer> indices,
        List<Integer> array,
        String currentNode,
        List<String> visitedNodes,
        List<String> frontier,
        String message
) {
    // Convenience constructor for array-based visualization steps
    public VisualizationStep(int step, ActionType action, List<Integer> indices, List<Integer> array, String message) {
        this(step, action, indices, array, null, null, null, message);
    }
}
