package com.codeloom.dsa.visualization.dto;

import jakarta.validation.constraints.Size;

import java.util.List;

public record VisualizationRequest(
        @Size(max = 50, message = "Input list size must not exceed 50 elements")
        List<Integer> input,

        Integer target,

        GraphVisualizationRequest graph
) {
}
