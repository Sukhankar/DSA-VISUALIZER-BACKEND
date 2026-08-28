package com.codeloom.dsa.roadmap.dto;

public class NextRecommendationDto {
    private String moduleSlug;
    private String moduleTitle;
    private String stepTitle;
    private String stepType;
    private String referenceSlug;
    private String actionUrl;
    private String recommendationReason;
    private Integer xpReward;

    public NextRecommendationDto() {}

    public NextRecommendationDto(String moduleSlug, String moduleTitle, String stepTitle, String stepType, String referenceSlug, String actionUrl, String recommendationReason, Integer xpReward) {
        this.moduleSlug = moduleSlug;
        this.moduleTitle = moduleTitle;
        this.stepTitle = stepTitle;
        this.stepType = stepType;
        this.referenceSlug = referenceSlug;
        this.actionUrl = actionUrl;
        this.recommendationReason = recommendationReason;
        this.xpReward = xpReward;
    }

    public String getModuleSlug() {
        return moduleSlug;
    }

    public void setModuleSlug(String moduleSlug) {
        this.moduleSlug = moduleSlug;
    }

    public String getModuleTitle() {
        return moduleTitle;
    }

    public void setModuleTitle(String moduleTitle) {
        this.moduleTitle = moduleTitle;
    }

    public String getStepTitle() {
        return stepTitle;
    }

    public void setStepTitle(String stepTitle) {
        this.stepTitle = stepTitle;
    }

    public String getStepType() {
        return stepType;
    }

    public void setStepType(String stepType) {
        this.stepType = stepType;
    }

    public String getReferenceSlug() {
        return referenceSlug;
    }

    public void setReferenceSlug(String referenceSlug) {
        this.referenceSlug = referenceSlug;
    }

    public String getActionUrl() {
        return actionUrl;
    }

    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }

    public String getRecommendationReason() {
        return recommendationReason;
    }

    public void setRecommendationReason(String recommendationReason) {
        this.recommendationReason = recommendationReason;
    }

    public Integer getXpReward() {
        return xpReward;
    }

    public void setXpReward(Integer xpReward) {
        this.xpReward = xpReward;
    }
}
