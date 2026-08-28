package com.codeloom.dsa.roadmap.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "roadmap_steps", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"module_id", "step_number"})
})
public class RoadmapStep {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    private RoadmapModule module;

    @Column(name = "step_number", nullable = false)
    private Integer stepNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "step_type", nullable = false, length = 50)
    private RoadmapStepType stepType;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "reference_slug", length = 100)
    private String referenceSlug;

    @Column(name = "xp_reward")
    private Integer xpReward = 20;

    public RoadmapStep() {}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public RoadmapModule getModule() {
        return module;
    }

    public void setModule(RoadmapModule module) {
        this.module = module;
    }

    public Integer getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(Integer stepNumber) {
        this.stepNumber = stepNumber;
    }

    public RoadmapStepType getStepType() {
        return stepType;
    }

    public void setStepType(RoadmapStepType stepType) {
        this.stepType = stepType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReferenceSlug() {
        return referenceSlug;
    }

    public void setReferenceSlug(String referenceSlug) {
        this.referenceSlug = referenceSlug;
    }

    public Integer getXpReward() {
        return xpReward;
    }

    public void setXpReward(Integer xpReward) {
        this.xpReward = xpReward;
    }
}
