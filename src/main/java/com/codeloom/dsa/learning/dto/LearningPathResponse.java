package com.codeloom.dsa.learning.dto;

import com.codeloom.dsa.learning.entity.ExperienceLevel;
import com.codeloom.dsa.roadmap.dto.RoadmapModuleDto;

import java.util.List;
import java.util.UUID;

public class LearningPathResponse {
    private UUID id;
    private String slug;
    private String name;
    private String description;
    private ExperienceLevel difficulty;
    private String estimatedDuration;
    private Integer displayOrder;
    private Boolean isActive;
    private List<RoadmapModuleDto> modules;

    public LearningPathResponse() {}

    public LearningPathResponse(UUID id, String slug, String name, String description, ExperienceLevel difficulty, String estimatedDuration, Integer displayOrder, Boolean isActive, List<RoadmapModuleDto> modules) {
        this.id = id;
        this.slug = slug;
        this.name = name;
        this.description = description;
        this.difficulty = difficulty;
        this.estimatedDuration = estimatedDuration;
        this.displayOrder = displayOrder;
        this.isActive = isActive;
        this.modules = modules;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ExperienceLevel getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(ExperienceLevel difficulty) {
        this.difficulty = difficulty;
    }

    public String getEstimatedDuration() {
        return estimatedDuration;
    }

    public void setEstimatedDuration(String estimatedDuration) {
        this.estimatedDuration = estimatedDuration;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    public List<RoadmapModuleDto> getModules() {
        return modules;
    }

    public void setModules(List<RoadmapModuleDto> modules) {
        this.modules = modules;
    }
}
