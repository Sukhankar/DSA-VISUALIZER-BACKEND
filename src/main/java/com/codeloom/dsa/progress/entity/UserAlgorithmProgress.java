package com.codeloom.dsa.progress.entity;

import com.codeloom.dsa.algorithm.entity.Algorithm;
import com.codeloom.dsa.user.entity.User;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "user_algorithm_progress",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_algorithm_progress_user_algorithm",
                        columnNames = {"user_id", "algorithm_id"}
                )
        }
)
public class UserAlgorithmProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "algorithm_id", nullable = false)
    private Algorithm algorithm;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProgressStatus status = ProgressStatus.NOT_STARTED;

    @Column(name = "progress_percentage", nullable = false)
    private int progressPercentage = 0;

    @Column(name = "last_step")
    private Integer lastStep;

    @Column(name = "started_at")
    private OffsetDateTime startedAt;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected UserAlgorithmProgress() {
    }

    public UserAlgorithmProgress(User user, Algorithm algorithm) {
        this.user = user;
        this.algorithm = algorithm;
        this.status = ProgressStatus.NOT_STARTED;
        this.progressPercentage = 0;
    }

    @PrePersist
    protected void onCreate() {
        this.updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public ProgressStatus getStatus() {
        return status;
    }

    public int getProgressPercentage() {
        return progressPercentage;
    }

    public Integer getLastStep() {
        return lastStep;
    }

    public OffsetDateTime getStartedAt() {
        return startedAt;
    }

    public OffsetDateTime getCompletedAt() {
        return completedAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void start() {
        if (this.status == ProgressStatus.NOT_STARTED) {
            this.status = ProgressStatus.IN_PROGRESS;
        }
        if (this.startedAt == null) {
            this.startedAt = OffsetDateTime.now();
        }
    }

    public void updateProgress(int percentage, Integer lastStep) {
        if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException("Progress percentage must be between 0 and 100");
        }

        this.progressPercentage = percentage;
        if (lastStep != null) {
            this.lastStep = lastStep;
        }

        if (percentage > 0 && this.status == ProgressStatus.NOT_STARTED) {
            this.status = ProgressStatus.IN_PROGRESS;
            if (this.startedAt == null) {
                this.startedAt = OffsetDateTime.now();
            }
        }
    }

    public void complete() {
        this.status = ProgressStatus.COMPLETED;
        this.progressPercentage = 100;
        if (this.startedAt == null) {
            this.startedAt = OffsetDateTime.now();
        }
        if (this.completedAt == null) {
            this.completedAt = OffsetDateTime.now();
        }
    }
}
