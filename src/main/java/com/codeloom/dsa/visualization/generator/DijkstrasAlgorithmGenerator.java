package com.codeloom.dsa.visualization.generator;

import com.codeloom.dsa.visualization.dto.VisualizationRequest;
import com.codeloom.dsa.visualization.dto.VisualizationResponse;
import com.codeloom.dsa.visualization.dto.VisualizationStep;
import com.codeloom.dsa.visualization.entity.ActionType;
import com.codeloom.dsa.visualization.entity.VisualizationType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class DijkstrasAlgorithmGenerator implements VisualizationGenerator {

    private static final String SLUG = "dijkstras-algorithm";

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

        List<Integer> dist = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            dist.add(i == 0 ? 0 : 999);
        }

        // 1. Initial State
        steps.add(new VisualizationStep(
                stepNum++,
                ActionType.INITIAL,
                List.of(),
                new ArrayList<>(dist),
                "Initial Dijkstra shortest distance table. Source vertex 0 dist = 0, others = 999"
        ));

        // 2. Relaxation Steps
        boolean[] visited = new boolean[n];

        for (int count = 0; count < n; count++) {
            int u = -1;
            int minDist = Integer.MAX_VALUE;

            for (int i = 0; i < n; i++) {
                if (!visited[i] && dist.get(i) < minDist) {
                    minDist = dist.get(i);
                    u = i;
                }
            }

            if (u == -1) break;
            visited[u] = true;

            steps.add(new VisualizationStep(
                    stepNum++,
                    ActionType.SELECT,
                    List.of(u),
                    new ArrayList<>(dist),
                    String.format("Selected unvisited vertex %d with minimum distance %d", u, dist.get(u))
            ));

            int left = 2 * u + 1;
            int right = 2 * u + 2;

            if (left < n && !visited[left]) {
                int weight = Math.abs(array.get(left));
                int newDist = dist.get(u) + weight;
                if (newDist < dist.get(left)) {
                    dist.set(left, newDist);
                    steps.add(new VisualizationStep(
                            stepNum++,
                            ActionType.UPDATE,
                            List.of(left),
                            new ArrayList<>(dist),
                            String.format("Relaxed edge %d -> %d (weight %d). Updated shortest distance of %d to %d", u, left, weight, left, newDist)
                    ));
                }
            }

            if (right < n && !visited[right]) {
                int weight = Math.abs(array.get(right));
                int newDist = dist.get(u) + weight;
                if (newDist < dist.get(right)) {
                    dist.set(right, newDist);
                    steps.add(new VisualizationStep(
                            stepNum++,
                            ActionType.UPDATE,
                            List.of(right),
                            new ArrayList<>(dist),
                            String.format("Relaxed edge %d -> %d (weight %d). Updated shortest distance of %d to %d", u, right, weight, right, newDist)
                    ));
                }
            }
        }

        // 3. Completion Step
        steps.add(new VisualizationStep(
                stepNum,
                ActionType.COMPLETE,
                List.of(),
                new ArrayList<>(dist),
                "Dijkstra's Shortest Path Algorithm completed!"
        ));

        return new VisualizationResponse(
                SLUG,
                VisualizationType.GRAPH,
                steps
        );
    }
}
