package com.codeloom.dsa.practice.entity;

import com.codeloom.dsa.problem.entity.Problem;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "daily_challenges")
public class DailyChallenge {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "challenge_date", nullable = false, unique = true)
    private LocalDate challengeDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    @Column(name = "bonus_xp", nullable = false)
    private int bonusXp = 100;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    protected DailyChallenge() {}

    public DailyChallenge(LocalDate challengeDate, Problem problem, int bonusXp) {
        this.challengeDate = challengeDate;
        this.problem = problem;
        this.bonusXp = bonusXp;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public LocalDate getChallengeDate() {
        return challengeDate;
    }

    public Problem getProblem() {
        return problem;
    }

    public int getBonusXp() {
        return bonusXp;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
