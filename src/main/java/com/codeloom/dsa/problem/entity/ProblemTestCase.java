package com.codeloom.dsa.problem.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "problem_test_cases")
public class ProblemTestCase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    @Column(name = "test_case_number", nullable = false)
    private int testCaseNumber;

    @Column(name = "input_data", nullable = false, columnDefinition = "TEXT")
    private String inputData;

    @Column(name = "output_data", nullable = false, columnDefinition = "TEXT")
    private String outputData;

    @Column(name = "is_hidden", nullable = false)
    private boolean isHidden;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    protected ProblemTestCase() {
    }

    public ProblemTestCase(
            Problem problem,
            int testCaseNumber,
            String inputData,
            String outputData,
            boolean isHidden
    ) {
        this.problem = problem;
        this.testCaseNumber = testCaseNumber;
        this.inputData = inputData;
        this.outputData = outputData;
        this.isHidden = isHidden;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public Problem getProblem() {
        return problem;
    }

    public int getTestCaseNumber() {
        return testCaseNumber;
    }

    public String getInputData() {
        return inputData;
    }

    public String getOutputData() {
        return outputData;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
