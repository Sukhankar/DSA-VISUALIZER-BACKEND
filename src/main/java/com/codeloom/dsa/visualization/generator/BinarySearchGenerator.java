package com.codeloom.dsa.visualization.generator;

import com.codeloom.dsa.visualization.dto.VisualizationRequest;
import com.codeloom.dsa.visualization.dto.VisualizationResponse;
import com.codeloom.dsa.visualization.dto.VisualizationStep;
import com.codeloom.dsa.visualization.entity.ActionType;
import com.codeloom.dsa.visualization.entity.VisualizationType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
                "Starting Binary Search for target " + target,
                Map.of("java", 2, "python", 2, "cpp", 2, "pseudocode", 2),
                "Binary Search requires a sorted array. We set low at start (index 0) and high at end (index " + (array.size() - 1) + ").",
                "By keeping bounds low and high, we can eliminate half of the remaining elements in each step.",
                "Invariants established: search range is [0, " + (array.size() - 1) + "]",
                "Initial space: O(1), Search space size: " + array.size()
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

                    String.format("Comparing target %d with middle value %d at index %d [low=%d, high=%d]", target, midVal, mid, low, high),
                    Map.of("java", 4, "python", 4, "cpp", 4, "pseudocode", 4),
                    String.format("We pick the middle index %d. Value is %d. Is %d equal to, greater than, or less than target %d?", mid, midVal, midVal, target),
                    "Checking the midpoint allows us to divide the remaining problem size N in half.",
                    String.format("Loop invariant: Target, if present, lies in array[%d..%d]. Midpoint calculated using overflow-safe formula.", low, high),
                    String.format("Remaining search space size: %d elements", (high - low + 1))
            ));

            if (midVal == target) {
                found = true;
                foundIndex = mid;
                steps.add(new VisualizationStep(
                        stepNum++,
                        ActionType.FOUND,
                        List.of(mid),
                        new ArrayList<>(array),

                        String.format("Target %d found at index %d", target, mid),
                        Map.of("java", 5, "python", 6, "cpp", 5, "pseudocode", 5),
                        String.format("Success! The middle value at index %d matches the target %d.", mid, target),
                        "Target matched exactly. We can return immediately without searching further.",
                        "Target located. Search terminates early.",
                        "Total comparisons made: " + (stepNum - 1)
                ));
                break;
            } else if (midVal < target) {
                low = mid + 1;
                steps.add(new VisualizationStep(
                        stepNum++,
                        ActionType.UPDATE,
                        List.of(Math.min(low, Math.max(0, array.size() - 1)), high),
                        new ArrayList<>(array),

                        String.format("Target %d > %d. Adjusting search range to right half [low=%d, high=%d]", target, midVal, low, high),
                        Map.of("java", 6, "python", 8, "cpp", 6, "pseudocode", 6),
                        String.format("Since %d is smaller than target %d, and the array is sorted, the target must lie to the right of index %d. We move low to %d.", midVal, target, mid, low),
                        "All elements at or left of mid are smaller than target, so we safely discard the left half.",
                        String.format("Search space halved: [low=%d, high=%d].", low, high),
                        "Remaining search space reduced by 50% -> logarithmic reduction O(log N)."
                ));
            } else {
                high = mid - 1;
                steps.add(new VisualizationStep(
                        stepNum++,
                        ActionType.UPDATE,
                        List.of(low, Math.max(0, high)),
                        new ArrayList<>(array),

                        String.format("Target %d < %d. Adjusting search range to left half [low=%d, high=%d]", target, midVal, low, high),
                        Map.of("java", 7, "python", 10, "cpp", 7, "pseudocode", 7),
                        String.format("Since %d is larger than target %d, the target must lie to the left of index %d. We move high to %d.", midVal, target, mid, high),
                        "All elements at or right of mid are larger than target, so we safely discard the right half.",
                        String.format("Search space halved: [low=%d, high=%d].", low, high),
                        "Remaining search space reduced by 50% -> logarithmic reduction O(log N)."
                ));
            }
        }

        if (!found) {
            steps.add(new VisualizationStep(
                    stepNum++,
                    ActionType.NOT_FOUND,
                    List.of(),
                    new ArrayList<>(array),

                    String.format("Target %d not found in the array", target),
                    Map.of("java", 9, "python", 11, "cpp", 9, "pseudocode", 8),
                    String.format("Search range exhausted (low=%d > high=%d). Target %d is not present in the array.", low, high, target),
                    "When low > high, all possible positions have been ruled out.",
                    "Exhaustive search complete under O(log N) steps. Return -1.",
                    "Time Complexity: O(log N), Auxiliary Space: O(1)"
            ));
        }

        // 3. Completion Step
        steps.add(new VisualizationStep(
                stepNum,
                ActionType.COMPLETE,
                foundIndex != -1 ? List.of(foundIndex) : List.of(),
                new ArrayList<>(array),

                found ? "Binary Search completed successfully" : "Binary Search completed: target not found",
                Map.of("java", 10, "python", 11, "cpp", 10, "pseudocode", 8),
                found ? "Finished! We found the element in logarithmic time." : "Finished! Confirmed element is not present.",
                "Binary Search finishes in O(log N) time complexity.",
                "Algorithm terminated cleanly.",
                "Time Complexity: O(log N), Auxiliary Space: O(1)"
        ));

        return new VisualizationResponse(
                SLUG,
                VisualizationType.ARRAY,
                steps
        );
    }
}
