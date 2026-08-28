package com.codeloom.dsa.roadmap.dto;

import com.codeloom.dsa.roadmap.entity.RoadmapTier;
import jakarta.validation.constraints.NotNull;

public class AssessmentRequestDto {

    @NotNull(message = "Experience level is required")
    private RoadmapTier experienceLevel;

    private String preferredLanguage;
    private Boolean knowsArrays = false;
    private Boolean knowsSorting = false;
    private Boolean knowsTrees = false;
    private Boolean solvedProblemsBefore = false;
    private String goal;

    public AssessmentRequestDto() {}

    public RoadmapTier getExperienceLevel() {
        return experienceLevel;
    }

    public void setExperienceLevel(RoadmapTier experienceLevel) {
        this.experienceLevel = experienceLevel;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    public Boolean getKnowsArrays() {
        return knowsArrays;
    }

    public void setKnowsArrays(Boolean knowsArrays) {
        this.knowsArrays = knowsArrays;
    }

    public Boolean getKnowsSorting() {
        return knowsSorting;
    }

    public void setKnowsSorting(Boolean knowsSorting) {
        this.knowsSorting = knowsSorting;
    }

    public Boolean getKnowsTrees() {
        return knowsTrees;
    }

    public void setKnowsTrees(Boolean knowsTrees) {
        this.knowsTrees = knowsTrees;
    }

    public Boolean getSolvedProblemsBefore() {
        return solvedProblemsBefore;
    }

    public void setSolvedProblemsBefore(Boolean solvedProblemsBefore) {
        this.solvedProblemsBefore = solvedProblemsBefore;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }
}
