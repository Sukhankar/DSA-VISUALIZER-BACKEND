package com.codeloom.dsa.algorithm.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "algorithm_examples")
public class AlgorithmExample {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "algorithm_id", nullable = false)
    private Algorithm algorithm;

    @Column(name = "example_number", nullable = false)
    private int exampleNumber;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(name = "input_data", nullable = false, columnDefinition = "TEXT")
    private String inputData;

    @Column(name = "output_data", nullable = false, columnDefinition = "TEXT")
    private String outputData;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    protected AlgorithmExample() {
    }

    public AlgorithmExample(
            Algorithm algorithm,
            int exampleNumber,
            String title,
            String inputData,
            String outputData,
            String explanation
    ) {
        this.algorithm = algorithm;
        this.exampleNumber = exampleNumber;
        this.title = title;
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

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public int getExampleNumber() {
        return exampleNumber;
    }

    public String getTitle() {
        return title;
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
