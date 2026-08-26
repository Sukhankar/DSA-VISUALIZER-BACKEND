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
public class BinarySearchGenerator implements VisualizationGenerator {

    private static final String SLUG = "binary-search";

    @Override
    public boolean supports(String algorithmSlug) {
        return SLUG.equalsIgnoreCase(algorithmSlug);
    }

    @Override
    public VisualizationResponse generate(String algorithmSlug, VisualizationRequest request) {
        List<Integer> array = new ArrayList<>(request.input());
        List<VisualizationStep> steps = new ArrayList<>();
        int stepNum = 1;

        // Ensure array is sorted for Binary Search
        Collections.sort(array);

        int target = request.target() != null ? request.target() : (array.isEmpty() ? 0 : array.get(array.size() - 1));

        // 1. Initial State
        steps.add(new VisualizationStep(
                stepNum++,
                ActionType.INITIAL,
                List.of(),
                new ArrayList<>(array),
                "Sorted array for Binary Search. Target: " + target
        ));

        // 2. Binary Search Logic
        int low = 0;
        int high = array.size() - 1;
        boolean found = false;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            int midVal = array.get(mid);

            steps.add(new VisualizationStep(
                    stepNum++,
                    ActionType.SELECT,
                    List.of(low, mid, high),
                    new ArrayList<>(array),
                    String.format("Pointers at low=%d (%d), mid=%d (%d), high=%d (%d)", low, array.get(low), mid, midVal, high, array.get(high))
            ));

            steps.add(new VisualizationStep(
                    stepNum++,
                    ActionType.COMPARE,
                    List.of(mid),
                    new ArrayList<>(array),
                    String.format("Comparing mid element %d at index %d with target %d", midVal, mid, target)
            ));

            if (midVal == target) {
                steps.add(new VisualizationStep(
                        stepNum++,
                        ActionType.SELECT,
                        List.of(mid),
                        new ArrayList<>(array),
                        String.format("Found target %d at index %d!", target, mid)
                ));
                found = true;
                break;
            } else if (midVal < target) {
                low = mid + 1;
                steps.add(new VisualizationStep(
                        stepNum++,
                        ActionType.UPDATE,
                        List.of(Math.min(low, array.size() - 1), high),
                        new ArrayList<>(array),
                        String.format("Target %d > %d. Shifting search range to right [low=%d, high=%d]", target, midVal, low, high)
                ));
            } else {
                high = mid - 1;
                steps.add(new VisualizationStep(
                        stepNum++,
                        ActionType.UPDATE,
                        List.of(low, Math.max(0, high)),
                        new ArrayList<>(array),
                        String.format("Target %d < %d. Shifting search range to left [low=%d, high=%d]", target, midVal, low, high)
                ));
            }
        }

        // 3. Completion Step
        steps.add(new VisualizationStep(
                stepNum,
                ActionType.COMPLETE,
                List.of(),
                new ArrayList<>(array),
                found ? "Binary Search completed! Target found." : "Binary Search completed! Target " + target + " not found."
        ));

        return new VisualizationResponse(
                SLUG,
                VisualizationType.ARRAY,
                steps
        );
    }
}
