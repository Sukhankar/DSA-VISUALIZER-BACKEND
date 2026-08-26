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
public class KadanesAlgorithmGenerator implements VisualizationGenerator {

    private static final String SLUG = "kadanes-algorithm";

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
                "Initial array for Kadane's Maximum Subarray Algorithm"
        ));

        // 2. Kadane's Logic
        int maxSoFar = array.get(0);
        int currentMax = array.get(0);
        int start = 0;
        int end = 0;
        int tempStart = 0;

        steps.add(new VisualizationStep(
                stepNum++,
                ActionType.SELECT,
                List.of(0),
                new ArrayList<>(array),
                String.format("Starting at index 0. Current subarray sum: %d, Max sum so far: %d", currentMax, maxSoFar)
        ));

        for (int i = 1; i < array.size(); i++) {
            int val = array.get(i);

            if (val > currentMax + val) {
                currentMax = val;
                tempStart = i;
            } else {
                currentMax += val;
            }

            steps.add(new VisualizationStep(
                    stepNum++,
                    ActionType.VISIT,
                    List.of(i),
                    new ArrayList<>(array),
                    String.format("Visiting index %d (%d). Current subarray sum ending at %d is %d", i, val, i, currentMax)
            ));

            if (currentMax > maxSoFar) {
                maxSoFar = currentMax;
                start = tempStart;
                end = i;

                List<Integer> maxRange = new ArrayList<>();
                for (int r = start; r <= end; r++) {
                    maxRange.add(r);
                }

                steps.add(new VisualizationStep(
                        stepNum++,
                        ActionType.UPDATE,
                        maxRange,
                        new ArrayList<>(array),
                        String.format("New global maximum subarray sum %d found across indices [%d..%d]", maxSoFar, start, end)
                ));
            }
        }

        // 3. Completion Step
        steps.add(new VisualizationStep(
                stepNum,
                ActionType.COMPLETE,
                List.of(),
                new ArrayList<>(array),
                String.format("Kadane's Algorithm completed! Maximum subarray sum is %d.", maxSoFar)
        ));

        return new VisualizationResponse(
                SLUG,
                VisualizationType.ARRAY,
                steps
        );
    }
}
