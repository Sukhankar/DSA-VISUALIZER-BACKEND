package com.codeloom.dsa.visualization.dto;

import com.codeloom.dsa.visualization.entity.ActionType;

import java.util.List;

public record VisualizationStep(
        int step,
        ActionType action,
        List<Integer> indices,
        List<Integer> array,
        String message
) {
}
