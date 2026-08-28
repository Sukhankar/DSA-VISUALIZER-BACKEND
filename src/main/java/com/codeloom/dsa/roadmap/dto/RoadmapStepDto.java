package com.codeloom.dsa.roadmap.dto;

import com.codeloom.dsa.roadmap.entity.RoadmapStepType;
import java.util.UUID;

public class RoadmapStepDto {
    private UUID id;
    private Integer stepNumber;
    private RoadmapStepType stepType;
    private String title;
    private String description;
    private String referenceSlug;
    private Integer xpReward;
    private Boolean completed;

    public RoadmapStepDto() {}

    public RoadmapStepDto(UUID id, Integer stepNumber, RoadmapStepType stepType, String title, String description, String referenceSlug, Integer xpReward, Boolean completed) {
        this.id = id;
        this.stepNumber = stepNumber;
        this.stepType = stepType;
        this.title = title;
        this.description = description;
        this.referenceSlug = referenceSlug;
        this.xpReward = xpReward;
        this.completed = completed;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }
}
