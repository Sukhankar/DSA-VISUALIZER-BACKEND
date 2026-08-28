package com.codeloom.dsa.practice.entity;

import com.codeloom.dsa.algorithm.entity.AlgorithmCategory;
import com.codeloom.dsa.algorithm.entity.Difficulty;
import com.codeloom.dsa.user.entity.User;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "practice_sessions")
public class PracticeSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode", nullable = false)
    private PracticeMode mode;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SessionStatus status = SessionStatus.IN_PROGRESS;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty")
    private Difficulty difficulty;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private AlgorithmCategory category;

    @Column(name = "time_limit_seconds")
    private Integer timeLimitSeconds;

    @Column(name = "total_problems", nullable = false)
    private int totalProblems = 0;

    @Column(name = "solved_problems", nullable = false)
    private int solvedProblems = 0;

    @Column(name = "score", nullable = false)
    private int score = 0;

    @Column(name = "xp_earned", nullable = false)
    private int xpEarned = 0;

    @Column(name = "started_at", nullable = false, updatable = false)
    private OffsetDateTime startedAt;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    private List<PracticeSessionProblem> problems = new ArrayList<>();

    protected PracticeSession() {}

    public PracticeSession(User user, PracticeMode mode, Difficulty difficulty, AlgorithmCategory category, Integer timeLimitSeconds) {
        this.user = user;
        this.mode = mode;
        this.difficulty = difficulty;
        this.category = category;
        this.timeLimitSeconds = timeLimitSeconds;
        this.status = SessionStatus.IN_PROGRESS;
    }

    @PrePersist
    protected void onCreate() {
        this.startedAt = OffsetDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public PracticeMode getMode() {
        return mode;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public AlgorithmCategory getCategory() {
        return category;
    }

    public Integer getTimeLimitSeconds() {
        return timeLimitSeconds;
    }

    public int getTotalProblems() {
        return totalProblems;
    }

    public void setTotalProblems(int totalProblems) {
        this.totalProblems = totalProblems;
    }

    public int getSolvedProblems() {
        return solvedProblems;
    }

    public void incrementSolvedProblems() {
        this.solvedProblems++;
    }

    public int getScore() {
        return score;
    }

    public void addScore(int score) {
        this.score += score;
    }

    public int getXpEarned() {
        return xpEarned;
    }

    public void addXpEarned(int xp) {
        this.xpEarned += xp;
    }

    public OffsetDateTime getStartedAt() {
        return startedAt;
    }

    public OffsetDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(OffsetDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public List<PracticeSessionProblem> getProblems() {
        return problems;
    }

    public void addProblem(PracticeSessionProblem sessionProblem) {
        this.problems.add(sessionProblem);
        sessionProblem.setSession(this);
        this.totalProblems = this.problems.size();
    }

    public void completeSession() {
        this.status = SessionStatus.COMPLETED;
        this.completedAt = OffsetDateTime.now();
    }

    public void abandonSession() {
        this.status = SessionStatus.ABANDONED;
        this.completedAt = OffsetDateTime.now();
    }
}
