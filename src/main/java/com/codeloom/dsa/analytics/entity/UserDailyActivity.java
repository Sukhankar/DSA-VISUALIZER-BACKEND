package com.codeloom.dsa.analytics.entity;

import com.codeloom.dsa.user.entity.User;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "user_daily_activity",
        uniqueConstraints = @UniqueConstraint(name = "unique_user_daily_activity", columnNames = {"user_id", "activity_date"})
)
public class UserDailyActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "activity_date", nullable = false)
    private LocalDate activityDate;

    @Column(name = "algorithms_viewed_count", nullable = false)
    private int algorithmsViewedCount = 0;

    @Column(name = "problems_solved_count", nullable = false)
    private int problemsSolvedCount = 0;

    @Column(name = "xp_earned", nullable = false)
    private int xpEarned = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    protected UserDailyActivity() {}

    public UserDailyActivity(User user, LocalDate activityDate) {
        this.user = user;
        this.activityDate = activityDate;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public LocalDate getActivityDate() {
        return activityDate;
    }

    public int getAlgorithmsViewedCount() {
        return algorithmsViewedCount;
    }

    public void incrementAlgorithmsViewed() {
        this.algorithmsViewedCount++;
    }

    public int getProblemsSolvedCount() {
        return problemsSolvedCount;
    }

    public void incrementProblemsSolved() {
        this.problemsSolvedCount++;
    }

    public int getXpEarned() {
        return xpEarned;
    }

    public void addXpEarned(int xp) {
        this.xpEarned += xp;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
