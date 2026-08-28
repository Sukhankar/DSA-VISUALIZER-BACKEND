package com.codeloom.dsa.roadmap.dto;

import com.codeloom.dsa.roadmap.entity.RoadmapTier;

public class AssessmentResultDto {
    private String assessmentId;
    private RoadmapTier assignedTier;
    private String recommendedModuleSlug;
    private String recommendedModuleTitle;
    private String summaryMessage;
    private Integer bonusXpEarned;

    public AssessmentResultDto() {}

    public AssessmentResultDto(String assessmentId, RoadmapTier assignedTier, String recommendedModuleSlug, String recommendedModuleTitle, String summaryMessage, Integer bonusXpEarned) {
        this.assessmentId = assessmentId;
        this.assignedTier = assignedTier;
        this.recommendedModuleSlug = recommendedModuleSlug;
        this.recommendedModuleTitle = recommendedModuleTitle;
        this.summaryMessage = summaryMessage;
        this.bonusXpEarned = bonusXpEarned;
    }

    public String getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(String assessmentId) {
        this.assessmentId = assessmentId;
    }

    public RoadmapTier getAssignedTier() {
        return assignedTier;
    }

    public void setAssignedTier(RoadmapTier assignedTier) {
        this.assignedTier = assignedTier;
    }

    public String getRecommendedModuleSlug() {
        return recommendedModuleSlug;
    }

    public void setRecommendedModuleSlug(String recommendedModuleSlug) {
        this.recommendedModuleSlug = recommendedModuleSlug;
    }

    public String getRecommendedModuleTitle() {
        return recommendedModuleTitle;
    }

    public void setRecommendedModuleTitle(String recommendedModuleTitle) {
        this.recommendedModuleTitle = recommendedModuleTitle;
    }

    public String getSummaryMessage() {
        return summaryMessage;
    }

    public void setSummaryMessage(String summaryMessage) {
        this.summaryMessage = summaryMessage;
    }

    public Integer getBonusXpEarned() {
        return bonusXpEarned;
    }

    public void setBonusXpEarned(Integer bonusXpEarned) {
        this.bonusXpEarned = bonusXpEarned;
    }
}
