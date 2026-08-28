package com.codeloom.dsa.analytics.entity;

import com.codeloom.dsa.user.entity.User;
import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_xp")
public class UserXp implements Persistable<UUID> {

    @Id
    @Column(name = "user_id")
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "total_xp", nullable = false)
    private int totalXp = 0;

    @Column(name = "current_level", nullable = false)
    private int currentLevel = 1;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Transient
    private boolean isNewEntity = true;

    protected UserXp() {}

    public UserXp(User user) {
        this.user = user;
        if (user != null) {
            this.id = user.getId();
        }
        this.totalXp = 0;
        this.currentLevel = 1;
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

    public int getTotalXp() {
        return totalXp;
    }

    public void addXp(int xp) {
        this.totalXp += xp;
        this.currentLevel = calculateLevel(this.totalXp);
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public static int calculateLevel(int xp) {
        if (xp <= 0) return 1;
        return (int) Math.floor(Math.sqrt(xp / 100.0)) + 1;
    }

    public static int getXpForLevel(int level) {
        if (level <= 1) return 0;
        return (int) Math.pow(level - 1, 2) * 100;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}
