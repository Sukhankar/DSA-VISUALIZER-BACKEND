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
        if (request.target() == null) {
            throw new IllegalArgumentException("Linear Search requires a target value to be specified");
        }

        List<Integer> array = new ArrayList<>(request.input());
        List<VisualizationStep> steps = new ArrayList<>();
        int stepNum = 1;
        int target = request.target();

        // 1. Initial State
        steps.add(new VisualizationStep(
                stepNum++,
                ActionType.INITIAL,
                List.of(),
                new ArrayList<>(array),
                "Starting Linear Search for target " + target
        ));

        // 2. Linear Search Loop
        boolean found = false;
        int foundIndex = -1;

        for (int i = 0; i < array.size(); i++) {
            int val = array.get(i);

            steps.add(new VisualizationStep(
                    stepNum++,
                    ActionType.COMPARE,
                    List.of(i),
                    new ArrayList<>(array),
                    String.format("Comparing target %d with value %d at index %d", target, val, i)
            ));

            if (val == target) {
                found = true;
                foundIndex = i;
                steps.add(new VisualizationStep(
                        stepNum++,
                        ActionType.FOUND,
                        List.of(i),
                        new ArrayList<>(array),
                        String.format("Target %d found at index %d", target, i)
                ));
                break;
            }
        }

        if (!found) {
            steps.add(new VisualizationStep(
                    stepNum++,
                    ActionType.NOT_FOUND,
                    List.of(),
                    new ArrayList<>(array),
                    String.format("Target %d not found in the array", target)
            ));
        }

        // 3. Completion Step
        steps.add(new VisualizationStep(
                stepNum,
                ActionType.COMPLETE,
                foundIndex != -1 ? List.of(foundIndex) : List.of(),
                new ArrayList<>(array),
                found ? "Linear Search completed" : "Linear Search completed: target not found"
        ));

        return new VisualizationResponse(
                SLUG,
                VisualizationType.ARRAY,
                steps
        );
    }
}
