package com.codeloom.dsa.algorithm.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "algorithm_related")
public class RelatedAlgorithm {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "algorithm_id", nullable = false)
    private Algorithm algorithm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_algorithm_id", nullable = false)
    private Algorithm relatedAlgorithm;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    protected RelatedAlgorithm() {
    }

    public RelatedAlgorithm(Algorithm algorithm, Algorithm relatedAlgorithm) {
        this.algorithm = algorithm;
        this.relatedAlgorithm = relatedAlgorithm;
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

    public Algorithm getRelatedAlgorithm() {
        return relatedAlgorithm;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
