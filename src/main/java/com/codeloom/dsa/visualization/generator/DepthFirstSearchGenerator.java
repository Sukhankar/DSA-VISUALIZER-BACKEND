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
public class DepthFirstSearchGenerator implements VisualizationGenerator {

    private static final String SLUG = "depth-first-search";

    @Override
    public boolean supports(String algorithmSlug) {
        return SLUG.equalsIgnoreCase(algorithmSlug);
    }

    @Override
    public VisualizationResponse generate(String algorithmSlug, VisualizationRequest request) {
        List<Integer> array = new ArrayList<>(request.input());
        List<VisualizationStep> steps = new ArrayList<>();
        int stepNum = 1;
        int n = array.size();

        // 1. Initial State
        steps.add(new VisualizationStep(
                stepNum++,
                ActionType.INITIAL,
                List.of(),
                new ArrayList<>(array),
                "Initial Graph DFS. Start vertex at index 0"
        ));

        // 2. DFS Traversal Logic
        boolean[] visited = new boolean[n];
        int[] currentStep = {stepNum};

        dfs(0, array, visited, steps, currentStep);

        // 3. Completion Step
        steps.add(new VisualizationStep(
                currentStep[0],
                ActionType.COMPLETE,
                List.of(),
                new ArrayList<>(array),
                "Depth-First Search completed! Graph traversal finished."
        ));

        return new VisualizationResponse(
                SLUG,
                VisualizationType.GRAPH,
                steps
        );
    }

    private void dfs(int u, List<Integer> array, boolean[] visited, List<VisualizationStep> steps, int[] stepNum) {
        visited[u] = true;

        steps.add(new VisualizationStep(
                stepNum[0]++,
                ActionType.VISIT,
                List.of(u),
                new ArrayList<>(array),
                String.format("DFS visited vertex %d (val=%d)", u, array.get(u))
        ));

        int left = 2 * u + 1;
        int right = 2 * u + 2;

        if (left < array.size() && !visited[left]) {
            steps.add(new VisualizationStep(
                    stepNum[0]++,
                    ActionType.SELECT,
                    List.of(left),
                    new ArrayList<>(array),
                    String.format("Exploring edge %d -> %d", u, left)
            ));
            dfs(left, array, visited, steps, stepNum);
        }

        if (right < array.size() && !visited[right]) {
            steps.add(new VisualizationStep(
                    stepNum[0]++,
                    ActionType.SELECT,
                    List.of(right),
                    new ArrayList<>(array),
                    String.format("Exploring edge %d -> %d", u, right)
            ));
            dfs(right, array, visited, steps, stepNum);
        }
    }
}
