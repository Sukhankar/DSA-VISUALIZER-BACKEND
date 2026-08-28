package com.codeloom.dsa.practice.entity;

import com.codeloom.dsa.problem.entity.Problem;
import com.codeloom.dsa.problem.entity.ProblemSubmission;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "practice_session_problems", uniqueConstraints = {
        @UniqueConstraint(name = "uk_session_problem_order", columnNames = {"session_id", "order_index"})
})
public class PracticeSessionProblem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private PracticeSession session;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    @Column(name = "order_index", nullable = false)
    private int orderIndex;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SessionProblemStatus status = SessionProblemStatus.UNATTEMPTED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id")
    private ProblemSubmission submission;

    @Column(name = "solved_at")
    private OffsetDateTime solvedAt;

    protected PracticeSessionProblem() {}

    public PracticeSessionProblem(Problem problem, int orderIndex) {
        this.problem = problem;
        this.orderIndex = orderIndex;
        this.status = SessionProblemStatus.UNATTEMPTED;
    }

    public UUID getId() {
        return id;
    }

    public PracticeSession getSession() {
        return session;
    }

    public void setSession(PracticeSession session) {
        this.session = session;
    }

    public Problem getProblem() {
        return problem;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public SessionProblemStatus getStatus() {
        return status;
    }

    public void setStatus(SessionProblemStatus status) {
        this.status = status;
    }

    public ProblemSubmission getSubmission() {
        return submission;
    }

    public void setSubmission(ProblemSubmission submission) {
        this.submission = submission;
    }

    public OffsetDateTime getSolvedAt() {
        return solvedAt;
    }

    public void markSolved(ProblemSubmission submission) {
        this.status = SessionProblemStatus.SOLVED;
        this.submission = submission;
        this.solvedAt = OffsetDateTime.now();
    }
}
