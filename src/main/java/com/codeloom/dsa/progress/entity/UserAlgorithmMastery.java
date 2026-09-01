package com.codeloom.dsa.progress.entity;

import com.codeloom.dsa.algorithm.entity.Algorithm;
import com.codeloom.dsa.user.entity.User;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "user_algorithm_mastery",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_algorithm_mastery", columnNames = {"user_id", "algorithm_id"})
        }
)
public class UserAlgorithmMastery {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "algorithm_id", nullable = false)
    private Algorithm algorithm;

    @Column(nullable = false)
    private boolean mastered = false;

    @Column(name = "mastered_at")
    private OffsetDateTime masteredAt;

    @Column(name = "xp_awarded", nullable = false)
    private int xpAwarded = 100;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected UserAlgorithmMastery() {}

    public UserAlgorithmMastery(User user, Algorithm algorithm, boolean mastered) {
        this.user = user;
        this.algorithm = algorithm;
        this.mastered = mastered;
        if (mastered) {
            this.masteredAt = OffsetDateTime.now();
        }
    }

    @PrePersist
    protected void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
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

    public boolean isMastered() {
        return mastered;
    }

    public void setMastered(boolean mastered) {
        this.mastered = mastered;
        if (mastered && this.masteredAt == null) {
            this.masteredAt = OffsetDateTime.now();
        }
    }

    public OffsetDateTime getMasteredAt() {
        return masteredAt;
    }

    public int getXpAwarded() {
        return xpAwarded;
    }

    public void setXpAwarded(int xpAwarded) {
        this.xpAwarded = xpAwarded;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}
