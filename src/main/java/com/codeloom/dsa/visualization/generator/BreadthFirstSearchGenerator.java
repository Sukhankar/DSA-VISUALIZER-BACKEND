package com.codeloom.dsa.visualization.generator;

import com.codeloom.dsa.visualization.dto.*;
import com.codeloom.dsa.visualization.entity.ActionType;
import com.codeloom.dsa.visualization.entity.VisualizationType;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class BreadthFirstSearchGenerator implements VisualizationGenerator {

    private static final List<String> SUPPORTED_SLUGS = List.of(
            "breadth-first-search",
            "bfs",
            "graph-bfs",
            "bfs-traversal"
    );

    @Override
    public boolean supports(String algorithmSlug) {
        if (algorithmSlug == null) return false;
        String s = algorithmSlug.toLowerCase();
        return SUPPORTED_SLUGS.contains(s) || (s.contains("bfs") && !s.contains("dfs"));
    }

    @Override
    public VisualizationResponse generate(String algorithmSlug, VisualizationRequest request) {
        GraphVisualizationRequest graphReq = (request != null && request.graph() != null)
                ? request.graph()
                : getDefaultGraph();

        return generateGraphBfs(algorithmSlug, graphReq);
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

    private VisualizationResponse generateGraphBfs(String slug, GraphVisualizationRequest graph) {
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
        int stepNum = 1;

        Queue<String> queue = new ArrayDeque<>();
        Set<String> visited = new LinkedHashSet<>();
        List<String> traversedEdges = new ArrayList<>();

        queue.add(startNode);
        visited.add(startNode);

        // Step 1: Initial
        GraphStateSnapshot initSnap = new GraphStateSnapshot(
                nodes, edges, isDirected, isWeighted,
                List.of(startNode), new ArrayList<>(visited), List.of(), List.of(),
                new ArrayList<>(queue), List.of(), startNode, startNode, null,
                Map.of(), Map.of(), List.of(), List.of(), null, List.of(), List.of(), null, null,
                "Initialized BFS traversal starting from node " + startNode
        );
        steps.add(new VisualizationStep(stepNum++, ActionType.INITIAL, initSnap, initSnap.explanation(), Map.of(), null, null, null, null, null));

        while (!queue.isEmpty()) {
            String curr = queue.poll();

            GraphStateSnapshot visitSnap = new GraphStateSnapshot(
                    nodes, edges, isDirected, isWeighted,
                    List.of(curr), new ArrayList<>(visited), List.of(), new ArrayList<>(traversedEdges),
                    new ArrayList<>(queue), List.of(), curr, startNode, null,
                    Map.of(), Map.of(), List.of(), List.of(), null, List.of(), List.of(), null, null,
                    "Dequeued and visiting node " + curr
            );
            steps.add(new VisualizationStep(stepNum++, ActionType.VISIT, visitSnap, visitSnap.explanation(), Map.of(), null, null, null, null, null));

            for (String nbr : adj.getOrDefault(curr, List.of())) {
                String edgeId = curr + "-" + nbr;
                if (!visited.contains(nbr)) {
                    visited.add(nbr);
                    queue.add(nbr);
                    traversedEdges.add(edgeId);

                    GraphStateSnapshot insertSnap = new GraphStateSnapshot(
                            nodes, edges, isDirected, isWeighted,
                            List.of(nbr), new ArrayList<>(visited), List.of(edgeId), new ArrayList<>(traversedEdges),
                            new ArrayList<>(queue), List.of(), nbr, startNode, null,
                            Map.of(), Map.of(), List.of(), List.of(), null, List.of(), List.of(), null, null,
                            "Enqueued unvisited neighbor " + nbr + " via edge " + curr + " -> " + nbr
                    );
                    steps.add(new VisualizationStep(stepNum++, ActionType.INSERT, insertSnap, insertSnap.explanation(), Map.of(), null, null, null, null, null));
                }
            }
        }

        GraphStateSnapshot completeSnap = new GraphStateSnapshot(
                nodes, edges, isDirected, isWeighted,
                List.of(), new ArrayList<>(visited), List.of(), new ArrayList<>(traversedEdges),
                List.of(), List.of(), null, startNode, null,
                Map.of(), Map.of(), List.of(), List.of(), null, List.of(), List.of(), null, null,
                "BFS traversal completed! Visited nodes order: " + new ArrayList<>(visited)
        );
        steps.add(new VisualizationStep(stepNum, ActionType.COMPLETE, completeSnap, completeSnap.explanation(), Map.of(), null, null, null, null, null));

        return new VisualizationResponse(
                slug != null ? slug : "breadth-first-search",
                VisualizationType.GRAPH,
                steps
        );
    }
}
