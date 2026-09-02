package com.codeloom.dsa.visualization.generator;

import com.codeloom.dsa.visualization.dto.*;
import com.codeloom.dsa.visualization.entity.ActionType;
import com.codeloom.dsa.visualization.entity.VisualizationType;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class GraphGeneralGenerator implements VisualizationGenerator {

    private static final List<String> SUPPORTED_SLUGS = List.of(
            "topological-sort",
            "find-path-graph-easy",
            "clone-graph-med",
            "tarjan-scc-hard",
            "cycle-detection",
            "connected-components"
    );

    @Override
    public boolean supports(String algorithmSlug) {
        if (algorithmSlug == null) return false;
        String s = algorithmSlug.toLowerCase();
        return SUPPORTED_SLUGS.contains(s) || (s.contains("graph") && !s.contains("bfs") && !s.contains("dfs") && !s.contains("dijkstra") && !s.contains("mst"));
    }

    @Override
    public VisualizationResponse generate(String algorithmSlug, VisualizationRequest request) {
        GraphVisualizationRequest graphReq = (request != null && request.graph() != null)
                ? request.graph()
                : getDefaultGraph();

        String slug = algorithmSlug != null ? algorithmSlug.toLowerCase() : "topological-sort";
        if (slug.contains("topological")) {
            return generateTopologicalSort(slug, graphReq);
        }
        return generateGeneralGraphSteps(slug, graphReq);
    }

    private GraphVisualizationRequest getDefaultGraph() {
        List<GraphNodeDto> nodes = List.of(
                new GraphNodeDto("A", "A"),
                new GraphNodeDto("B", "B"),
                new GraphNodeDto("C", "C"),
                new GraphNodeDto("D", "D"),
                new GraphNodeDto("E", "E")
        );
        List<GraphEdgeDto> edges = List.of(
                new GraphEdgeDto("A-B", "A", "B", null),
                new GraphEdgeDto("A-C", "A", "C", null),
                new GraphEdgeDto("B-D", "B", "D", null),
                new GraphEdgeDto("C-D", "C", "D", null),
                new GraphEdgeDto("D-E", "D", "E", null)
        );
        return new GraphVisualizationRequest(nodes, edges, true, false, "A", "E");
    }

    private VisualizationResponse generateTopologicalSort(String slug, GraphVisualizationRequest graph) {
        List<GraphNodeDto> nodes = graph.nodes() != null ? graph.nodes() : List.of();
        List<GraphEdgeDto> edges = graph.edges() != null ? graph.edges() : List.of();

        if (nodes.isEmpty()) {
            GraphVisualizationRequest def = getDefaultGraph();
            nodes = def.nodes();
            edges = def.edges();
        }

        List<String> nodeIds = nodes.stream().map(GraphNodeDto::id).toList();
        Map<String, Integer> inDegree = new LinkedHashMap<>();
        Map<String, List<String>> adj = new LinkedHashMap<>();
        for (String id : nodeIds) {
            inDegree.put(id, 0);
            adj.put(id, new ArrayList<>());
        }

        for (GraphEdgeDto e : edges) {
            String u = e.source();
            String v = e.target();
            if (adj.containsKey(u) && adj.containsKey(v)) {
                adj.get(u).add(v);
                inDegree.put(v, inDegree.get(v) + 1);
            }
        }

        Queue<String> zeroInDegreeQueue = new ArrayDeque<>();
        for (String id : nodeIds) {
            if (inDegree.get(id) == 0) {
                zeroInDegreeQueue.add(id);
            }
        }

        List<VisualizationStep> steps = new ArrayList<>();
        int stepNum = 1;
        List<String> topoOrder = new ArrayList<>();

        GraphStateSnapshot initSnap = new GraphStateSnapshot(
                nodes, edges, true, false,
                List.of(), List.of(), List.of(), List.of(),
                new ArrayList<>(zeroInDegreeQueue), List.of(), null, null, null,
                Map.of(), Map.of(), List.of(), List.of(), null, List.of(), List.of(), null, null,
                "Kahn's Algorithm: Calculated in-degrees for all vertices. Enqueued in-degree 0 nodes: " + zeroInDegreeQueue
        );
        steps.add(new VisualizationStep(stepNum++, ActionType.INITIAL, initSnap, initSnap.explanation(), Map.of(), null, null, null, null, null));

        while (!zeroInDegreeQueue.isEmpty()) {
            String u = zeroInDegreeQueue.poll();
            topoOrder.add(u);

            GraphStateSnapshot processSnap = new GraphStateSnapshot(
                    nodes, edges, true, false,
                    List.of(u), new ArrayList<>(topoOrder), List.of(), List.of(),
                    new ArrayList<>(zeroInDegreeQueue), List.of(), u, null, null,
                    Map.of(), Map.of(), List.of(), List.of(), null, new ArrayList<>(topoOrder), List.of(), null, null,
                    "Processed vertex " + u + " and appended to Topological Order."
            );
            steps.add(new VisualizationStep(stepNum++, ActionType.VISIT, processSnap, processSnap.explanation(), Map.of(), null, null, null, null, null));

            for (String v : adj.getOrDefault(u, List.of())) {
                int newIn = inDegree.get(v) - 1;
                inDegree.put(v, newIn);
                String edgeId = u + "-" + v;

                if (newIn == 0) {
                    zeroInDegreeQueue.add(v);

                    GraphStateSnapshot edgeSnap = new GraphStateSnapshot(
                            nodes, edges, true, false,
                            List.of(v), new ArrayList<>(topoOrder), List.of(edgeId), List.of(edgeId),
                            new ArrayList<>(zeroInDegreeQueue), List.of(), v, null, null,
                            Map.of(), Map.of(), List.of(), List.of(), null, new ArrayList<>(topoOrder), List.of(), null, null,
                            "Decremented in-degree of " + v + " to 0. Enqueued " + v + "."
                    );
                    steps.add(new VisualizationStep(stepNum++, ActionType.INSERT, edgeSnap, edgeSnap.explanation(), Map.of(), null, null, null, null, null));
                }
            }
        }

        GraphStateSnapshot completeSnap = new GraphStateSnapshot(
                nodes, edges, true, false,
                List.of(), new ArrayList<>(topoOrder), List.of(), List.of(),
                List.of(), List.of(), null, null, null,
                Map.of(), Map.of(), List.of(), List.of(), null, new ArrayList<>(topoOrder), List.of(), null, null,
                "Topological Sort completed! Topological Order: " + topoOrder
        );
        steps.add(new VisualizationStep(stepNum, ActionType.COMPLETE, completeSnap, completeSnap.explanation(), Map.of(), null, null, null, null, null));

        return new VisualizationResponse(
                slug,
                VisualizationType.GRAPH_NETWORK,
                steps
        );
    }

    private VisualizationResponse generateGeneralGraphSteps(String slug, GraphVisualizationRequest graph) {
        List<GraphNodeDto> nodes = graph.nodes() != null ? graph.nodes() : List.of();
        List<GraphEdgeDto> edges = graph.edges() != null ? graph.edges() : List.of();

        if (nodes.isEmpty()) {
            GraphVisualizationRequest def = getDefaultGraph();
            nodes = def.nodes();
            edges = def.edges();
        }

        List<String> nodeIds = nodes.stream().map(GraphNodeDto::id).toList();
        List<VisualizationStep> steps = new ArrayList<>();
        int stepNum = 1;

        GraphStateSnapshot initSnap = new GraphStateSnapshot(
                nodes, edges, Boolean.TRUE.equals(graph.directed()), Boolean.TRUE.equals(graph.weighted()),
                List.of(nodeIds.get(0)), List.of(), List.of(), List.of(),
                List.of(), List.of(), nodeIds.get(0), nodeIds.get(0), null,
                Map.of(), Map.of(), List.of(), List.of(), null, List.of(), List.of(), null, null,
                "Initialized graph visualization for " + slug
        );
        steps.add(new VisualizationStep(stepNum++, ActionType.INITIAL, initSnap, initSnap.explanation(), Map.of(), null, null, null, null, null));

        List<String> visited = new ArrayList<>();
        for (String id : nodeIds) {
            visited.add(id);
            GraphStateSnapshot stepSnap = new GraphStateSnapshot(
                    nodes, edges, Boolean.TRUE.equals(graph.directed()), Boolean.TRUE.equals(graph.weighted()),
                    List.of(id), new ArrayList<>(visited), List.of(), List.of(),
                    List.of(), List.of(), id, nodeIds.get(0), null,
                    Map.of(), Map.of(), List.of(), List.of(), null, new ArrayList<>(visited), List.of(), null, null,
                    "Processed graph vertex " + id
            );
            steps.add(new VisualizationStep(stepNum++, ActionType.VISIT, stepSnap, stepSnap.explanation(), Map.of(), null, null, null, null, null));
        }

        GraphStateSnapshot completeSnap = new GraphStateSnapshot(
                nodes, edges, Boolean.TRUE.equals(graph.directed()), Boolean.TRUE.equals(graph.weighted()),
                List.of(), new ArrayList<>(visited), List.of(), List.of(),
                List.of(), List.of(), null, nodeIds.get(0), null,
                Map.of(), Map.of(), List.of(), List.of(), null, new ArrayList<>(visited), List.of(), null, null,
                "Graph execution completed for algorithm: " + slug
        );
        steps.add(new VisualizationStep(stepNum, ActionType.COMPLETE, completeSnap, completeSnap.explanation(), Map.of(), null, null, null, null, null));

        return new VisualizationResponse(
                slug,
                VisualizationType.GRAPH_NETWORK,
                steps
        );
    }
}
