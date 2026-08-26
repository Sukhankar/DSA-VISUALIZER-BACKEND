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
public class InsertionSortGenerator implements VisualizationGenerator {

    private static final String SLUG = "insertion-sort";

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

        // 2. Insertion Sort Logic
        for (int i = 1; i < n; i++) {
            int key = array.get(i);

            steps.add(new VisualizationStep(
                    stepNum++,
                    ActionType.SELECT,
                    List.of(i),
                    new ArrayList<>(array),
                    String.format("Selected key %d at index %d to insert into sorted portion [0..%d]", key, i, i - 1)
            ));

            int j = i - 1;
            while (j >= 0 && array.get(j) > key) {
                // Compare
                steps.add(new VisualizationStep(
                        stepNum++,
                        ActionType.COMPARE,
                        List.of(j, j + 1),
                        new ArrayList<>(array),
                        String.format("Comparing key %d with %d at index %d (%d > key)", key, array.get(j), j, array.get(j))
                ));

                // Shift right
                array.set(j + 1, array.get(j));

                steps.add(new VisualizationStep(
                        stepNum++,
                        ActionType.UPDATE,
                        List.of(j + 1),
                        new ArrayList<>(array),
                        String.format("Shifted %d right to index %d", array.get(j + 1), j + 1)
                ));

                j--;
            }

            if (j >= 0) {
                // Final comparison showing why loop stopped
                steps.add(new VisualizationStep(
                        stepNum++,
                        ActionType.COMPARE,
                        List.of(j, j + 1),
                        new ArrayList<>(array),
                        String.format("Comparing key %d with %d at index %d (%d <= key)", key, array.get(j), j, array.get(j))
                ));
            }

            // Insert key
            array.set(j + 1, key);

            steps.add(new VisualizationStep(
                    stepNum++,
                    ActionType.UPDATE,
                    List.of(j + 1),
                    new ArrayList<>(array),
                    String.format("Inserted key %d at index %d", key, j + 1)
            ));
        }

        // 3. Completion Step
        steps.add(new VisualizationStep(
                stepNum,
                ActionType.COMPLETE,
                List.of(),
                new ArrayList<>(array),
                "Insertion Sort completed! Array is fully sorted."
        ));

        return new VisualizationResponse(
                SLUG,
                VisualizationType.ARRAY,
                steps
        );
    }
}
