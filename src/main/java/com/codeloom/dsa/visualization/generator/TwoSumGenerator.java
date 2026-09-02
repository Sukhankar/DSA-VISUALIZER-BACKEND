package com.codeloom.dsa.visualization.generator;

import com.codeloom.dsa.visualization.dto.VisualizationRequest;
import com.codeloom.dsa.visualization.dto.VisualizationResponse;
import com.codeloom.dsa.visualization.dto.VisualizationStep;
import com.codeloom.dsa.visualization.entity.ActionType;
import com.codeloom.dsa.visualization.entity.VisualizationType;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class TwoSumGenerator implements VisualizationGenerator {

    private static final String SLUG = "two-sum";

    @Override
    public boolean supports(String algorithmSlug) {
        return SLUG.equalsIgnoreCase(algorithmSlug);
    }

    @Override
    public VisualizationResponse generate(String algorithmSlug, VisualizationRequest request) {
        List<Integer> array = (request != null && request.input() != null && !request.input().isEmpty())
                ? new ArrayList<>(request.input())
                : List.of(2, 7, 11, 15);
        int target = (request != null && request.target() != null) ? request.target() : 9;

        List<VisualizationStep> steps = new ArrayList<>();
        int stepNum = 1;
        Map<Integer, Integer> map = new HashMap<>();

        // 1. Initial Step
        steps.add(new VisualizationStep(
                stepNum++,
                ActionType.INITIAL,
                List.of(),
                new ArrayList<>(array),
                String.format("Searching for two numbers in array that add up to target = %d.", target),
                Map.of("java", 1, "python", 1, "cpp", 1),
                "We need to find indices i and j such that array[i] + array[j] = target.",
                "Using a Hash Map allows complement lookup in O(1) time.",
                "Initialize hash table to record seen values.",
                "Time: O(N) amortized | Space: O(N) hash table",
                Map.of("target", target)
        ));

        boolean foundPair = false;
        for (int i = 0; i < array.size(); i++) {
            int num = array.get(i);
            int complement = target - num;

            // 2. Compare / Check Complement Step
            steps.add(new VisualizationStep(
                    stepNum++,
                    ActionType.COMPARE,
                    List.of(i),
                    new ArrayList<>(array),
                    String.format("Inspecting index %d (val = %d). Complement needed = %d - %d = %d.", i, num, target, num, complement),
                    Map.of("java", 3, "python", 3, "cpp", 3),
                    String.format("Checking if required complement %d exists in our hash table.", complement),
                    String.format("If hash table contains %d, we have found our target sum pair.", complement),
                    String.format("Looking up complement %d in map.", complement),
                    "Hash lookup takes O(1) average time.",
                    Map.of("currentIndex", i, "currentVal", num, "complement", complement, "seenMap", new HashMap<>(map))
            ));

            if (map.containsKey(complement)) {
                int prevIndex = map.get(complement);
                foundPair = true;

                // 3. Match Found Step
                steps.add(new VisualizationStep(
                        stepNum++,
                        ActionType.FOUND,
                        List.of(prevIndex, i),
                        new ArrayList<>(array),
                        String.format("Found pair! array[%d] (%d) + array[%d] (%d) = %d.", prevIndex, complement, i, num, target),
                        Map.of("java", 4, "python", 4, "cpp", 4),
                        String.format("Complement %d was previously stored at index %d.", complement, prevIndex),
                        String.format("The current number %d at index %d combined with %d at index %d equals target %d.", num, i, complement, prevIndex, target),
                        String.format("Match verified at indices [%d, %d].", prevIndex, i),
                        "Target pair identified. Algorithm terminates early.",
                        Map.of("solutionIndices", List.of(prevIndex, i), "found", true)
                ));
                break;
            } else {
                map.put(num, i);
                steps.add(new VisualizationStep(
                        stepNum++,
                        ActionType.UPDATE,
                        List.of(i),
                        new ArrayList<>(array),
                        String.format("Complement %d not found in map. Storing map[%d] = %d.", complement, num, i),
                        Map.of("java", 6, "python", 5, "cpp", 6),
                        String.format("Added number %d with index %d to hash table.", num, i),
                        "Future elements can now check against this number as their potential complement.",
                        String.format("Hash map updated: %s.", map),
                        "O(1) insertion into hash table.",
                        Map.of("seenMap", new HashMap<>(map))
                ));
            }
        }

        // 4. Complete Step
        String completeMsg = foundPair
                ? String.format("Two Sum completed! Solution indices verified for target = %d.", target)
                : String.format("Two Sum completed. No pair sums to target = %d.", target);

        steps.add(new VisualizationStep(
                stepNum,
                ActionType.COMPLETE,
                List.of(),
                new ArrayList<>(array),
                completeMsg,
                Map.of("java", 7, "python", 6, "cpp", 7),
                "Algorithm execution has finished.",
                "Single pass hash map traversal achieves optimal linear time complexity.",
                "Process complete.",
                "Final Complexity: O(N) Time, O(N) Space",
                Map.of("found", foundPair)
        ));

        return new VisualizationResponse(SLUG, VisualizationType.ARRAY, steps);
    }
}
