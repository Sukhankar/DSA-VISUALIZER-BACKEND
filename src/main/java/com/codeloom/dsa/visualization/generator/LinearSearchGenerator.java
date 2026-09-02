package com.codeloom.dsa.visualization.generator;

import com.codeloom.dsa.visualization.dto.VisualizationRequest;
import com.codeloom.dsa.visualization.dto.VisualizationResponse;
import com.codeloom.dsa.visualization.dto.VisualizationStep;
import com.codeloom.dsa.visualization.entity.ActionType;
import com.codeloom.dsa.visualization.entity.VisualizationType;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class LinearSearchGenerator implements VisualizationGenerator {

    private static final String SLUG = "linear-search";

    @Override
    public boolean supports(String algorithmSlug) {
        return SLUG.equalsIgnoreCase(algorithmSlug);
    }

    @Override
    public VisualizationResponse generate(String algorithmSlug, VisualizationRequest request) {
        if (request != null && request.input() != null && request.target() == null) {
            throw new IllegalArgumentException("Target value is required for linear search.");
        }

        List<Integer> array = (request != null && request.input() != null && !request.input().isEmpty())
                ? new ArrayList<>(request.input())
                : List.of(5, 2, 8, 1, 9);
        int target = (request != null && request.target() != null) ? request.target() : 8;

        List<VisualizationStep> steps = new ArrayList<>();
        int stepNum = 1;

        // 1. Initial Step
        steps.add(new VisualizationStep(
                stepNum++,
                ActionType.INITIAL,
                List.of(0),
                new ArrayList<>(array),
                String.format("Linear Search starting for target = %d across array of size %d.", target, array.size()),
                Map.of("java", 1, "python", 1, "cpp", 1),
                String.format("Will inspect elements sequentially starting from index 0 for value %d.", target),
                "Linear search evaluates elements one by one without assuming order.",
                "Starting at index 0.",
                "Time: O(N) worst case | Space: O(1)",
                Map.of("target", target, "currentIndex", 0)
        ));

        boolean found = false;
        for (int i = 0; i < array.size(); i++) {
            int val = array.get(i);

            // 2. Inspection / Compare Step
            steps.add(new VisualizationStep(
                    stepNum++,
                    ActionType.COMPARE,
                    List.of(i),
                    new ArrayList<>(array),
                    String.format("Inspecting index %d (val = %d). Comparing with target %d.", i, val, target),
                    Map.of("java", 3, "python", 3, "cpp", 3),
                    String.format("Checking element at index %d: is %d equal to target %d?", i, val, target),
                    "Evaluates current element equality against target.",
                    String.format("Inspecting index %d.", i),
                    "Comparison takes O(1) time.",
                    Map.of("currentIndex", i, "currentVal", val, "target", target)
            ));

            if (val == target) {
                found = true;
                steps.add(new VisualizationStep(
                        stepNum++,
                        ActionType.FOUND,
                        List.of(i),
                        new ArrayList<>(array),
                        String.format("Target %d found at index %d!", target, i),
                        Map.of("java", 4, "python", 4, "cpp", 4),
                        String.format("Value at index %d equals target %d.", i, target),
                        "Sequential match confirmed.",
                        String.format("Match located at index %d.", i),
                        "Search completes early on match.",
                        Map.of("foundIndex", i, "found", true)
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
                    String.format("Reached end of array. Target %d not found.", target),
                    Map.of("java", 5, "python", 5, "cpp", 5),
                    "Inspected all elements without finding a match.",
                    "Target is absent from array.",
                    "Search terminated.",
                    "N comparisons performed.",
                    Map.of("found", false)
            ));
        }

        steps.add(new VisualizationStep(
                stepNum,
                ActionType.COMPLETE,
                List.of(),
                new ArrayList<>(array),
                "Linear Search completed processing.",
                Map.of("java", 6, "python", 6, "cpp", 6),
                "Execution complete.",
                "Optimal O(N) sequential search finished.",
                "Process complete.",
                "Final Complexity: O(N) Time, O(1) Space",
                Map.of("found", found)
        ));

        return new VisualizationResponse(SLUG, VisualizationType.ARRAY, steps);
    }
}
