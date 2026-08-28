package com.codeloom.dsa.roadmap.dto;

import com.codeloom.dsa.learning.dto.LearningPathResponse;
import java.util.List;

public class UserRoadmapDto {
    private LearningPathResponse path;
    private int overallProgress;
    private RoadmapModuleDto currentModule;
    private List<RoadmapModuleDto> modules;

    public UserRoadmapDto() {}

    public UserRoadmapDto(LearningPathResponse path, int overallProgress, RoadmapModuleDto currentModule, List<RoadmapModuleDto> modules) {
        this.path = path;
        this.overallProgress = overallProgress;
        this.currentModule = currentModule;
        this.modules = modules;
    }

    public LearningPathResponse getPath() {
        return path;
    }

    public void setPath(LearningPathResponse path) {
        this.path = path;
    }

    public int getOverallProgress() {
        return overallProgress;
    }

    public void setOverallProgress(int overallProgress) {
        this.overallProgress = overallProgress;
    }

    public RoadmapModuleDto getCurrentModule() {
        return currentModule;
    }

    public void setCurrentModule(RoadmapModuleDto currentModule) {
        this.currentModule = currentModule;
    }

    public List<RoadmapModuleDto> getModules() {
        return modules;
    }

    public void setModules(List<RoadmapModuleDto> modules) {
        this.modules = modules;
    }
}
