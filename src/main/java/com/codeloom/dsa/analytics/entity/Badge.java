package com.codeloom.dsa.analytics.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "badges")
public class Badge {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "icon_name", nullable = false, length = 50)
    private String iconName;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(name = "xp_reward", nullable = false)
    private int xpReward = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private BadgeRarity rarity = BadgeRarity.COMMON;

    @Column(name = "unlock_type", length = 50)
    private String unlockType;

    @Column(name = "unlock_value")
    private Integer unlockValue;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    protected Badge() {}

    public Badge(String code, String name, String description, String iconName, String category, int xpReward, BadgeRarity rarity, String unlockType, Integer unlockValue) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.iconName = iconName;
        this.category = category;
        this.xpReward = xpReward;
        this.rarity = rarity != null ? rarity : BadgeRarity.COMMON;
        this.unlockType = unlockType;
        this.unlockValue = unlockValue;
    }


    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getIconName() {
        return iconName;
    }

    public String getCategory() {
        return category;
    }

    public int getXpReward() {
        return xpReward;
    }

    public BadgeRarity getRarity() {
        return rarity;
    }

    public String getUnlockType() {
        return unlockType;
    }

    public Integer getUnlockValue() {
        return unlockValue;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

}
