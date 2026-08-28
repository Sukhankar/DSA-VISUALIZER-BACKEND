package com.codeloom.dsa.problem.entity;

import com.codeloom.dsa.user.entity.User;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "problem_submissions")
public class ProblemSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    @Column(nullable = false, length = 20)
    private String language;

    @Column(name = "source_code", nullable = false, columnDefinition = "TEXT")
    private String sourceCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SubmissionStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SubmissionVerdict verdict;

    @Column(name = "execution_time_ms")
    private Integer executionTimeMs;

    @Column(name = "memory_used_kb")
    private Integer memoryUsedKb;

    @Column(name = "total_tests", nullable = false)
    private int totalTests;

    @Column(name = "passed_tests", nullable = false)
    private int passedTests;

    @Column(name = "submitted_at", nullable = false, updatable = false)
    private OffsetDateTime submittedAt;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    protected ProblemSubmission() {
    }

    public ProblemSubmission(
            User user,
            Problem problem,
            String language,
            String sourceCode,
            SubmissionStatus status,
            SubmissionVerdict verdict,
            Integer executionTimeMs,
            Integer memoryUsedKb,
            int totalTests,
            int passedTests
    ) {
        this.user = user;
        this.problem = problem;
        this.language = language;
        this.sourceCode = sourceCode;
        this.status = status;
        this.verdict = verdict;
        this.executionTimeMs = executionTimeMs;
        this.memoryUsedKb = memoryUsedKb;
        this.totalTests = totalTests;
        this.passedTests = passedTests;
    }

    @PrePersist
    protected void onCreate() {
        this.submittedAt = OffsetDateTime.now();
        if (this.completedAt == null && (this.status == SubmissionStatus.COMPLETED || this.status == SubmissionStatus.FAILED)) {
            this.completedAt = OffsetDateTime.now();
        }
    }

    public void updateResult(SubmissionStatus status, SubmissionVerdict verdict, Integer executionTimeMs, Integer memoryUsedKb, int totalTests, int passedTests) {
        this.status = status;
        this.verdict = verdict;
        this.executionTimeMs = executionTimeMs;
        this.memoryUsedKb = memoryUsedKb;
        this.totalTests = totalTests;
        this.passedTests = passedTests;
        this.completedAt = OffsetDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Problem getProblem() {
        return problem;
    }

    public String getLanguage() {
        return language;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public SubmissionStatus getStatus() {
        return status;
    }

    public SubmissionVerdict getVerdict() {
        return verdict;
    }

    public Integer getExecutionTimeMs() {
        return executionTimeMs;
    }

    public Integer getMemoryUsedKb() {
        return memoryUsedKb;
    }

    public int getTotalTests() {
        return totalTests;
    }

    public int getPassedTests() {
        return passedTests;
    }

    public OffsetDateTime getSubmittedAt() {
        return submittedAt;
    }

    public OffsetDateTime getCompletedAt() {
        return completedAt;
    }
}
