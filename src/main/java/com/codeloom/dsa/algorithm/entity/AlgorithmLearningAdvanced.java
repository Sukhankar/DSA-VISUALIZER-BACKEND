package com.codeloom.dsa.algorithm.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "algorithm_learning_advanced")
public class AlgorithmLearningAdvanced {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "learning_content_id", nullable = false)
    private AlgorithmLearningContent learningContent;

    @Column(name = "mathematical_foundation", columnDefinition = "TEXT")
    private String mathematicalFoundation;

    @Column(columnDefinition = "TEXT")
    private String invariant;

    @Column(name = "correctness_proof", columnDefinition = "TEXT")
    private String correctnessProof;

    @Column(columnDefinition = "TEXT")
    private String recurrence;

    @Column(name = "recurrence_solution", columnDefinition = "TEXT")
    private String recurrenceSolution;

    @Column(columnDefinition = "TEXT")
    private String optimization;

    @Column(name = "memory_analysis", columnDefinition = "TEXT")
    private String memoryAnalysis;

    @Column(name = "advanced_tradeoffs", columnDefinition = "TEXT")
    private String advancedTradeoffs;

    @Column(name = "competitive_programming_notes", columnDefinition = "TEXT")
    private String competitiveProgrammingNotes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    protected AlgorithmLearningAdvanced() {
    }

    public AlgorithmLearningAdvanced(
            AlgorithmLearningContent learningContent,
            String mathematicalFoundation,
            String invariant,
            String correctnessProof,
            String recurrence,
            String recurrenceSolution,
            String optimization,
            String memoryAnalysis,
            String advancedTradeoffs,
            String competitiveProgrammingNotes
    ) {
        this.learningContent = learningContent;
        this.mathematicalFoundation = mathematicalFoundation;
        this.invariant = invariant;
        this.correctnessProof = correctnessProof;
        this.recurrence = recurrence;
        this.recurrenceSolution = recurrenceSolution;
        this.optimization = optimization;
        this.memoryAnalysis = memoryAnalysis;
        this.advancedTradeoffs = advancedTradeoffs;
        this.competitiveProgrammingNotes = competitiveProgrammingNotes;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public AlgorithmLearningContent getLearningContent() {
        return learningContent;
    }

    public String getMathematicalFoundation() {
        return mathematicalFoundation;
    }

    public String getInvariant() {
        return invariant;
    }

    public String getCorrectnessProof() {
        return correctnessProof;
    }

    public String getRecurrence() {
        return recurrence;
    }

    public String getRecurrenceSolution() {
        return recurrenceSolution;
    }

    public String getOptimization() {
        return optimization;
    }

    public String getMemoryAnalysis() {
        return memoryAnalysis;
    }

    public String getAdvancedTradeoffs() {
        return advancedTradeoffs;
    }

    public String getCompetitiveProgrammingNotes() {
        return competitiveProgrammingNotes;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
