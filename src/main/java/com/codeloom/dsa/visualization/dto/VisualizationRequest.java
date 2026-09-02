package com.codeloom.dsa.visualization.dto;

import jakarta.validation.constraints.Size;

import java.util.List;

public record VisualizationRequest(
        String type,

        @Size(max = 100, message = "Input list size must not exceed 100 elements")
        List<Integer> input,

        Integer target,

        GraphVisualizationRequest graph,

        List<PointDto> points,

        List<Integer> listInput,

        List<Integer> stackInput,

        List<Integer> queueInput,

        List<String> trieInput,

        List<List<Integer>> matrixInput,

        KnapsackInputDto knapsackInput
) {
    public VisualizationRequest(List<Integer> input) {
        this(null, input, null, null, null, null, null, null, null, null, null);
    }

    public record KnapsackInputDto(
            List<Integer> weights,
            List<Integer> values,
            Integer capacity
    ) {}
}
