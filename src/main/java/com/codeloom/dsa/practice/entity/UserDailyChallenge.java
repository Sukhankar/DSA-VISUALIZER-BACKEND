package com.codeloom.dsa.practice.entity;

import com.codeloom.dsa.problem.entity.ProblemSubmission;
import com.codeloom.dsa.user.entity.User;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_daily_challenges", uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_daily_challenge", columnNames = {"user_id", "daily_challenge_id"})
})
public class UserDailyChallenge {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "daily_challenge_id", nullable = false)
    private DailyChallenge dailyChallenge;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SessionProblemStatus status = SessionProblemStatus.UNATTEMPTED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id")
    private ProblemSubmission submission;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    protected UserDailyChallenge() {}

    public UserDailyChallenge(User user, DailyChallenge dailyChallenge) {
        this.user = user;
        this.dailyChallenge = dailyChallenge;
        this.status = SessionProblemStatus.UNATTEMPTED;
    }

    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public DailyChallenge getDailyChallenge() {
        return dailyChallenge;
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

    public OffsetDateTime getCompletedAt() {
        return completedAt;
    }

    public void markCompleted(ProblemSubmission submission) {
        this.status = SessionProblemStatus.SOLVED;
        this.submission = submission;
        this.completedAt = OffsetDateTime.now();
    }
}
