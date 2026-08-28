package com.codeloom.dsa.roadmap.repository;

import com.codeloom.dsa.roadmap.entity.RoadmapModule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoadmapModuleRepository extends JpaRepository<RoadmapModule, UUID> {
    Optional<RoadmapModule> findBySlug(String slug);
    List<RoadmapModule> findAllByOrderByOrderIndexAsc();
}
