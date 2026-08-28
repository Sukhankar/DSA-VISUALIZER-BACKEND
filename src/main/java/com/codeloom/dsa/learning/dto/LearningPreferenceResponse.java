package com.codeloom.dsa.learning.dto;

import com.codeloom.dsa.learning.entity.ExperienceLevel;
import com.codeloom.dsa.learning.entity.PrimaryGoal;

public class LearningPreferenceResponse {
    private ExperienceLevel experienceLevel;
    private String preferredLanguage;
    private Integer dailyLearningMinutes;
    private PrimaryGoal primaryGoal;
    private Boolean completedAssessment;

    public LearningPreferenceResponse() {}

    public LearningPreferenceResponse(ExperienceLevel experienceLevel, String preferredLanguage, Integer dailyLearningMinutes, PrimaryGoal primaryGoal, Boolean completedAssessment) {
        this.experienceLevel = experienceLevel;
        this.preferredLanguage = preferredLanguage;
        this.dailyLearningMinutes = dailyLearningMinutes;
        this.primaryGoal = primaryGoal;
        this.completedAssessment = completedAssessment;
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
}
