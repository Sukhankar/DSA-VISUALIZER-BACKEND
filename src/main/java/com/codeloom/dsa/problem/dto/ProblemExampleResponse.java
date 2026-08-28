package com.codeloom.dsa.problem.dto;

public record ProblemExampleResponse(
        int exampleNumber,
        String inputData,
        String outputData,
        String explanation
) {
}
