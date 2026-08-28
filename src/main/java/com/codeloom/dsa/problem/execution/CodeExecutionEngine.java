package com.codeloom.dsa.problem.execution;

import com.codeloom.dsa.problem.entity.ProblemTestCase;

import java.util.List;

public interface CodeExecutionEngine {
    ExecutionSummary evaluate(String language, String sourceCode, List<ProblemTestCase> testCases);
}
