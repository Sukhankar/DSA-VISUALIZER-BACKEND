package com.codeloom.dsa.roadmap.entity;

import com.codeloom.dsa.user.entity.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_assessments")
public class UserAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "experience_level", nullable = false, length = 50)
    private RoadmapTier experienceLevel;

    @Column(name = "preferred_language", length = 50)
    private String preferredLanguage;

    @Column(name = "knows_arrays")
    private Boolean knowsArrays = false;

    @Column(name = "knows_sorting")
    private Boolean knowsSorting = false;

    @Column(name = "knows_trees")
    private Boolean knowsTrees = false;

    @Column(name = "solved_problems_before")
    private Boolean solvedProblemsBefore = false;

    @Column(length = 255)
    private String goal;

    @Column(name = "recommended_module_slug", length = 100)
    private String recommendedModuleSlug;

    @Column(name = "completed_at", insertable = false, updatable = false)
    private LocalDateTime completedAt;

    public UserAssessment() {}

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

    public RoadmapTier getExperienceLevel() {
        return experienceLevel;
    }

    public void setExperienceLevel(RoadmapTier experienceLevel) {
        this.experienceLevel = experienceLevel;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    public Boolean getKnowsArrays() {
        return knowsArrays;
    }

    public void setKnowsArrays(Boolean knowsArrays) {
        this.knowsArrays = knowsArrays;
    }

    public Boolean getKnowsSorting() {
        return knowsSorting;
    }

    public void setKnowsSorting(Boolean knowsSorting) {
        this.knowsSorting = knowsSorting;
    }

    public Boolean getKnowsTrees() {
        return knowsTrees;
    }

    public void setKnowsTrees(Boolean knowsTrees) {
        this.knowsTrees = knowsTrees;
    }

    public Boolean getSolvedProblemsBefore() {
        return solvedProblemsBefore;
    }

    public void setSolvedProblemsBefore(Boolean solvedProblemsBefore) {
        this.solvedProblemsBefore = solvedProblemsBefore;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getRecommendedModuleSlug() {
        return recommendedModuleSlug;
    }

    public void setRecommendedModuleSlug(String recommendedModuleSlug) {
        this.recommendedModuleSlug = recommendedModuleSlug;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
}
