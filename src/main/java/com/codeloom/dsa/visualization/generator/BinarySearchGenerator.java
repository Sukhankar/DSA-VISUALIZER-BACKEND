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
public class BinarySearchGenerator implements VisualizationGenerator {

    private static final String SLUG = "binary-search";

    @Override
    public boolean supports(String algorithmSlug) {
        return SLUG.equalsIgnoreCase(algorithmSlug);
    }

    @Override
    public VisualizationResponse generate(String algorithmSlug, VisualizationRequest request) {
        if (request.target() == null) {
            throw new IllegalArgumentException("Binary Search requires a target value to be specified");
        }

        List<Integer> array = new ArrayList<>(request.input());

        // Validate that input array is sorted in ascending order
        for (int i = 0; i < array.size() - 1; i++) {
            if (array.get(i) > array.get(i + 1)) {
                throw new IllegalArgumentException("Binary Search requires the input array to be sorted in ascending order");
            }
        }

        List<VisualizationStep> steps = new ArrayList<>();
        int stepNum = 1;
        int target = request.target();

        // 1. Initial State
        steps.add(new VisualizationStep(
                stepNum++,
                ActionType.INITIAL,
                List.of(),
                new ArrayList<>(array),
                "Starting Binary Search for target " + target
        ));

        // 2. Binary Search Logic
        int low = 0;
        int high = array.size() - 1;
        boolean found = false;
        int foundIndex = -1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            int midVal = array.get(mid);

            steps.add(new VisualizationStep(
                    stepNum++,
                    ActionType.COMPARE,
                    List.of(low, mid, high),
                    new ArrayList<>(array),
                    String.format("Comparing target %d with middle value %d at index %d [low=%d, high=%d]", target, midVal, mid, low, high)
            ));

            if (midVal == target) {
                found = true;
                foundIndex = mid;
                steps.add(new VisualizationStep(
                        stepNum++,
                        ActionType.FOUND,
                        List.of(mid),
                        new ArrayList<>(array),
                        String.format("Target %d found at index %d", target, mid)
                ));
                break;
            } else if (midVal < target) {
                low = mid + 1;
                steps.add(new VisualizationStep(
                        stepNum++,
                        ActionType.UPDATE,
                        List.of(Math.min(low, Math.max(0, array.size() - 1)), high),
                        new ArrayList<>(array),
                        String.format("Target %d > %d. Adjusting search range to right half [low=%d, high=%d]", target, midVal, low, high)
                ));
            } else {
                high = mid - 1;
                steps.add(new VisualizationStep(
                        stepNum++,
                        ActionType.UPDATE,
                        List.of(low, Math.max(0, high)),
                        new ArrayList<>(array),
                        String.format("Target %d < %d. Adjusting search range to left half [low=%d, high=%d]", target, midVal, low, high)
                ));
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
                found ? "Binary Search completed" : "Binary Search completed: target not found"
        ));

        return new VisualizationResponse(
                SLUG,
                VisualizationType.ARRAY,
                steps
        );
    }
}
