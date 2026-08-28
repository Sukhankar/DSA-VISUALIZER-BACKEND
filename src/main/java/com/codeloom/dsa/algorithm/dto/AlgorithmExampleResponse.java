package com.codeloom.dsa.algorithm.dto;

public record AlgorithmExampleResponse(
        int exampleNumber,
        String title,
        String inputData,
        String outputData,
        String explanation
) {
}
