package com.codeloom.dsa.learning.service;

import com.codeloom.dsa.learning.dto.LearningPathResponse;
import com.codeloom.dsa.learning.entity.LearningPath;
import com.codeloom.dsa.learning.repository.LearningPathRepository;
import com.codeloom.dsa.roadmap.dto.RoadmapModuleDto;
import com.codeloom.dsa.roadmap.entity.RoadmapModule;
import com.codeloom.dsa.roadmap.repository.RoadmapModuleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class LearningPathService {

    private final LearningPathRepository learningPathRepository;
    private final RoadmapModuleRepository moduleRepository;

    public LearningPathService(LearningPathRepository learningPathRepository, RoadmapModuleRepository moduleRepository) {
        this.learningPathRepository = learningPathRepository;
        this.moduleRepository = moduleRepository;
    }

    public List<LearningPathResponse> getAllActivePaths() {
        List<LearningPath> paths = learningPathRepository.findAllByIsActiveTrueOrderByDisplayOrderAsc();
        List<LearningPathResponse> responses = new ArrayList<>();

        for (LearningPath path : paths) {
            responses.add(mapToResponse(path, false));
        }

        return responses;
    }

    public LearningPathResponse getPathBySlug(String slug) {
        LearningPath path = learningPathRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Learning path not found: " + slug));

        return mapToResponse(path, true);
    }

    private LearningPathResponse mapToResponse(LearningPath path, boolean includeModules) {
        List<RoadmapModuleDto> moduleDtos = new ArrayList<>();

        if (includeModules) {
            List<RoadmapModule> modules = moduleRepository.findAllByOrderByOrderIndexAsc();
            for (RoadmapModule m : modules) {
                if (m.getLearningPath() != null && m.getLearningPath().getId().equals(path.getId())) {
                    RoadmapModuleDto dto = new RoadmapModuleDto();
                    dto.setId(m.getId());
                    dto.setSlug(m.getSlug());
                    dto.setTitle(m.getTitle());
                    dto.setDescription(m.getDescription());
                    dto.setOrderIndex(m.getOrderIndex());
                    dto.setTier(m.getTier());
                    dto.setIconName(m.getIconName());
                    dto.setCategorySlug(m.getCategorySlug());
                    dto.setXpReward(m.getXpReward());
                    moduleDtos.add(dto);
                }
            }
        }

        return new LearningPathResponse(
                path.getId(),
                path.getSlug(),
                path.getName(),
                path.getDescription(),
                path.getDifficulty(),
                path.getEstimatedDuration(),
                path.getDisplayOrder(),
                path.getIsActive(),
                moduleDtos
        );
    }
}
