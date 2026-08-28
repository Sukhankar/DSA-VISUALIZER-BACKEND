package com.codeloom.dsa.algorithm.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "algorithms")
public class Algorithm {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "category_id",
            nullable = false
    )
    private AlgorithmCategory category;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(
            nullable = false,
            unique = true,
            length = 150
    )
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String overview;

    @Column(name = "when_to_use", columnDefinition = "TEXT")
    private String whenToUse;

    @Column(columnDefinition = "TEXT")
    private String advantages;

    @Column(columnDefinition = "TEXT")
    private String limitations;

    @Column(columnDefinition = "TEXT")
    private String constraints;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Difficulty difficulty;

    @Column(name = "time_complexity", length = 100)
    private String timeComplexity;

    @Column(name = "space_complexity", length = 100)
    private String spaceComplexity;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private OffsetDateTime createdAt;

    protected Algorithm() {
    }

    public Algorithm(
            AlgorithmCategory category,
            String name,
            String slug,
            String description,
            Difficulty difficulty,
            String timeComplexity,
            String spaceComplexity
    ) {
        this.category = category;
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.difficulty = difficulty;
        this.timeComplexity = timeComplexity;
        this.spaceComplexity = spaceComplexity;
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

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    public String getDescription() {
        return description;
    }

    public String getOverview() {
        return overview;
    }

    public String getWhenToUse() {
        return whenToUse;
    }

    public String getAdvantages() {
        return advantages;
    }

    public String getLimitations() {
        return limitations;
    }

    public String getConstraints() {
        return constraints;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public String getTimeComplexity() {
        return timeComplexity;
    }

    public String getSpaceComplexity() {
        return spaceComplexity;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void update(
            AlgorithmCategory category,
            String name,
            String slug,
            String description,
            Difficulty difficulty,
            String timeComplexity,
            String spaceComplexity
    ) {
        this.category = category;
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.difficulty = difficulty;
        this.timeComplexity = timeComplexity;
        this.spaceComplexity = spaceComplexity;
    }

    public void setRichDetails(
            String overview,
            String whenToUse,
            String advantages,
            String limitations,
            String constraints
    ) {
        this.overview = overview;
        this.whenToUse = whenToUse;
        this.advantages = advantages;
        this.limitations = limitations;
        this.constraints = constraints;
    }
}