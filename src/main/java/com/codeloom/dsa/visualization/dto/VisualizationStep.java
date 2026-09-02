package com.codeloom.dsa.visualization.dto;

import com.codeloom.dsa.visualization.entity.ActionType;

import java.util.List;
import java.util.Map;

public record VisualizationStep(
        int step,
        ActionType action,
        List<Object> indices,
        List<Object> array,
        String currentNode,
        List<String> visitedNodes,
        List<String> frontier,
        GraphStateSnapshot graphState,
        String message,
        Map<String, Integer> codeLineMap,
        String beginnerExplanation,
        String advancedExplanation,
        String whyMessage,
        String complexityImpact,
        Map<String, Object> customState
) {
    // Primary 14-arg Constructor
    public VisualizationStep(
            int step,
            ActionType action,
            List<Object> indices,
            List<Object> array,
            String currentNode,
            List<String> visitedNodes,
            List<String> frontier,
            String message,
            Map<String, Integer> codeLineMap,
            String beginnerExplanation,
            String advancedExplanation,
            String whyMessage,
            String complexityImpact,
            Map<String, Object> customState
    ) {
        this(step, action, indices, array, currentNode, visitedNodes, frontier, null, message, codeLineMap, beginnerExplanation, advancedExplanation, whyMessage, complexityImpact, customState);
    }

    // 1. Legacy array constructor (5 args)
    public VisualizationStep(int step, ActionType action, List<?> indices, List<?> array, String message) {
        this(step, action, (List<Object>) indices, (List<Object>) array, null, null, null, null, message, Map.of(), null, null, null, null, null);
    }

    // 2. Legacy graph constructor (8 args)
    public VisualizationStep(
            int step,
            ActionType action,
            List<?> indices,
            List<?> array,
            String currentNode,
            List<String> visitedNodes,
            List<String> frontier,
            String message
    ) {
        this(step, action, (List<Object>) indices, (List<Object>) array, currentNode, visitedNodes, frontier, null, message, Map.of(), null, null, null, null, null);
    }

    // 3. Legacy array constructor with explanations (10 args)
    public VisualizationStep(
            int step,
            ActionType action,
            List<?> indices,
            List<?> array,
            String message,
            Map<String, Integer> codeLineMap,
            String beginnerExplanation,
            String advancedExplanation,
            String whyMessage,
            String complexityImpact
    ) {
        this(step, action, (List<Object>) indices, (List<Object>) array, null, null, null, null, message, codeLineMap, beginnerExplanation, advancedExplanation, whyMessage, complexityImpact, null);
    }

    // 4. Legacy graph constructor with explanations (13 args)
    public VisualizationStep(
            int step,
            ActionType action,
            List<?> indices,
            List<?> array,
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
        this(step, action, (List<Object>) indices, (List<Object>) array, currentNode, visitedNodes, frontier, null, message, codeLineMap, beginnerExplanation, advancedExplanation, whyMessage, complexityImpact, null);
    }

    // 5. Structure-specific / Geometry constructor (9 args)
    public VisualizationStep(
            int step,
            ActionType action,
            String message,
            Map<String, Integer> codeLineMap,
            String beginnerExplanation,
            String advancedExplanation,
            String whyMessage,
            String complexityImpact,
            Map<String, Object> customState
    ) {
        this(step, action, List.of(), List.of(), null, null, null, null, message, codeLineMap, beginnerExplanation, advancedExplanation, whyMessage, complexityImpact, customState);
    }

    // 6. Graph-specific constructor with GraphStateSnapshot (10 args)
    public VisualizationStep(
            int step,
            ActionType action,
            GraphStateSnapshot graphState,
            String message,
            Map<String, Integer> codeLineMap,
            String beginnerExplanation,
            String advancedExplanation,
            String whyMessage,
            String complexityImpact,
            Map<String, Object> customState
    ) {
        this(step, action, List.of(), List.of(), graphState != null ? graphState.currentNodeId() : null, graphState != null ? graphState.visitedNodeIds() : List.of(), graphState != null ? graphState.queuedNodeIds() : List.of(), graphState, message, codeLineMap, beginnerExplanation, advancedExplanation, whyMessage, complexityImpact, customState);
    }
}
