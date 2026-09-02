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
public class KnapsackDpGenerator implements VisualizationGenerator {

    private static final List<String> SUPPORTED_SLUGS = List.of(
            "01-knapsack",
            "knapsack-problem",
            "0-1-knapsack-problem",
            "knapsack"
    );

    @Override
    public boolean supports(String algorithmSlug) {
        if (algorithmSlug == null) return false;
        String s = algorithmSlug.toLowerCase();
        return SUPPORTED_SLUGS.contains(s) || s.contains("knapsack");
    }

    @Override
    public VisualizationResponse generate(String algorithmSlug, VisualizationRequest request) {
        List<Integer> values = List.of(60, 100, 120);
        List<Integer> weights = List.of(10, 20, 30);
        int W = 50; // Capacity

        List<VisualizationStep> steps = new ArrayList<>();
        int stepNum = 1;

        steps.add(new VisualizationStep(
                stepNum++,
                ActionType.INITIAL,
                List.of(),
                List.of(0, 0, 0, 0, 0, 0),
                String.format("Initializing 0/1 Knapsack DP Table for Capacity W=%d with %d items.", W, values.size())
        ));

        // 1D DP Array representation for steps visualization
        int[] dp = new int[W + 1];

        for (int i = 0; i < values.size(); i++) {
            int v = values.get(i);
            int w = weights.get(i);

            for (int cap = W; cap >= w; cap--) {
                int option1 = dp[cap];
                int option2 = dp[cap - w] + v;

                if (option2 > option1) {
                    dp[cap] = option2;

                    List<Integer> dpSnapshot = new ArrayList<>();
                    for (int k = 0; k <= W; k += 10) {
                        dpSnapshot.add(dp[k]);
                    }

                    steps.add(new VisualizationStep(
                            stepNum++,
                            ActionType.UPDATE,
                            List.of(i, cap / 10),
                            dpSnapshot,
                            String.format("Item %d (val=%d, wt=%d): Updated DP table capacity %d → Max Value: %d", i + 1, v, w, cap, option2)
                    ));
                }
            }
        }

        List<Integer> finalDpSnapshot = new ArrayList<>();
        for (int k = 0; k <= W; k += 10) {
            finalDpSnapshot.add(dp[k]);
        }

        steps.add(new VisualizationStep(
                stepNum,
                ActionType.COMPLETE,
                List.of(W / 10),
                finalDpSnapshot,
                String.format("0/1 Knapsack DP computation completed! Maximum total value achieved for capacity %d = %d", W, dp[W])
        ));

        return new VisualizationResponse(
                algorithmSlug,
                VisualizationType.DP_TABLE,
                steps
        );
    }
}
