package com.codeloom.dsa.roadmap.repository;

import com.codeloom.dsa.roadmap.entity.RoadmapStep;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RoadmapStepRepository extends JpaRepository<RoadmapStep, UUID> {
    List<RoadmapStep> findByModuleSlugOrderByStepNumberAsc(String moduleSlug);
}
