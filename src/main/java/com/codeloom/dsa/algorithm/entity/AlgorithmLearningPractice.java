package com.codeloom.dsa.algorithm.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "algorithm_learning_practice")
public class AlgorithmLearningPractice {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "learning_content_id", nullable = false)
    private AlgorithmLearningContent learningContent;

    @Column(name = "problem_title", nullable = false)
    private String problemTitle;

    @Column(name = "problem_slug", nullable = false)
    private String problemSlug;

    @Column(nullable = false, length = 50)
    private String difficulty;

    @Column(length = 50)
    private String platform = "CodeLoom Arena";

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    protected AlgorithmLearningPractice() {
    }

    public AlgorithmLearningPractice(
            AlgorithmLearningContent learningContent,
            String problemTitle,
            String problemSlug,
            String difficulty,
            String platform,
            Integer displayOrder
    ) {
        this.learningContent = learningContent;
        this.problemTitle = problemTitle;
        this.problemSlug = problemSlug;
        this.difficulty = difficulty;
        this.platform = platform != null ? platform : "CodeLoom Arena";
        this.displayOrder = displayOrder != null ? displayOrder : 0;
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

    public String getProblemTitle() {
        return problemTitle;
    }

    public String getProblemSlug() {
        return problemSlug;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getPlatform() {
        return platform;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
