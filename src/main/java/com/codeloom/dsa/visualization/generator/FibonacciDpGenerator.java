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
public class FibonacciDpGenerator implements VisualizationGenerator {

    private static final String SLUG = "fibonacci-dynamic-programming";

    @Override
    public boolean supports(String algorithmSlug) {
        return SLUG.equalsIgnoreCase(algorithmSlug);
    }

    @Override
    public VisualizationResponse generate(String algorithmSlug, VisualizationRequest request) {
        List<VisualizationStep> steps = new ArrayList<>();
        int stepNum = 1;

        int n = Math.min(15, request.target() != null ? request.target() : Math.max(1, request.input().size()));
        List<Integer> dpTable = new ArrayList<>();

        // 1. Initial State
        steps.add(new VisualizationStep(
                stepNum++,
                ActionType.INITIAL,
                List.of(),
                new ArrayList<>(dpTable),
                "Initializing empty DP table for Fibonacci calculation F(n), n = " + n
        ));

        // Base case 0
        dpTable.add(0);
        steps.add(new VisualizationStep(
                stepNum++,
                ActionType.INSERT,
                List.of(0),
                new ArrayList<>(dpTable),
                "Base case F(0) = 0 initialized"
        ));

        if (n >= 1) {
            // Base case 1
            dpTable.add(1);
            steps.add(new VisualizationStep(
                    stepNum++,
                    ActionType.INSERT,
                    List.of(1),
                    new ArrayList<>(dpTable),
                    "Base case F(1) = 1 initialized"
            ));
        }

        // DP loop
        for (int i = 2; i <= n; i++) {
            int prev1 = dpTable.get(i - 1);
            int prev2 = dpTable.get(i - 2);
            int current = prev1 + prev2;

            steps.add(new VisualizationStep(
                    stepNum++,
                    ActionType.COMPARE,
                    List.of(i - 1, i - 2),
                    new ArrayList<>(dpTable),
                    String.format("Computing F(%d) = F(%d) + F(%d) -> %d + %d", i, i - 1, i - 2, prev1, prev2)
            ));

            dpTable.add(current);
            steps.add(new VisualizationStep(
                    stepNum++,
                    ActionType.INSERT,
                    List.of(i),
                    new ArrayList<>(dpTable),
                    String.format("Stored F(%d) = %d in DP table", i, current)
            ));
        }

        // 3. Completion Step
        steps.add(new VisualizationStep(
                stepNum,
                ActionType.COMPLETE,
                List.of(),
                new ArrayList<>(dpTable),
                String.format("Fibonacci DP completed! F(%d) = %d.", n, dpTable.get(dpTable.size() - 1))
        ));

        return new VisualizationResponse(
                SLUG,
                VisualizationType.DP_TABLE,
                steps
        );
    }
}
