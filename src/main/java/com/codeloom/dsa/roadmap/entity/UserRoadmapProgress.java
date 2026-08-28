package com.codeloom.dsa.roadmap.entity;

import com.codeloom.dsa.user.entity.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_roadmap_progress", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "module_id"})
})
public class UserRoadmapProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    private RoadmapModule module;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private RoadmapStatus status = RoadmapStatus.LOCKED;

    @Column(name = "completion_percentage")
    private Integer completionPercentage = 0;

    @Column(name = "algorithm_progress")
    private Integer algorithmProgress = 0;

    @Column(name = "problem_progress")
    private Integer problemProgress = 0;

    @Column(name = "overall_progress")
    private Integer overallProgress = 0;

    @Column(name = "unlocked_at")
    private LocalDateTime unlockedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "last_activity_at")
    private LocalDateTime lastActivityAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();


    public UserRoadmapProgress() {}

    public UserRoadmapProgress(User user, RoadmapModule module, RoadmapStatus status) {
        this.user = user;
        this.module = module;
        this.status = status;
        if (status != RoadmapStatus.LOCKED) {
            this.unlockedAt = LocalDateTime.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public RoadmapModule getModule() {
        return module;
    }

    public void setModule(RoadmapModule module) {
        this.module = module;
    }

    public RoadmapStatus getStatus() {
        return status;
    }

    public void setStatus(RoadmapStatus status) {
        this.status = status;
    }

    public Integer getCompletionPercentage() {
        return completionPercentage;
    }

    public void setCompletionPercentage(Integer completionPercentage) {
        this.completionPercentage = completionPercentage;
    }

    public Integer getAlgorithmProgress() {
        return algorithmProgress;
    }

    public void setAlgorithmProgress(Integer algorithmProgress) {
        this.algorithmProgress = algorithmProgress;
    }

    public Integer getProblemProgress() {
        return problemProgress;
    }

    public void setProblemProgress(Integer problemProgress) {
        this.problemProgress = problemProgress;
    }

    public Integer getOverallProgress() {
        return overallProgress;
    }

    public void setOverallProgress(Integer overallProgress) {
        this.overallProgress = overallProgress;
    }

    public LocalDateTime getUnlockedAt() {
        return unlockedAt;
    }

    public void setUnlockedAt(LocalDateTime unlockedAt) {
        this.unlockedAt = unlockedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getLastActivityAt() {
        return lastActivityAt;
    }

    public void setLastActivityAt(LocalDateTime lastActivityAt) {
        this.lastActivityAt = lastActivityAt;
    }


    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
