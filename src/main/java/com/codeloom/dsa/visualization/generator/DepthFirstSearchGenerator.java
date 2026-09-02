package com.codeloom.dsa.visualization.generator;

import com.codeloom.dsa.visualization.dto.*;
import com.codeloom.dsa.visualization.entity.ActionType;
import com.codeloom.dsa.visualization.entity.VisualizationType;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DepthFirstSearchGenerator implements VisualizationGenerator {

    private static final List<String> SUPPORTED_SLUGS = List.of(
            "depth-first-search",
            "dfs",
            "graph-dfs",
            "dfs-traversal"
    );

    @Override
    public boolean supports(String algorithmSlug) {
        if (algorithmSlug == null) return false;
        String s = algorithmSlug.toLowerCase();
        return SUPPORTED_SLUGS.contains(s) || (s.contains("dfs") && !s.contains("bfs"));
    }

    @Override
    public VisualizationResponse generate(String algorithmSlug, VisualizationRequest request) {
        GraphVisualizationRequest graphReq = (request != null && request.graph() != null)
                ? request.graph()
                : getDefaultGraph();

        return generateGraphDfs(algorithmSlug, graphReq);
    }

    private GraphVisualizationRequest getDefaultGraph() {
        List<GraphNodeDto> nodes = List.of(
                new GraphNodeDto("A", "A"),
                new GraphNodeDto("B", "B"),
                new GraphNodeDto("C", "C"),
                new GraphNodeDto("D", "D"),
                new GraphNodeDto("E", "E"),
                new GraphNodeDto("F", "F")
        );
        List<GraphEdgeDto> edges = List.of(
                new GraphEdgeDto("A-B", "A", "B", null),
                new GraphEdgeDto("A-C", "A", "C", null),
                new GraphEdgeDto("B-D", "B", "D", null),
                new GraphEdgeDto("B-E", "B", "E", null),
                new GraphEdgeDto("C-F", "C", "F", null),
                new GraphEdgeDto("E-F", "E", "F", null)
        );
        return new GraphVisualizationRequest(nodes, edges, false, false, "A", null);
    }

    private VisualizationResponse generateGraphDfs(String slug, GraphVisualizationRequest graph) {
        List<GraphNodeDto> nodes = graph.nodes() != null ? graph.nodes() : List.of();
        List<GraphEdgeDto> edges = graph.edges() != null ? graph.edges() : List.of();
        boolean isDirected = Boolean.TRUE.equals(graph.directed());
        boolean isWeighted = Boolean.TRUE.equals(graph.weighted());

        if (nodes.isEmpty()) {
            GraphVisualizationRequest def = getDefaultGraph();
            nodes = def.nodes();
            edges = def.edges();
        }

        List<String> nodeIds = nodes.stream().map(GraphNodeDto::id).toList();
        String startNode = (graph.startNode() != null && nodeIds.contains(graph.startNode()))
                ? graph.startNode()
                : nodeIds.get(0);

        Map<String, List<String>> adj = new LinkedHashMap<>();
        for (String id : nodeIds) {
            adj.put(id, new ArrayList<>());
        }

        for (GraphEdgeDto edge : edges) {
            String u = edge.source();
            String v = edge.target();
            if (adj.containsKey(u) && adj.containsKey(v)) {
                adj.get(u).add(v);
                if (!isDirected) {
                    adj.get(v).add(u);
                }
            }
        }

        List<VisualizationStep> steps = new ArrayList<>();
        int[] stepCounter = {1};

        Set<String> visited = new LinkedHashSet<>();
        Deque<String> stack = new ArrayDeque<>();
        List<String> traversedEdges = new ArrayList<>();

        // Initial step
        GraphStateSnapshot initSnap = new GraphStateSnapshot(
                nodes, edges, isDirected, isWeighted,
                List.of(startNode), new ArrayList<>(visited), List.of(), List.of(),
                List.of(), List.of(startNode), startNode, startNode, null,
                Map.of(), Map.of(), List.of(), List.of(), null, List.of(), List.of(), null, null,
                "Initialized DFS traversal starting from node " + startNode
        );
        steps.add(new VisualizationStep(stepCounter[0]++, ActionType.INITIAL, initSnap, initSnap.explanation(), Map.of(), null, null, null, null, null));

        dfsRecursive(startNode, nodes, edges, isDirected, isWeighted, adj, visited, stack, traversedEdges, steps, stepCounter);

        GraphStateSnapshot completeSnap = new GraphStateSnapshot(
                nodes, edges, isDirected, isWeighted,
                List.of(), new ArrayList<>(visited), List.of(), new ArrayList<>(traversedEdges),
                List.of(), List.of(), null, startNode, null,
                Map.of(), Map.of(), List.of(), List.of(), null, List.of(), List.of(), null, null,
                "DFS traversal completed! Visited nodes order: " + new ArrayList<>(visited)
        );
        steps.add(new VisualizationStep(stepCounter[0], ActionType.COMPLETE, completeSnap, completeSnap.explanation(), Map.of(), null, null, null, null, null));

        return new VisualizationResponse(
                slug != null ? slug : "depth-first-search",
                VisualizationType.GRAPH,
                steps
        );
    }

    private void dfsRecursive(String curr, List<GraphNodeDto> nodes, List<GraphEdgeDto> edges,
                              boolean isDirected, boolean isWeighted,
                              Map<String, List<String>> adj, Set<String> visited,
                              Deque<String> stack, List<String> traversedEdges,
                              List<VisualizationStep> steps, int[] stepCounter) {
        visited.add(curr);
        stack.push(curr);

        List<String> activeStack = new ArrayList<>(stack);
        Collections.reverse(activeStack);

        GraphStateSnapshot visitSnap = new GraphStateSnapshot(
                nodes, edges, isDirected, isWeighted,
                List.of(curr), new ArrayList<>(visited), List.of(), new ArrayList<>(traversedEdges),
                List.of(), activeStack, curr, null, null,
                Map.of(), Map.of(), List.of(), List.of(), null, List.of(), List.of(), null, null,
                "Pushed node " + curr + " to call stack & marked visited."
        );
        steps.add(new VisualizationStep(stepCounter[0]++, ActionType.VISIT, visitSnap, visitSnap.explanation(), Map.of(), null, null, null, null, null));

        for (String nbr : adj.getOrDefault(curr, List.of())) {
            String edgeId = curr + "-" + nbr;
            if (!visited.contains(nbr)) {
                traversedEdges.add(edgeId);

                GraphStateSnapshot edgeSnap = new GraphStateSnapshot(
                        nodes, edges, isDirected, isWeighted,
                        List.of(curr, nbr), new ArrayList<>(visited), List.of(edgeId), new ArrayList<>(traversedEdges),
                        List.of(), activeStack, curr, null, null,
                        Map.of(), Map.of(), List.of(), List.of(), null, List.of(), List.of(), null, null,
                        "Traversing unvisited edge " + curr + " -> " + nbr
                );
                steps.add(new VisualizationStep(stepCounter[0]++, ActionType.SELECT, edgeSnap, edgeSnap.explanation(), Map.of(), null, null, null, null, null));

                dfsRecursive(nbr, nodes, edges, isDirected, isWeighted, adj, visited, stack, traversedEdges, steps, stepCounter);
            }
        }

        stack.pop();
    }
}
