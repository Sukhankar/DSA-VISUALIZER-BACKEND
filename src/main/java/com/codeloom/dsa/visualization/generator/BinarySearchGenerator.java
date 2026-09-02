package com.codeloom.dsa.visualization.generator;

import com.codeloom.dsa.visualization.dto.VisualizationRequest;
import com.codeloom.dsa.visualization.dto.VisualizationResponse;
import com.codeloom.dsa.visualization.dto.VisualizationStep;
import com.codeloom.dsa.visualization.entity.ActionType;
import com.codeloom.dsa.visualization.entity.VisualizationType;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class BinarySearchGenerator implements VisualizationGenerator {

    private static final String SLUG = "binary-search";

    @Override
    public boolean supports(String algorithmSlug) {
        return SLUG.equalsIgnoreCase(algorithmSlug);
    }

    @Override
    public VisualizationResponse generate(String algorithmSlug, VisualizationRequest request) {
        if (request != null && request.input() != null && request.target() == null) {
            throw new IllegalArgumentException("Target value is required for binary search.");
        }
        if (request != null && request.input() != null && !isSorted(request.input())) {
            throw new IllegalArgumentException("Input array must be sorted for binary search.");
        }

        List<Integer> array = (request != null && request.input() != null && !request.input().isEmpty())
                ? new ArrayList<>(request.input())
                : List.of(1, 3, 5, 7, 9, 11, 13);
        int target = (request != null && request.target() != null) ? request.target() : 7;

        List<VisualizationStep> steps = new ArrayList<>();
        int stepNum = 1;

        int left = 0;
        int right = array.size() - 1;

        // 1. Initial Step
        steps.add(new VisualizationStep(
                stepNum++,
                ActionType.INITIAL,
                List.of(left, right),
                new ArrayList<>(array),
                String.format("Binary Search initialized for target = %d across sorted array.", target),
                Map.of("java", 1, "python", 1, "cpp", 1),
                String.format("Target is %d. Pointers set: LEFT = 0, RIGHT = %d.", target, right),
                "Binary Search requires a sorted array to prune half the remaining search space at every step.",
                String.format("Initial range spans indices [%d..%d].", left, right),
                "Time: O(log N) | Space: O(1)",
                Map.of("left", left, "right", right, "target", target)
        ));

        boolean found = false;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            int midVal = array.get(mid);

            // 2. Mid Pointer Calculation & Comparison Step
            steps.add(new VisualizationStep(
                    stepNum++,
                    ActionType.COMPARE,
                    List.of(mid),
                    new ArrayList<>(array),
                    String.format("Calculated MID = %d (val = %d). Comparing with target %d.", mid, midVal, target),
                    Map.of("java", 3, "python", 3, "cpp", 3),
                    String.format("MID index computed as left + (right - left) / 2 = %d.", mid),
                    String.format("Comparing mid value %d against target %d.", midVal, target),
                    String.format("Search interval narrowed to [%d..%d]. Checking mid element.", left, right),
                    "Comparison takes O(1) time.",
                    Map.of("left", left, "mid", mid, "right", right, "midVal", midVal, "target", target)
            ));

            if (midVal == target) {
                found = true;
                steps.add(new VisualizationStep(
                        stepNum++,
                        ActionType.FOUND,
                        List.of(mid),
                        new ArrayList<>(array),
                        String.format("Target %d located at index MID = %d!", target, mid),
                        Map.of("java", 4, "python", 4, "cpp", 4),
                        String.format("Element at index %d matches target %d.", mid, target),
                        "Search succeeds immediately upon finding exact match.",
                        String.format("Target found at index %d.", mid),
                        "Search complete in O(log N) steps.",
                        Map.of("left", left, "mid", mid, "right", right, "foundIndex", mid, "found", true)
                ));
                break;
            } else if (midVal < target) {
                int oldLeft = left;
                left = mid + 1;
                steps.add(new VisualizationStep(
                        stepNum++,
                        ActionType.UPDATE,
                        List.of(left, right),
                        new ArrayList<>(array),
                        String.format("midVal (%d) < target (%d). Target must be in right half. Updating LEFT = %d.", midVal, target, left),
                        Map.of("java", 5, "python", 5, "cpp", 5),
                        String.format("Since array is sorted and %d < %d, all elements <= index %d can be eliminated.", midVal, target, mid),
                        String.format("Left bound updated from %d to %d.", oldLeft, left),
                        String.format("Eliminating left subsegment [0..%d].", mid),
                        "Reduces search space by ~50%.",
                        Map.of("left", left, "right", right, "target", target)
                ));
            } else {
                int oldRight = right;
                right = mid - 1;
                steps.add(new VisualizationStep(
                        stepNum++,
                        ActionType.UPDATE,
                        List.of(left, right),
                        new ArrayList<>(array),
                        String.format("midVal (%d) > target (%d). Target must be in left half. Updating RIGHT = %d.", midVal, target, right),
                        Map.of("java", 6, "python", 6, "cpp", 6),
                        String.format("Since array is sorted and %d > %d, all elements >= index %d can be eliminated.", midVal, target, mid),
                        String.format("Right bound updated from %d to %d.", oldRight, right),
                        String.format("Eliminating right subsegment [%d..end].", mid),
                        "Reduces search space by ~50%.",
                        Map.of("left", left, "right", right, "target", target)
                ));
            }
        }

        if (!found) {
            steps.add(new VisualizationStep(
                    stepNum++,
                    ActionType.NOT_FOUND,
                    List.of(),
                    new ArrayList<>(array),
                    String.format("Search interval exhausted (LEFT > RIGHT). Target %d is not in array.", target),
                    Map.of("java", 7, "python", 7, "cpp", 7),
                    "Left pointer crossed right pointer without locating match.",
                    "Proves target does not exist in the given sorted array.",
                    "Search terminated with negative result.",
                    "Total comparisons: O(log N).",
                    Map.of("found", false)
            ));
        }

        steps.add(new VisualizationStep(
                stepNum,
                ActionType.COMPLETE,
                List.of(),
                new ArrayList<>(array),
                "Binary Search completed processing.",
                Map.of("java", 8, "python", 8, "cpp", 8),
                "Execution finished.",
                "Logarithmic search time O(log N) verified.",
                "Process complete.",
                "Final Complexity: O(log N) Time, O(1) Space",
                Map.of("found", found)
        ));

        return new VisualizationResponse(SLUG, VisualizationType.ARRAY, steps);
    }

    private boolean isSorted(List<Integer> list) {
        if (list == null || list.size() <= 1) return true;
        for (int i = 0; i < list.size() - 1; i++) {
            if (list.get(i) > list.get(i + 1)) return false;
        }
        return true;
    }
}
