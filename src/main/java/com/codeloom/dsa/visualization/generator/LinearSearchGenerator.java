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
public class LinearSearchGenerator implements VisualizationGenerator {

    private static final String SLUG = "linear-search";

    @Override
    public boolean supports(String algorithmSlug) {
        return SLUG.equalsIgnoreCase(algorithmSlug);
    }

    @Override
    public VisualizationResponse generate(String algorithmSlug, VisualizationRequest request) {
        List<Integer> array = new ArrayList<>(request.input());
        List<VisualizationStep> steps = new ArrayList<>();
        int stepNum = 1;
        int target = request.target() != null ? request.target() : (array.isEmpty() ? 0 : array.get(array.size() - 1));

        // 1. Initial State
        steps.add(new VisualizationStep(
                stepNum++,
                ActionType.INITIAL,
                List.of(),
                new ArrayList<>(array),
                "Initial array state. Target to search: " + target
        ));

        // 2. Linear Search
        boolean found = false;
        for (int i = 0; i < array.size(); i++) {
            int val = array.get(i);

            steps.add(new VisualizationStep(
                    stepNum++,
                    ActionType.COMPARE,
                    List.of(i),
                    new ArrayList<>(array),
                    String.format("Comparing element %d at index %d with target %d", val, i, target)
            ));

            if (val == target) {
                steps.add(new VisualizationStep(
                        stepNum++,
                        ActionType.SELECT,
                        List.of(i),
                        new ArrayList<>(array),
                        String.format("Found target %d at index %d!", target, i)
                ));
                found = true;
                break;
            }
        }

        // 3. Completion Step
        steps.add(new VisualizationStep(
                stepNum,
                ActionType.COMPLETE,
                List.of(),
                new ArrayList<>(array),
                found ? "Linear Search completed! Target found." : "Linear Search completed! Target " + target + " not found."
        ));

        return new VisualizationResponse(
                SLUG,
                VisualizationType.ARRAY,
                steps
        );
    }
}
