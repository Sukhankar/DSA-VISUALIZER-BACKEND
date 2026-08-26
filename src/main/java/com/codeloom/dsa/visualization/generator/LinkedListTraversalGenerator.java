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
public class LinkedListTraversalGenerator implements VisualizationGenerator {

    private static final String SLUG = "linked-list-traversal";

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
                "Initial Linked List state. Head points to index 0"
        ));

        // 2. Traversal Loop
        for (int i = 0; i < array.size(); i++) {
            int val = array.get(i);
            String nextInfo = (i < array.size() - 1) ? "next -> index " + (i + 1) : "next -> null";

            steps.add(new VisualizationStep(
                    stepNum++,
                    ActionType.VISIT,
                    List.of(i),
                    new ArrayList<>(array),
                    String.format("Current pointer at node index %d (val=%d). %s", i, val, nextInfo)
            ));
        }

        // 3. Completion Step
        steps.add(new VisualizationStep(
                stepNum,
                ActionType.COMPLETE,
                List.of(),
                new ArrayList<>(array),
                "Reached end of Linked List (current == null). Traversal completed!"
        ));

        return new VisualizationResponse(
                SLUG,
                VisualizationType.LINKED_LIST,
                steps
        );
    }
}
