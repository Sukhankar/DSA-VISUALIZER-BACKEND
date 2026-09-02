package com.codeloom.dsa.visualization.generator;

import com.codeloom.dsa.visualization.dto.VisualizationRequest;
import com.codeloom.dsa.visualization.dto.VisualizationResponse;
import com.codeloom.dsa.visualization.dto.VisualizationStep;
import com.codeloom.dsa.visualization.entity.ActionType;
import com.codeloom.dsa.visualization.entity.VisualizationType;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class KadanesAlgorithmGenerator implements VisualizationGenerator {

    private static final String SLUG = "kadanes-algorithm";

    @Override
    public boolean supports(String algorithmSlug) {
        return SLUG.equalsIgnoreCase(algorithmSlug);
    }

    @Override
    public VisualizationResponse generate(String algorithmSlug, VisualizationRequest request) {
        List<Integer> array = (request != null && request.input() != null && !request.input().isEmpty())
                ? new ArrayList<>(request.input())
                : List.of(-2, 1, -3, 4, -1, 2, 1, -5, 4);

        List<VisualizationStep> steps = new ArrayList<>();
        int stepNum = 1;

        int currentSum = array.get(0);
        int maxSum = array.get(0);
        int bestStart = 0;
        int bestEnd = 0;
        int tempStart = 0;

        // 1. Initial Step
        steps.add(new VisualizationStep(
                stepNum++,
                ActionType.INITIAL,
                List.of(0),
                new ArrayList<>(array),
                String.format("Kadane's Algorithm starting. Initialized currentSum = %d, maxSum = %d.", currentSum, maxSum),
                Map.of("java", 1, "python", 1, "cpp", 1),
                "Kadane's Algorithm computes the maximum contiguous subarray sum in O(N) linear time.",
                "At each element, we decide whether to add it to the existing subarray or start a new subarray.",
                String.format("Initialized at index 0 (val = %d).", array.get(0)),
                "Time: O(N) | Space: O(1)",
                Map.of("currentSum", currentSum, "maxSum", maxSum, "currentIndex", 0)
        ));

        for (int i = 1; i < array.size(); i++) {
            int num = array.get(i);

            // 2. Decision Step: Extend existing or start new
            if (num > currentSum + num) {
                currentSum = num;
                tempStart = i;
            } else {
                currentSum += num;
            }

            boolean isNewMax = false;
            if (currentSum > maxSum) {
                maxSum = currentSum;
                bestStart = tempStart;
                bestEnd = i;
                isNewMax = true;
            }

            steps.add(new VisualizationStep(
                    stepNum++,
                    isNewMax ? ActionType.UPDATE : ActionType.COMPARE,
                    List.of(tempStart, i),
                    new ArrayList<>(array),
                    String.format("Index %d (val = %d): currentSum = %d, maxSum = %d.", i, num, currentSum, maxSum),
                    Map.of("java", 3, "python", 3, "cpp", 3),
                    String.format("Evaluating index %d (val = %d). currentSum updated to %d.", i, num, currentSum),
                    isNewMax
                            ? String.format("New global maximum subarray sum found! maxSum updated to %d (span [%d..%d]).", maxSum, bestStart, bestEnd)
                            : String.format("currentSum (%d) <= global maxSum (%d). Global maximum remains unchanged.", currentSum, maxSum),
                    String.format("Inspecting index %d. Subarray window [%d..%d].", i, tempStart, i),
                    "O(1) transition per element.",
                    Map.of("currentSum", currentSum, "maxSum", maxSum, "currentIndex", i, "bestStart", bestStart, "bestEnd", bestEnd)
            ));
        }

        // 3. Complete Step
        steps.add(new VisualizationStep(
                stepNum,
                ActionType.COMPLETE,
                List.of(bestStart, bestEnd),
                new ArrayList<>(array),
                String.format("Kadane's Algorithm completed! Maximum contiguous subarray sum = %d (subarray indices [%d..%d]).", maxSum, bestStart, bestEnd),
                Map.of("java", 6, "python", 6, "cpp", 6),
                "Single pass complete.",
                "Kadane's dynamic programming window optimization verified.",
                String.format("Maximum subarray sum = %d.", maxSum),
                "Final Complexity: O(N) Time, O(1) Space",
                Map.of("maxSum", maxSum, "bestStart", bestStart, "bestEnd", bestEnd)
        ));

        return new VisualizationResponse(SLUG, VisualizationType.ARRAY, steps);
    }
}
