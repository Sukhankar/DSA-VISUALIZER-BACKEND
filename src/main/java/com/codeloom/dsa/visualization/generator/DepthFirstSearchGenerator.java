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
public class DepthFirstSearchGenerator implements VisualizationGenerator {

    private static final String SLUG = "depth-first-search";

    @Override
    public boolean supports(String algorithmSlug) {
        return SLUG.equalsIgnoreCase(algorithmSlug);
    }

    @Override
    public VisualizationResponse generate(String algorithmSlug, VisualizationRequest request) {
        if (request.graph() != null) {
            return generateGraphDfs(request.graph());
        }

        if (request.input() == null || request.input().isEmpty()) {
            throw new IllegalArgumentException("Depth-First Search requires graph payload or non-empty input list");
        }

        return generateArrayDfs(request.input());
    }

    private VisualizationResponse generateGraphDfs(GraphVisualizationRequest graph) {
        if (graph.nodes() == null || graph.nodes().isEmpty()) {
            throw new IllegalArgumentException("Graph nodes list must not be empty");
        }
        if (graph.startNode() == null || !graph.nodes().contains(graph.startNode())) {
            throw new IllegalArgumentException("Start node must be present in graph nodes");
        }

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

        Set<String> visited = new LinkedHashSet<>();
        Deque<String> stack = new ArrayDeque<>();
        int[] stepCounter = {stepNum};

        String startNode = graph.startNode();

        // 1. Initial Step (frontier[0] = bottom, frontier[last] = top)
        steps.add(new VisualizationStep(
                stepCounter[0]++,
                ActionType.INITIAL,
                List.of(),
                List.of(),
                null,
                List.of(),
                List.of(startNode),
                "Starting Depth-First Search from node " + startNode
        ));

        dfsRecursive(startNode, adjList, visited, stack, steps, stepCounter);

        // 3. Complete Step
        steps.add(new VisualizationStep(
                stepCounter[0],
                ActionType.COMPLETE,
                List.of(),
                List.of(),
                null,
                new ArrayList<>(visited),
                List.of(),
                "Depth-First Search completed! Traversal order: " + new ArrayList<>(visited)
        ));

        return new VisualizationResponse(
                SLUG,
                VisualizationType.GRAPH,
                steps
        );
    }

    private void dfsRecursive(String curr, Map<String, List<String>> adjList, Set<String> visited,
                              Deque<String> stack, List<VisualizationStep> steps, int[] stepCounter) {
        visited.add(curr);
        stack.push(curr);

        // Active call stack representation: bottom at index 0, top at last index
        List<String> activeStack = new ArrayList<>(stack);
        Collections.reverse(activeStack);

        steps.add(new VisualizationStep(
                stepCounter[0]++,
                ActionType.VISIT,
                List.of(),
                List.of(),
                curr,
                new ArrayList<>(visited),
                activeStack,
                "Visiting node " + curr + " (stack top)"
        ));

        List<String> neighbors = adjList.getOrDefault(curr, List.of());
        for (String nbr : neighbors) {
            if (!visited.contains(nbr)) {
                dfsRecursive(nbr, adjList, visited, stack, steps, stepCounter);
            }
        }

        stack.pop();
    }

    private VisualizationResponse generateArrayDfs(List<Integer> input) {
        List<Integer> array = new ArrayList<>(input);
        List<VisualizationStep> steps = new ArrayList<>();
        int stepNum = 1;
        int n = array.size();

        steps.add(new VisualizationStep(
                stepNum++,
                ActionType.INITIAL,
                List.of(),
                new ArrayList<>(array),
                "Initial Graph DFS. Start vertex at index 0"
        ));

        boolean[] visited = new boolean[n];
        int[] currentStep = {stepNum};

        arrayDfsRecursive(0, array, visited, steps, currentStep);

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

    private void arrayDfsRecursive(int u, List<Integer> array, boolean[] visited, List<VisualizationStep> steps, int[] stepNum) {
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
            arrayDfsRecursive(left, array, visited, steps, stepNum);
        }

        if (right < array.size() && !visited[right]) {
            steps.add(new VisualizationStep(
                    stepNum[0]++,
                    ActionType.SELECT,
                    List.of(right),
                    new ArrayList<>(array),
                    String.format("Exploring edge %d -> %d", u, right)
            ));
            arrayDfsRecursive(right, array, visited, steps, stepNum);
        }
    }
}
