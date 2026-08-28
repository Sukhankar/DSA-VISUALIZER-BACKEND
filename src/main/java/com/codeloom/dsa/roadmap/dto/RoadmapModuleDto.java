package com.codeloom.dsa.roadmap.dto;

import com.codeloom.dsa.roadmap.entity.RoadmapStatus;
import com.codeloom.dsa.roadmap.entity.RoadmapTier;

import java.util.List;
import java.util.UUID;

public class RoadmapModuleDto {
    private UUID id;
    private String slug;
    private String title;
    private String description;
    private Integer orderIndex;
    private RoadmapTier tier;
    private String iconName;
    private String categorySlug;
    private String prerequisiteModuleSlug;
    private String prerequisiteModuleTitle;
    private Integer xpReward;
    private RoadmapStatus status;
    private Integer completionPercentage;
    private List<RoadmapStepDto> steps;

    public RoadmapModuleDto() {}

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

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public RoadmapTier getTier() {
        return tier;
    }

    public void setTier(RoadmapTier tier) {
        this.tier = tier;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public String getCategorySlug() {
        return categorySlug;
    }

    public void setCategorySlug(String categorySlug) {
        this.categorySlug = categorySlug;
    }

    public String getPrerequisiteModuleSlug() {
        return prerequisiteModuleSlug;
    }

    public void setPrerequisiteModuleSlug(String prerequisiteModuleSlug) {
        this.prerequisiteModuleSlug = prerequisiteModuleSlug;
    }

    public String getPrerequisiteModuleTitle() {
        return prerequisiteModuleTitle;
    }

    public void setPrerequisiteModuleTitle(String prerequisiteModuleTitle) {
        this.prerequisiteModuleTitle = prerequisiteModuleTitle;
    }

    public Integer getXpReward() {
        return xpReward;
    }

    public void setXpReward(Integer xpReward) {
        this.xpReward = xpReward;
    }

    public RoadmapStatus getStatus() {
        return status;
    }

    public void setStatus(RoadmapStatus status) {
        this.status = status;
    }

    public Integer getCompletionPercentage() {
        return completionPercentage;
    }

    public void setCompletionPercentage(Integer completionPercentage) {
        this.completionPercentage = completionPercentage;
    }

    public List<RoadmapStepDto> getSteps() {
        return steps;
    }

    public void setSteps(List<RoadmapStepDto> steps) {
        this.steps = steps;
    }
}
