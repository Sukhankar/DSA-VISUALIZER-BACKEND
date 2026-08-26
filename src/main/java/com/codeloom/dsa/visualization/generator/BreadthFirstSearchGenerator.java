package com.codeloom.dsa.visualization.generator;

import com.codeloom.dsa.visualization.dto.VisualizationRequest;
import com.codeloom.dsa.visualization.dto.VisualizationResponse;
import com.codeloom.dsa.visualization.dto.VisualizationStep;
import com.codeloom.dsa.visualization.entity.ActionType;
import com.codeloom.dsa.visualization.entity.VisualizationType;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

@Component
public class BreadthFirstSearchGenerator implements VisualizationGenerator {

    private static final String SLUG = "breadth-first-search";

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
                "Initial Graph BFS. Start vertex at index 0"
        ));

        // 2. BFS Queue Logic
        Deque<Integer> queue = new ArrayDeque<>();
        boolean[] visited = new boolean[n];

        queue.add(0);
        visited[0] = true;

        steps.add(new VisualizationStep(
                stepNum++,
                ActionType.SELECT,
                List.of(0),
                new ArrayList<>(array),
                "Enqueued start vertex 0 (" + array.get(0) + ")"
        ));

        while (!queue.isEmpty()) {
            int curr = queue.poll();

            steps.add(new VisualizationStep(
                    stepNum++,
                    ActionType.VISIT,
                    List.of(curr),
                    new ArrayList<>(array),
                    String.format("Dequeued and visited vertex %d (val=%d)", curr, array.get(curr))
            ));

            // Neighbors in adjacency list representation
            int left = 2 * curr + 1;
            int right = 2 * curr + 2;

            if (left < n && !visited[left]) {
                visited[left] = true;
                queue.add(left);
                steps.add(new VisualizationStep(
                        stepNum++,
                        ActionType.INSERT,
                        List.of(left),
                        new ArrayList<>(array),
                        String.format("Enqueued neighbor vertex %d (val=%d)", left, array.get(left))
                ));
            }

            if (right < n && !visited[right]) {
                visited[right] = true;
                queue.add(right);
                steps.add(new VisualizationStep(
                        stepNum++,
                        ActionType.INSERT,
                        List.of(right),
                        new ArrayList<>(array),
                        String.format("Enqueued neighbor vertex %d (val=%d)", right, array.get(right))
                ));
            }
        }

        // 3. Completion Step
        steps.add(new VisualizationStep(
                stepNum,
                ActionType.COMPLETE,
                List.of(),
                new ArrayList<>(array),
                "Breadth-First Search completed! All reachable vertices visited."
        ));

        return new VisualizationResponse(
                SLUG,
                VisualizationType.GRAPH,
                steps
        );
    }
}
