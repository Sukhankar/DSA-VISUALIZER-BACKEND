package com.codeloom.dsa.visualization.service;

import com.codeloom.dsa.algorithm.repository.AlgorithmRepository;
import com.codeloom.dsa.common.exception.ResourceNotFoundException;
import com.codeloom.dsa.visualization.dto.VisualizationRequest;
import com.codeloom.dsa.visualization.dto.VisualizationResponse;
import com.codeloom.dsa.visualization.entity.VisualizationType;
import com.codeloom.dsa.visualization.generator.VisualizationGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class VisualizationService {

    private final AlgorithmRepository algorithmRepository;
    private final List<VisualizationGenerator> generators;

    public VisualizationService(
            AlgorithmRepository algorithmRepository,
            List<VisualizationGenerator> generators
    ) {
        this.algorithmRepository = algorithmRepository;
        this.generators = generators;
    }

    public VisualizationResponse generateVisualization(
            String slug,
            VisualizationRequest request
    ) {
        // 1. Verify algorithm exists in database
        algorithmRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Algorithm not found with slug: " + slug));

        // 2. Validate request input payload
        if (request.graph() == null && (request.input() == null || request.input().isEmpty())) {
            throw new IllegalArgumentException("Input list must not be empty");
        }

        // 3. Find supporting generator
        return generators.stream()
                .filter(g -> g.supports(slug))
                .findFirst()
                .map(g -> g.generate(slug, request))
                .orElseGet(() -> new VisualizationResponse(
                        slug,
                        VisualizationType.ARRAY,
                        Collections.emptyList()
                ));
    }
}
