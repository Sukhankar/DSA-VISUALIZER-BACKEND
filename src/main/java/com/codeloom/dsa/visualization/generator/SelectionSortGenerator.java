package com.codeloom.dsa.visualization.generator;

import com.codeloom.dsa.visualization.dto.VisualizationRequest;
import com.codeloom.dsa.visualization.dto.VisualizationResponse;
import com.codeloom.dsa.visualization.dto.VisualizationStep;
import com.codeloom.dsa.visualization.entity.ActionType;
import com.codeloom.dsa.visualization.entity.VisualizationType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class SelectionSortGenerator implements VisualizationGenerator {

    private static final String SLUG = "selection-sort";

    @Override
    public boolean supports(String algorithmSlug) {
        return SLUG.equalsIgnoreCase(algorithmSlug);
    }

    @Override
    public VisualizationResponse generate(String algorithmSlug, VisualizationRequest request) {
        List<Integer> array = new ArrayList<>(request.input());
        List<VisualizationStep> steps = new ArrayList<>();
        int stepNum = 1;
        int n = array.size();

        // 1. Initial State
        steps.add(new VisualizationStep(
                stepNum++,
                ActionType.INITIAL,
                List.of(),
                new ArrayList<>(array),
                "Initial array state"
        ));

        // 2. Selection Sort Logic
        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;

            // Select candidate minimum
            steps.add(new VisualizationStep(
                    stepNum++,
                    ActionType.SELECT,
                    List.of(i),
                    new ArrayList<>(array),
                    String.format("Selected element %d at index %d as initial minimum candidate", array.get(i), i)
            ));

            for (int j = i + 1; j < n; j++) {
                // Compare with current minimum
                steps.add(new VisualizationStep(
                        stepNum++,
                        ActionType.COMPARE,
                        List.of(j, minIdx),
                        new ArrayList<>(array),
                        String.format("Comparing element %d at index %d with minimum %d at index %d",
                                array.get(j), j, array.get(minIdx), minIdx)
                ));

                if (array.get(j) < array.get(minIdx)) {
                    minIdx = j;
                    steps.add(new VisualizationStep(
                            stepNum++,
                            ActionType.SELECT,
                            List.of(minIdx),
                            new ArrayList<>(array),
                            String.format("Found new minimum element %d at index %d", array.get(minIdx), minIdx)
                    ));
                }
            }

            if (minIdx != i) {
                int oldVal = array.get(i);
                int newVal = array.get(minIdx);
                Collections.swap(array, i, minIdx);

                steps.add(new VisualizationStep(
                        stepNum++,
                        ActionType.SWAP,
                        List.of(i, minIdx),
                        new ArrayList<>(array),
                        String.format("Swapped %d and %d to place minimum element %d at index %d", oldVal, newVal, newVal, i)
                ));
            }
        }

        // 3. Completion Step
        steps.add(new VisualizationStep(
                stepNum,
                ActionType.COMPLETE,
                List.of(),
                new ArrayList<>(array),
                "Selection Sort completed! Array is fully sorted."
        ));

        return new VisualizationResponse(
                SLUG,
                VisualizationType.ARRAY,
                steps
        );
    }
}
