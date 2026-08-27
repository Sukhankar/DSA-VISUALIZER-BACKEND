package com.codeloom.dsa.progress.entity;

import com.codeloom.dsa.algorithm.entity.Algorithm;
import com.codeloom.dsa.user.entity.User;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "user_favorites",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_favorites_user_algorithm",
                        columnNames = {"user_id", "algorithm_id"}
                )
        }
)
public class UserFavorite {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "algorithm_id", nullable = false)
    private Algorithm algorithm;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    protected UserFavorite() {
    }

    public UserFavorite(User user, Algorithm algorithm) {
        this.user = user;
        this.algorithm = algorithm;
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

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
