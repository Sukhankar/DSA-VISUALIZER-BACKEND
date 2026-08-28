package com.codeloom.dsa.problem.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "problem_examples")
public class ProblemExample {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    @Column(name = "example_number", nullable = false)
    private int exampleNumber;

    @Column(name = "input_data", nullable = false, columnDefinition = "TEXT")
    private String inputData;

    @Column(name = "output_data", nullable = false, columnDefinition = "TEXT")
    private String outputData;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    protected ProblemExample() {
    }

    public ProblemExample(
            Problem problem,
            int exampleNumber,
            String inputData,
            String outputData,
            String explanation
    ) {
        this.problem = problem;
        this.exampleNumber = exampleNumber;
        this.inputData = inputData;
        this.outputData = outputData;
        this.explanation = explanation;
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

    public int getExampleNumber() {
        return exampleNumber;
    }

    public String getInputData() {
        return inputData;
    }

    public String getOutputData() {
        return outputData;
    }

    public String getExplanation() {
        return explanation;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
