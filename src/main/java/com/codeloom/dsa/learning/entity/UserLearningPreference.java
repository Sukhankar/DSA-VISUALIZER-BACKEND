package com.codeloom.dsa.learning.entity;

import com.codeloom.dsa.user.entity.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_learning_preferences", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id"})
})
public class UserLearningPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "experience_level", nullable = false, length = 50)
    private ExperienceLevel experienceLevel = ExperienceLevel.BEGINNER;

    @Column(name = "preferred_language", length = 50)
    private String preferredLanguage = "Java";

    @Column(name = "daily_learning_minutes")
    private Integer dailyLearningMinutes = 30;

    @Enumerated(EnumType.STRING)
    @Column(name = "primary_goal", length = 100)
    private PrimaryGoal primaryGoal = PrimaryGoal.LEARN_DSA;

    @Column(name = "completed_assessment")
    private Boolean completedAssessment = false;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public UserLearningPreference() {}

    public UserLearningPreference(User user, ExperienceLevel experienceLevel, String preferredLanguage, Integer dailyLearningMinutes, PrimaryGoal primaryGoal) {
        this.user = user;
        this.experienceLevel = experienceLevel;
        this.preferredLanguage = preferredLanguage;
        this.dailyLearningMinutes = dailyLearningMinutes;
        this.primaryGoal = primaryGoal;
        this.completedAssessment = true;
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

    public ExperienceLevel getExperienceLevel() {
        return experienceLevel;
    }

    public void setExperienceLevel(ExperienceLevel experienceLevel) {
        this.experienceLevel = experienceLevel;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    public Integer getDailyLearningMinutes() {
        return dailyLearningMinutes;
    }

    public void setDailyLearningMinutes(Integer dailyLearningMinutes) {
        this.dailyLearningMinutes = dailyLearningMinutes;
    }

    public PrimaryGoal getPrimaryGoal() {
        return primaryGoal;
    }

    public void setPrimaryGoal(PrimaryGoal primaryGoal) {
        this.primaryGoal = primaryGoal;
    }

    public Boolean getCompletedAssessment() {
        return completedAssessment;
    }

    public void setCompletedAssessment(Boolean completedAssessment) {
        this.completedAssessment = completedAssessment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
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
