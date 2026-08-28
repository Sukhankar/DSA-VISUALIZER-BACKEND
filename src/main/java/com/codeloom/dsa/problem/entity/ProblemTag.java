package com.codeloom.dsa.problem.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "problem_tags")
public class ProblemTag {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    @Column(name = "tag_name", nullable = false, length = 50)
    private String tagName;

    protected ProblemTag() {
    }

    public ProblemTag(Problem problem, String tagName) {
        this.problem = problem;
        this.tagName = tagName;
    }

    public UUID getId() {
        return id;
    }

    public Problem getProblem() {
        return problem;
    }

    public String getTagName() {
        return tagName;
    }
}
