package com.codeloom.dsa.learning.dto;

import com.codeloom.dsa.learning.entity.ExperienceLevel;
import com.codeloom.dsa.learning.entity.PrimaryGoal;
import jakarta.validation.constraints.NotNull;

public class LearningPreferenceRequest {

    @NotNull
    private ExperienceLevel experienceLevel;

    private String preferredLanguage = "Java";

    private Integer dailyLearningMinutes = 30;

    private PrimaryGoal primaryGoal = PrimaryGoal.LEARN_DSA;

    public LearningPreferenceRequest() {}

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
}
