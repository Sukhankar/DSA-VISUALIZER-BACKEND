package com.codeloom.dsa.visualization.generator;

import com.codeloom.dsa.visualization.dto.VisualizationRequest;
import com.codeloom.dsa.visualization.dto.VisualizationResponse;
import com.codeloom.dsa.visualization.dto.VisualizationStep;
import com.codeloom.dsa.visualization.entity.ActionType;
import com.codeloom.dsa.visualization.entity.VisualizationType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TwoSumGenerator implements VisualizationGenerator {

    private static final String SLUG = "two-sum";

    @Override
    public boolean supports(String algorithmSlug) {
        return SLUG.equalsIgnoreCase(algorithmSlug);
    }

    @Override
    public VisualizationResponse generate(String algorithmSlug, VisualizationRequest request) {
        List<Integer> array = new ArrayList<>(request.input());
        List<VisualizationStep> steps = new ArrayList<>();
        int stepNum = 1;
        int target = request.target() != null ? request.target() : (array.size() >= 2 ? array.get(0) + array.get(1) : 0);

        // 1. Initial State
        steps.add(new VisualizationStep(
                stepNum++,
                ActionType.INITIAL,
                List.of(),
                new ArrayList<>(array),
                "Initial array. Target sum to find: " + target
        ));

        // 2. Two Sum Map Logic
        Map<Integer, Integer> map = new HashMap<>();
        boolean found = false;

        for (int i = 0; i < array.size(); i++) {
            int current = array.get(i);
            int complement = target - current;

            steps.add(new VisualizationStep(
                    stepNum++,
                    ActionType.COMPARE,
                    List.of(i),
                    new ArrayList<>(array),
                    String.format("Checking element %d at index %d. Needed complement: %d", current, i, complement)
            ));

            if (map.containsKey(complement)) {
                int prevIndex = map.get(complement);
                steps.add(new VisualizationStep(
                        stepNum++,
                        ActionType.SELECT,
                        List.of(prevIndex, i),
                        new ArrayList<>(array),
                        String.format("Found pair [%d, %d] at indices [%d, %d] summing to %d!", complement, current, prevIndex, i, target)
                ));
                found = true;
                break;
            }

            map.put(current, i);
            steps.add(new VisualizationStep(
                    stepNum++,
                    ActionType.UPDATE,
                    List.of(i),
                    new ArrayList<>(array),
                    String.format("Recorded value %d at index %d in lookup map", current, i)
            ));
        }

        // 3. Completion Step
        steps.add(new VisualizationStep(
                stepNum,
                ActionType.COMPLETE,
                List.of(),
                new ArrayList<>(array),
                found ? "Two Sum completed! Solution found." : "Two Sum completed! No matching pair found for target " + target
        ));

        return new VisualizationResponse(
                SLUG,
                VisualizationType.TWO_POINTER,
                steps
        );
    }
}
