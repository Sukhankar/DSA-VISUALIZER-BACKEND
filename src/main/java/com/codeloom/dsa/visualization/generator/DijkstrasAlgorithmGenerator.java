package com.codeloom.dsa.visualization.generator;

import com.codeloom.dsa.visualization.dto.*;
import com.codeloom.dsa.visualization.entity.ActionType;
import com.codeloom.dsa.visualization.entity.VisualizationType;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DijkstrasAlgorithmGenerator implements VisualizationGenerator {

    private static final String SLUG = "dijkstras-algorithm";

    @Override
    public boolean supports(String algorithmSlug) {
        if (algorithmSlug == null) return false;
        String s = algorithmSlug.toLowerCase();
        return SLUG.equals(s) || s.contains("dijkstra");
    }

    @Override
    public VisualizationResponse generate(String algorithmSlug, VisualizationRequest request) {
        GraphVisualizationRequest graphReq = (request != null && request.graph() != null)
                ? request.graph()
                : getDefaultWeightedGraph();

        return generateGraphDijkstra(algorithmSlug, graphReq);
    }

    private GraphVisualizationRequest getDefaultWeightedGraph() {
        List<GraphNodeDto> nodes = List.of(
                new GraphNodeDto("A", "A"),
                new GraphNodeDto("B", "B"),
                new GraphNodeDto("C", "C"),
                new GraphNodeDto("D", "D"),
                new GraphNodeDto("E", "E"),
                new GraphNodeDto("F", "F")
        );
        List<GraphEdgeDto> edges = List.of(
                new GraphEdgeDto("A-B", "A", "B", 4.0),
                new GraphEdgeDto("A-C", "A", "C", 2.0),
                new GraphEdgeDto("B-C", "B", "C", 1.0),
                new GraphEdgeDto("B-D", "B", "D", 5.0),
                new GraphEdgeDto("C-D", "C", "D", 8.0),
                new GraphEdgeDto("C-E", "C", "E", 10.0),
                new GraphEdgeDto("D-E", "D", "E", 2.0),
                new GraphEdgeDto("D-F", "D", "F", 6.0),
                new GraphEdgeDto("E-F", "E", "F", 3.0)
        );
        return new GraphVisualizationRequest(nodes, edges, false, true, "A", null);
    }

    private record EdgeWeight(String target, double weight, String edgeId) {}

    private VisualizationResponse generateGraphDijkstra(String slug, GraphVisualizationRequest graph) {
        List<GraphNodeDto> nodes = graph.nodes() != null ? graph.nodes() : List.of();
        List<GraphEdgeDto> edges = graph.edges() != null ? graph.edges() : List.of();
        boolean isDirected = Boolean.TRUE.equals(graph.directed());

        if (nodes.isEmpty()) {
            GraphVisualizationRequest def = getDefaultWeightedGraph();
            nodes = def.nodes();
            edges = def.edges();
        }

        List<String> nodeIds = nodes.stream().map(GraphNodeDto::id).toList();
        String startNode = (graph.startNode() != null && nodeIds.contains(graph.startNode()))
                ? graph.startNode()
                : nodeIds.get(0);

        Map<String, List<EdgeWeight>> adj = new LinkedHashMap<>();
        for (String id : nodeIds) {
            adj.put(id, new ArrayList<>());
        }

        for (GraphEdgeDto edge : edges) {
            String u = edge.source();
            String v = edge.target();
            double w = edge.weight() != null ? edge.weight() : 1.0;
            String edgeId = edge.id() != null ? edge.id() : u + "-" + v;

            if (adj.containsKey(u) && adj.containsKey(v)) {
                adj.get(u).add(new EdgeWeight(v, w, edgeId));
                if (!isDirected) {
                    adj.get(v).add(new EdgeWeight(u, w, edgeId));
                }
            }
        }

        Map<String, Double> distMap = new LinkedHashMap<>();
        Map<String, String> predecessorMap = new LinkedHashMap<>();
        for (String id : nodeIds) {
            distMap.put(id, Double.POSITIVE_INFINITY);
            predecessorMap.put(id, null);
        }
        distMap.put(startNode, 0.0);

        List<VisualizationStep> steps = new ArrayList<>();
        int stepNum = 1;

        Set<String> visited = new LinkedHashSet<>();
        PriorityQueue<String> pq = new PriorityQueue<>(Comparator.comparingDouble(distMap::get));
        pq.add(startNode);

        // Step 1: Initial
        GraphStateSnapshot initSnap = new GraphStateSnapshot(
                nodes, edges, isDirected, true,
                List.of(startNode), new ArrayList<>(visited), List.of(), List.of(),
                List.of(startNode), List.of(), startNode, startNode, null,
                formatDistances(distMap), predecessorMap, List.of(), List.of(), null, List.of(), List.of(), null, null,
                "Initialized Dijkstra distances: d[" + startNode + "] = 0, all others = ∞"
        );
        steps.add(new VisualizationStep(stepNum++, ActionType.INITIAL, initSnap, initSnap.explanation(), Map.of(), null, null, null, null, null));

        while (visited.size() < nodeIds.size()) {
            String u = null;
            double minDist = Double.POSITIVE_INFINITY;
            for (String id : nodeIds) {
                if (!visited.contains(id) && distMap.get(id) < minDist) {
                    minDist = distMap.get(id);
                    u = id;
                }
            }

            if (u == null || distMap.get(u) == Double.POSITIVE_INFINITY) {
                break;
            }

            visited.add(u);

            GraphStateSnapshot selectSnap = new GraphStateSnapshot(
                    nodes, edges, isDirected, true,
                    List.of(u), new ArrayList<>(visited), List.of(), List.of(),
                    List.of(), List.of(), u, startNode, null,
                    formatDistances(distMap), predecessorMap, List.of(), List.of(), null, List.of(), List.of(), null, null,
                    "Extracted vertex " + u + " with minimum distance d[" + u + "] = " + formatVal(distMap.get(u))
            );
            steps.add(new VisualizationStep(stepNum++, ActionType.SELECT, selectSnap, selectSnap.explanation(), Map.of(), null, null, null, null, null));

            for (EdgeWeight ew : adj.getOrDefault(u, List.of())) {
                String v = ew.target();
                double w = ew.weight();
                String edgeId = ew.edgeId();

                if (!visited.contains(v)) {
                    double newDist = distMap.get(u) + w;
                    if (newDist < distMap.get(v)) {
                        double oldDist = distMap.get(v);
                        distMap.put(v, newDist);
                        predecessorMap.put(v, u);

                        GraphStateSnapshot relaxSnap = new GraphStateSnapshot(
                                nodes, edges, isDirected, true,
                                List.of(v), new ArrayList<>(visited), List.of(edgeId), List.of(edgeId),
                                List.of(), List.of(), v, startNode, null,
                                formatDistances(distMap), predecessorMap, List.of(), List.of(), null, List.of(), List.of(), w, null,
                                "Relaxed edge " + u + " -> " + v + " (wt=" + formatVal(w) + "). Updated d[" + v + "] from " + formatVal(oldDist) + " to " + formatVal(newDist)
                        );
                        steps.add(new VisualizationStep(stepNum++, ActionType.UPDATE, relaxSnap, relaxSnap.explanation(), Map.of(), null, null, null, null, null));
                    }
                }
            }
        }

        // Build shortest path edges
        List<String> pathEdges = new ArrayList<>();
        for (Map.Entry<String, String> entry : predecessorMap.entrySet()) {
            if (entry.getValue() != null) {
                pathEdges.add(entry.getValue() + "-" + entry.getKey());
            }
        }

        GraphStateSnapshot completeSnap = new GraphStateSnapshot(
                nodes, edges, isDirected, true,
                List.of(), new ArrayList<>(visited), List.of(), pathEdges,
                List.of(), List.of(), null, startNode, null,
                formatDistances(distMap), predecessorMap, List.of(), List.of(), null, new ArrayList<>(visited), pathEdges, null, null,
                "Dijkstra's shortest path computation completed!"
        );
        steps.add(new VisualizationStep(stepNum, ActionType.COMPLETE, completeSnap, completeSnap.explanation(), Map.of(), null, null, null, null, null));

        return new VisualizationResponse(
                slug != null ? slug : SLUG,
                VisualizationType.WEIGHTED_GRAPH,
                steps
        );
    }

    private Map<String, String> formatDistances(Map<String, Double> map) {
        Map<String, String> formatted = new LinkedHashMap<>();
        for (Map.Entry<String, Double> e : map.entrySet()) {
            formatted.put(e.getKey(), formatVal(e.getValue()));
        }
        return formatted;
    }

    private String formatVal(Double v) {
        if (v == null || Double.isInfinite(v) || v >= 999.0) return "∞";
        if (v == Math.floor(v)) return String.valueOf(v.intValue());
        return String.format("%.1f", v);
    }
}
