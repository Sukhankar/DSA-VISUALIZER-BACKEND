package com.codeloom.dsa.problem.entity;

import com.codeloom.dsa.algorithm.entity.Algorithm;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "problem_related_algorithms")
public class ProblemRelatedAlgorithm {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "algorithm_id", nullable = false)
    private Algorithm algorithm;

    protected ProblemRelatedAlgorithm() {
    }

    public ProblemRelatedAlgorithm(Problem problem, Algorithm algorithm) {
        this.problem = problem;
        this.algorithm = algorithm;
    }

    public UUID getId() {
        return id;
    }

    public Problem getProblem() {
        return problem;
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }
}
