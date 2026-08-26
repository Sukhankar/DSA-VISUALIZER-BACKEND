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
public class BubbleSortGenerator implements VisualizationGenerator {

    private static final String SLUG = "bubble-sort";

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

        // 2. Bubble Sort Logic
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                int valA = array.get(j);
                int valB = array.get(j + 1);

                // Compare Step
                steps.add(new VisualizationStep(
                        stepNum++,
                        ActionType.COMPARE,
                        List.of(j, j + 1),
                        new ArrayList<>(array),
                        String.format("Comparing %d and %d at indices %d and %d", valA, valB, j, j + 1)
                ));

                if (valA > valB) {
                    // Swap
                    Collections.swap(array, j, j + 1);
                    swapped = true;

                    steps.add(new VisualizationStep(
                            stepNum++,
                            ActionType.SWAP,
                            List.of(j, j + 1),
                            new ArrayList<>(array),
                            String.format("Swapped %d and %d at indices %d and %d", valA, valB, j, j + 1)
                    ));
                } else {
                    // No Swap
                    steps.add(new VisualizationStep(
                            stepNum++,
                            ActionType.NO_SWAP,
                            List.of(j, j + 1),
                            new ArrayList<>(array),
                            String.format("No swap needed as %d <= %d", valA, valB)
                    ));
                }
            }

            // Early exit if array is already sorted
            if (!swapped) {
                break;
            }
        }

        // 3. Completion Step
        steps.add(new VisualizationStep(
                stepNum,
                ActionType.COMPLETE,
                List.of(),
                new ArrayList<>(array),
                "Bubble Sort completed! Array is fully sorted."
        ));

        return new VisualizationResponse(
                SLUG,
                VisualizationType.ARRAY,
                steps
        );
    }
}
