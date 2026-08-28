package com.codeloom.dsa.learning.dto;

public class LearningRecommendationResponse {
    private String type;
    private String title;
    private String description;
    private String slug;
    private Integer progress;
    private Integer xpReward;
    private String actionLabel;
    private String actionUrl;

    public LearningRecommendationResponse() {}

    public LearningRecommendationResponse(String type, String title, String description, String slug, Integer progress, Integer xpReward, String actionLabel, String actionUrl) {
        this.type = type;
        this.title = title;
        this.description = description;
        this.slug = slug;
        this.progress = progress;
        this.xpReward = xpReward;
        this.actionLabel = actionLabel;
        this.actionUrl = actionUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public Integer getXpReward() {
        return xpReward;
    }

    public void setXpReward(Integer xpReward) {
        this.xpReward = xpReward;
    }

    public String getActionLabel() {
        return actionLabel;
    }

    public void setActionLabel(String actionLabel) {
        this.actionLabel = actionLabel;
    }

    public String getActionUrl() {
        return actionUrl;
    }

    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }
}
