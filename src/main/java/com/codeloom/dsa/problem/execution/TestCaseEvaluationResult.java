package com.codeloom.dsa.problem.execution;

public record TestCaseEvaluationResult(
        int testCaseNumber,
        String inputData,
        String expectedOutput,
        String actualOutput,
        boolean passed,
        boolean isHidden,
        String errorMessage
) {
}
