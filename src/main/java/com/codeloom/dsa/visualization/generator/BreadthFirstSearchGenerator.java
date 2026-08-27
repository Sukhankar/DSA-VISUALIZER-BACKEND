package com.codeloom.dsa.visualization.generator;

import com.codeloom.dsa.visualization.dto.GraphEdgeDto;
import com.codeloom.dsa.visualization.dto.GraphVisualizationRequest;
import com.codeloom.dsa.visualization.dto.VisualizationRequest;
import com.codeloom.dsa.visualization.dto.VisualizationResponse;
import com.codeloom.dsa.visualization.dto.VisualizationStep;
import com.codeloom.dsa.visualization.entity.ActionType;
import com.codeloom.dsa.visualization.entity.VisualizationType;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class BreadthFirstSearchGenerator implements VisualizationGenerator {

    private static final String SLUG = "breadth-first-search";

    @Override
    public boolean supports(String algorithmSlug) {
        return SLUG.equalsIgnoreCase(algorithmSlug);
    }

    @Override
    public VisualizationResponse generate(String algorithmSlug, VisualizationRequest request) {
        if (request.graph() != null) {
            return generateGraphBfs(request.graph());
        }

        if (request.input() == null || request.input().isEmpty()) {
            throw new IllegalArgumentException("Breadth-First Search requires graph payload or non-empty input list");
        }

        return generateArrayBfs(request.input());
    }

    private VisualizationResponse generateGraphBfs(GraphVisualizationRequest graph) {
        if (graph.nodes() == null || graph.nodes().isEmpty()) {
            throw new IllegalArgumentException("Graph nodes list must not be empty");
        }
        if (graph.startNode() == null || !graph.nodes().contains(graph.startNode())) {
            throw new IllegalArgumentException("Start node must be present in graph nodes");
        }

        // Build deterministic adjacency list based on input edge order
        Map<String, List<String>> adjList = new LinkedHashMap<>();
        for (String node : graph.nodes()) {
            if (node == null) {
                throw new IllegalArgumentException("Graph contains null node value");
            }
            adjList.put(node, new ArrayList<>());
        }

        if (graph.edges() != null) {
            for (GraphEdgeDto edge : graph.edges()) {
                if (edge.from() == null || edge.to() == null || !adjList.containsKey(edge.from()) || !adjList.containsKey(edge.to())) {
                    throw new IllegalArgumentException("Edge references an unknown or null graph node");
                }
                adjList.get(edge.from()).add(edge.to());
            }
        }

        List<VisualizationStep> steps = new ArrayList<>();
        int stepNum = 1;

        Queue<String> queue = new ArrayDeque<>();
        Set<String> visited = new LinkedHashSet<>();

        String startNode = graph.startNode();

        // 1. Initial Step
        steps.add(new VisualizationStep(
                stepNum++,
                ActionType.INITIAL,
                List.of(),
                List.of(),
                null,
                List.of(),
                List.of(startNode),
                "Starting Breadth-First Search from node " + startNode
        ));

        visited.add(startNode);
        queue.add(startNode);

        while (!queue.isEmpty()) {
            String curr = queue.poll();

            steps.add(new VisualizationStep(
                    stepNum++,
                    ActionType.VISIT,
                    List.of(),
                    List.of(),
                    curr,
                    new ArrayList<>(visited),
                    new ArrayList<>(queue),
                    "Visiting node " + curr
            ));

            List<String> neighbors = adjList.getOrDefault(curr, List.of());
            List<String> newlyAdded = new ArrayList<>();

            for (String nbr : neighbors) {
                if (!visited.contains(nbr)) {
                    visited.add(nbr);
                    queue.add(nbr);
                    newlyAdded.add(nbr);
                }
            }

            if (!newlyAdded.isEmpty()) {
                steps.add(new VisualizationStep(
                        stepNum++,
                        ActionType.INSERT,
                        List.of(),
                        List.of(),
                        curr,
                        new ArrayList<>(visited),
                        new ArrayList<>(queue),
                        "Added neighbor(s) " + newlyAdded + " to the queue"
                ));
            }
        }

        // 3. Complete Step
        steps.add(new VisualizationStep(
                stepNum,
                ActionType.COMPLETE,
                List.of(),
                List.of(),
                null,
                new ArrayList<>(visited),
                List.of(),
                "Breadth-First Search completed! Traversal order: " + new ArrayList<>(visited)
        ));

        return new VisualizationResponse(
                SLUG,
                VisualizationType.GRAPH,
                steps
        );
    }

    private VisualizationResponse generateArrayBfs(List<Integer> input) {
        List<Integer> array = new ArrayList<>(input);
        List<VisualizationStep> steps = new ArrayList<>();
        int stepNum = 1;
        int n = array.size();

        steps.add(new VisualizationStep(
                stepNum++,
                ActionType.INITIAL,
                List.of(),
                new ArrayList<>(array),
                "Initial Graph BFS. Start vertex at index 0"
        ));

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
