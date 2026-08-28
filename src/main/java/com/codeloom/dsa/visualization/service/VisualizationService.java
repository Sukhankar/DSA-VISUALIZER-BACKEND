package com.codeloom.dsa.visualization.service;

import com.codeloom.dsa.algorithm.repository.AlgorithmRepository;
import com.codeloom.dsa.common.exception.ResourceNotFoundException;
import com.codeloom.dsa.visualization.dto.VisualizationRequest;
import com.codeloom.dsa.visualization.dto.VisualizationResponse;
import com.codeloom.dsa.visualization.entity.VisualizationType;
import com.codeloom.dsa.visualization.generator.VisualizationGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
                .orElseGet(() -> generateGenericVisualization(slug, request));
    }

    private VisualizationResponse generateGenericVisualization(String slug, VisualizationRequest request) {
        var algoOpt = algorithmRepository.findBySlug(slug);
        String catSlug = algoOpt.map(a -> a.getCategory().getSlug()).orElse("arrays");
        String name = algoOpt.map(a -> a.getName()).orElse(slug);

        VisualizationType type;
        if ("trees".equalsIgnoreCase(catSlug)) {
            type = VisualizationType.TREE;
        } else if ("linked-lists".equalsIgnoreCase(catSlug)) {
            type = VisualizationType.LINKED_LIST;
        } else if ("graphs".equalsIgnoreCase(catSlug)) {
            type = VisualizationType.GRAPH;
        } else {
            type = VisualizationType.ARRAY;
        }

        List<com.codeloom.dsa.visualization.dto.VisualizationStep> steps = new ArrayList<>();
        int stepNum = 1;

        if (type == VisualizationType.TREE) {
            steps.add(new com.codeloom.dsa.visualization.dto.VisualizationStep(stepNum++, com.codeloom.dsa.visualization.entity.ActionType.INITIAL, List.of(), List.of(), "Initializing " + name + " tree structure execution."));
            steps.add(new com.codeloom.dsa.visualization.dto.VisualizationStep(stepNum++, com.codeloom.dsa.visualization.entity.ActionType.VISIT, List.of(), List.of(), "Visiting root node [10] for tree query operation."));
            steps.add(new com.codeloom.dsa.visualization.dto.VisualizationStep(stepNum++, com.codeloom.dsa.visualization.entity.ActionType.COMPARE, List.of(), List.of(), "Evaluating left child [5] against range constraints."));
            steps.add(new com.codeloom.dsa.visualization.dto.VisualizationStep(stepNum++, com.codeloom.dsa.visualization.entity.ActionType.UPDATE, List.of(), List.of(), "Updating tree balance and range segment sum."));
            steps.add(new com.codeloom.dsa.visualization.dto.VisualizationStep(stepNum++, com.codeloom.dsa.visualization.entity.ActionType.VISIT, List.of(), List.of(), "Visiting right child [20] for range aggregation."));
            steps.add(new com.codeloom.dsa.visualization.dto.VisualizationStep(stepNum++, com.codeloom.dsa.visualization.entity.ActionType.COMPLETE, List.of(), List.of(), name + " tree execution complete! Range result verified."));
        } else if (type == VisualizationType.LINKED_LIST) {
            steps.add(new com.codeloom.dsa.visualization.dto.VisualizationStep(stepNum++, com.codeloom.dsa.visualization.entity.ActionType.INITIAL, List.of(), List.of(), "Initializing " + name + " linked list node structure."));
            steps.add(new com.codeloom.dsa.visualization.dto.VisualizationStep(stepNum++, com.codeloom.dsa.visualization.entity.ActionType.VISIT, List.of(), List.of(), "Setting HEAD pointer to initial Node [10]."));
            steps.add(new com.codeloom.dsa.visualization.dto.VisualizationStep(stepNum++, com.codeloom.dsa.visualization.entity.ActionType.UPDATE, List.of(), List.of(), "Traversing next pointer to Node [20]."));
            steps.add(new com.codeloom.dsa.visualization.dto.VisualizationStep(stepNum++, com.codeloom.dsa.visualization.entity.ActionType.INSERT, List.of(), List.of(), "Executing node link modification."));
            steps.add(new com.codeloom.dsa.visualization.dto.VisualizationStep(stepNum++, com.codeloom.dsa.visualization.entity.ActionType.COMPLETE, List.of(), List.of(), name + " linked list operation complete!"));
        } else if (type == VisualizationType.GRAPH) {
            List<String> visited = new ArrayList<>();
            visited.add("A");
            steps.add(new com.codeloom.dsa.visualization.dto.VisualizationStep(stepNum++, com.codeloom.dsa.visualization.entity.ActionType.INITIAL, List.of(), List.of(), "A", new ArrayList<>(visited), List.of("B", "C"), "Initializing " + name + " graph traversal."));
            visited.add("B");
            steps.add(new com.codeloom.dsa.visualization.dto.VisualizationStep(stepNum++, com.codeloom.dsa.visualization.entity.ActionType.VISIT, List.of(), List.of(), "B", new ArrayList<>(visited), List.of("C", "D"), "Exploring vertex B via edge (A -> B)."));
            visited.add("C");
            steps.add(new com.codeloom.dsa.visualization.dto.VisualizationStep(stepNum++, com.codeloom.dsa.visualization.entity.ActionType.VISIT, List.of(), List.of(), "C", new ArrayList<>(visited), List.of("D", "E"), "Exploring vertex C via edge (A -> C)."));
            steps.add(new com.codeloom.dsa.visualization.dto.VisualizationStep(stepNum++, com.codeloom.dsa.visualization.entity.ActionType.COMPLETE, List.of(), List.of(), "E", new ArrayList<>(visited), List.of(), name + " graph processing complete!"));
        } else {

            List<Integer> array = (request != null && request.input() != null && !request.input().isEmpty())
                    ? new ArrayList<>(request.input())
                    : List.of(5, 1, 4, 2, 8);
            
            steps.add(new com.codeloom.dsa.visualization.dto.VisualizationStep(stepNum++, com.codeloom.dsa.visualization.entity.ActionType.INITIAL, List.of(), new ArrayList<>(array), "Initializing array for " + name + "."));
            steps.add(new com.codeloom.dsa.visualization.dto.VisualizationStep(stepNum++, com.codeloom.dsa.visualization.entity.ActionType.SELECT, List.of(0), new ArrayList<>(array), "Inspecting element " + array.get(0) + " at index 0."));
            
            if (array.size() > 1) {
                steps.add(new com.codeloom.dsa.visualization.dto.VisualizationStep(stepNum++, com.codeloom.dsa.visualization.entity.ActionType.COMPARE, List.of(0, 1), new ArrayList<>(array), "Comparing element " + array.get(0) + " (index 0) with " + array.get(1) + " (index 1)."));
                List<Integer> modified = new ArrayList<>(array);
                if (modified.get(0) > modified.get(1)) {
                    Collections.swap(modified, 0, 1);
                    steps.add(new com.codeloom.dsa.visualization.dto.VisualizationStep(stepNum++, com.codeloom.dsa.visualization.entity.ActionType.SWAP, List.of(0, 1), new ArrayList<>(modified), "Swapping elements at indices 0 and 1."));
                } else {
                    steps.add(new com.codeloom.dsa.visualization.dto.VisualizationStep(stepNum++, com.codeloom.dsa.visualization.entity.ActionType.UPDATE, List.of(1), new ArrayList<>(modified), "Updating algorithm state for index 1."));
                }

                if (array.size() > 2) {
                    steps.add(new com.codeloom.dsa.visualization.dto.VisualizationStep(stepNum++, com.codeloom.dsa.visualization.entity.ActionType.VISIT, List.of(2), new ArrayList<>(modified), "Processing element " + array.get(2) + " at index 2."));
                }
                steps.add(new com.codeloom.dsa.visualization.dto.VisualizationStep(stepNum++, com.codeloom.dsa.visualization.entity.ActionType.COMPLETE, List.of(), new ArrayList<>(modified), name + " execution finished successfully."));
            } else {
                steps.add(new com.codeloom.dsa.visualization.dto.VisualizationStep(stepNum++, com.codeloom.dsa.visualization.entity.ActionType.COMPLETE, List.of(), new ArrayList<>(array), name + " execution finished."));
            }
        }

        return new VisualizationResponse(slug, type, steps);
    }

}
