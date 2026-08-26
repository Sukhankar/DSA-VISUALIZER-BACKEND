package com.codeloom.dsa.visualization.generator;

import com.codeloom.dsa.visualization.dto.VisualizationRequest;
import com.codeloom.dsa.visualization.dto.VisualizationResponse;
import com.codeloom.dsa.visualization.dto.VisualizationStep;
import com.codeloom.dsa.visualization.entity.ActionType;
import com.codeloom.dsa.visualization.entity.VisualizationType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TreeTraversalGenerator implements VisualizationGenerator {

    private static final String SLUG = "tree-traversal";

    @Override
    public boolean supports(String algorithmSlug) {
        return SLUG.equalsIgnoreCase(algorithmSlug);
    }

    @Override
    public VisualizationResponse generate(String algorithmSlug, VisualizationRequest request) {
        List<Integer> array = new ArrayList<>(request.input());
        List<VisualizationStep> steps = new ArrayList<>();
        int stepNum = 1;

        // 1. Initial State
        steps.add(new VisualizationStep(
                stepNum++,
                ActionType.INITIAL,
                List.of(),
                new ArrayList<>(array),
                "Initial Tree Traversal state. Root node at index 0"
        ));

        // 2. Traversal
        for (int i = 0; i < array.size(); i++) {
            int val = array.get(i);
            steps.add(new VisualizationStep(
                    stepNum++,
                    ActionType.VISIT,
                    List.of(i),
                    new ArrayList<>(array),
                    String.format("In-Order Traversal: Visited tree node %d at index %d", val, i)
            ));
        }

        // 3. Completion Step
        steps.add(new VisualizationStep(
                stepNum,
                ActionType.COMPLETE,
                List.of(),
                new ArrayList<>(array),
                "Tree Traversal completed!"
        ));

        return new VisualizationResponse(
                SLUG,
                VisualizationType.TREE,
                steps
        );
    }
}
