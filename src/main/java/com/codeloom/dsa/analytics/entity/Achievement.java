package com.codeloom.dsa.analytics.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "achievements")
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private AchievementCategory category;

    @Column(nullable = false, length = 50)
    private String icon;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private AchievementRarity rarity;

    @Column(name = "xp_reward", nullable = false)
    private int xpReward = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "requirement_type", nullable = false, length = 50)
    private AchievementRequirementType requirementType;

    @Column(name = "requirement_value", nullable = false)
    private int requirementValue = 1;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    protected Achievement() {
    }

    public Achievement(String code, String name, String description, AchievementCategory category, String icon, AchievementRarity rarity, int xpReward, AchievementRequirementType requirementType, int requirementValue) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.category = category;
        this.icon = icon;
        this.rarity = rarity;
        this.xpReward = xpReward;
        this.requirementType = requirementType;
        this.requirementValue = requirementValue;
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

    public AchievementCategory getCategory() {
        return category;
    }

    public String getIcon() {
        return icon;
    }

    public AchievementRarity getRarity() {
        return rarity;
    }

    public int getXpReward() {
        return xpReward;
    }

    public AchievementRequirementType getRequirementType() {
        return requirementType;
    }

    public int getRequirementValue() {
        return requirementValue;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
