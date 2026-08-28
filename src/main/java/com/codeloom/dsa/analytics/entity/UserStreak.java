package com.codeloom.dsa.analytics.entity;

import com.codeloom.dsa.user.entity.User;
import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_streaks")
public class UserStreak implements Persistable<UUID> {

    @Id
    @Column(name = "user_id")
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "current_streak", nullable = false)
    private int currentStreak = 0;

    @Column(name = "longest_streak", nullable = false)
    private int longestStreak = 0;

    @Column(name = "last_activity_date")
    private LocalDate lastActivityDate;

    @Column(name = "streak_freeze_count", nullable = false)
    private int streakFreezeCount = 0;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Transient
    private boolean isNewEntity = true;

    protected UserStreak() {}

    public UserStreak(User user) {
        this.user = user;
        if (user != null) {
            this.id = user.getId();
        }
        this.currentStreak = 0;
        this.longestStreak = 0;
        this.streakFreezeCount = 0;
        this.isNewEntity = true;
    }

    @PostLoad
    @PostPersist
    protected void markNotNew() {
        this.isNewEntity = false;
    }

    @Override
    public boolean isNew() {
        return isNewEntity;
    }

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        if (user != null && id == null) {
            this.id = user.getId();
        }
        this.updatedAt = OffsetDateTime.now();
    }

    @Override
    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(int currentStreak) {
        this.currentStreak = currentStreak;
        if (this.currentStreak > this.longestStreak) {
            this.longestStreak = this.currentStreak;
        }
    }

    public int getLongestStreak() {
        return longestStreak;
    }

    public void setLongestStreak(int longestStreak) {
        this.longestStreak = longestStreak;
    }

    public LocalDate getLastActivityDate() {
        return lastActivityDate;
    }

    public void setLastActivityDate(LocalDate lastActivityDate) {
        this.lastActivityDate = lastActivityDate;
    }

    public int getStreakFreezeCount() {
        return streakFreezeCount;
    }

    public void setStreakFreezeCount(int streakFreezeCount) {
        this.streakFreezeCount = streakFreezeCount;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}
