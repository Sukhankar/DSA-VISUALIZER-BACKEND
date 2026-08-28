package com.codeloom.dsa.problem.execution;

import com.codeloom.dsa.problem.entity.SubmissionVerdict;

import java.util.List;

public record ExecutionSummary(
        SubmissionVerdict verdict,
        int totalTests,
        int passedTests,
        int executionTimeMs,
        int memoryUsedKb,
        List<TestCaseEvaluationResult> testCaseResults
) {
}
