package com.codeloom.dsa.learning.dto;

import com.codeloom.dsa.learning.entity.ExperienceLevel;
import com.codeloom.dsa.learning.entity.PrimaryGoal;

public class OnboardingAssessmentRequest {
    private ExperienceLevel experienceLevel = ExperienceLevel.BEGINNER;
    private String preferredLanguage = "Java";
    private PrimaryGoal primaryGoal = PrimaryGoal.LEARN_DSA;
    private Integer dailyLearningMinutes = 30;

    public OnboardingAssessmentRequest() {}

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

    public PrimaryGoal getPrimaryGoal() {
        return primaryGoal;
    }

    public void setPrimaryGoal(PrimaryGoal primaryGoal) {
        this.primaryGoal = primaryGoal;
    }

    public Integer getDailyLearningMinutes() {
        return dailyLearningMinutes;
    }

    public void setDailyLearningMinutes(Integer dailyLearningMinutes) {
        this.dailyLearningMinutes = dailyLearningMinutes;
    }
}
