package com.codeloom.dsa.problem.entity;

import com.codeloom.dsa.algorithm.entity.AlgorithmCategory;
import com.codeloom.dsa.algorithm.entity.Difficulty;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "problems")
public class Problem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private AlgorithmCategory category;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, unique = true, length = 200)
    private String slug;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Difficulty difficulty;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String constraints;

    @Column(name = "input_format", columnDefinition = "TEXT")
    private String inputFormat;

    @Column(name = "output_format", columnDefinition = "TEXT")
    private String outputFormat;

    @Column(columnDefinition = "TEXT")
    private String hints;

    @Column(name = "solution_explanation", columnDefinition = "TEXT")
    private String solutionExplanation;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    protected Problem() {
    }

    public Problem(
            AlgorithmCategory category,
            String title,
            String slug,
            Difficulty difficulty,
            String description,
            String constraints,
            String inputFormat,
            String outputFormat,
            String hints,
            String solutionExplanation
    ) {
        this.category = category;
        this.title = title;
        this.slug = slug;
        this.difficulty = difficulty;
        this.description = description;
        this.constraints = constraints;
        this.inputFormat = inputFormat;
        this.outputFormat = outputFormat;
        this.hints = hints;
        this.solutionExplanation = solutionExplanation;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public AlgorithmCategory getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }

    public String getSlug() {
        return slug;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public String getDescription() {
        return description;
    }

    public String getConstraints() {
        return constraints;
    }

    public String getInputFormat() {
        return inputFormat;
    }

    public String getOutputFormat() {
        return outputFormat;
    }

    public String getHints() {
        return hints;
    }

    public String getSolutionExplanation() {
        return solutionExplanation;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
