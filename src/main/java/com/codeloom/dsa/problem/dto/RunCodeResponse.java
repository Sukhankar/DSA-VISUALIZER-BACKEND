package com.codeloom.dsa.problem.dto;

import com.codeloom.dsa.problem.entity.SubmissionVerdict;
import com.codeloom.dsa.problem.execution.TestCaseEvaluationResult;

import java.util.List;

public record RunCodeResponse(
        boolean passed,
        SubmissionVerdict verdict,
        int totalTests,
        int passedTests,
        int executionTimeMs,
        int memoryUsedKb,
        List<TestCaseEvaluationResult> testResults
) {
}
