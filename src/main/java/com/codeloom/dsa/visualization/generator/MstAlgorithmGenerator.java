package com.codeloom.dsa.visualization.generator;

import com.codeloom.dsa.visualization.dto.*;
import com.codeloom.dsa.visualization.entity.ActionType;
import com.codeloom.dsa.visualization.entity.VisualizationType;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class MstAlgorithmGenerator implements VisualizationGenerator {

    private static final List<String> SUPPORTED_SLUGS = List.of(
            "prims-mst",
            "kruskals-mst",
            "mst",
            "minimum-spanning-tree"
    );

    @Override
    public boolean supports(String algorithmSlug) {
        if (algorithmSlug == null) return false;
        String s = algorithmSlug.toLowerCase();
        return SUPPORTED_SLUGS.contains(s) || s.contains("mst") || s.contains("spanning-tree");
    }

    @Override
    public VisualizationResponse generate(String algorithmSlug, VisualizationRequest request) {
        GraphVisualizationRequest graphReq = (request != null && request.graph() != null)
                ? request.graph()
                : getDefaultWeightedGraph();

        String slug = algorithmSlug != null ? algorithmSlug.toLowerCase() : "prims-mst";
        if (slug.contains("kruskal")) {
            return generateKruskal(slug, graphReq);
        }
        return generatePrim(slug, graphReq);
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

    private record EdgeObj(String id, String u, String v, double weight) {}

    private VisualizationResponse generatePrim(String slug, GraphVisualizationRequest graph) {
        List<GraphNodeDto> nodes = graph.nodes() != null ? graph.nodes() : List.of();
        List<GraphEdgeDto> edges = graph.edges() != null ? graph.edges() : List.of();

        if (nodes.isEmpty()) {
            GraphVisualizationRequest def = getDefaultWeightedGraph();
            nodes = def.nodes();
            edges = def.edges();
        }

        List<String> nodeIds = nodes.stream().map(GraphNodeDto::id).toList();
        String startNode = (graph.startNode() != null && nodeIds.contains(graph.startNode()))
                ? graph.startNode()
                : nodeIds.get(0);

        List<VisualizationStep> steps = new ArrayList<>();
        int stepNum = 1;

        Set<String> visited = new LinkedHashSet<>();
        visited.add(startNode);

        List<String> mstEdges = new ArrayList<>();
        List<String> rejectedEdges = new ArrayList<>();
        double totalWeight = 0.0;

        GraphStateSnapshot initSnap = new GraphStateSnapshot(
                nodes, edges, false, true,
                List.of(startNode), new ArrayList<>(visited), List.of(), List.of(),
                List.of(), List.of(), startNode, startNode, null,
                Map.of(), Map.of(), List.of(), List.of(), null, List.of(), List.of(), 0.0, 0.0,
                "Initialized Prim's MST starting from root vertex " + startNode
        );
        steps.add(new VisualizationStep(stepNum++, ActionType.INITIAL, initSnap, initSnap.explanation(), Map.of(), null, null, null, null, null));

        while (visited.size() < nodeIds.size()) {
            EdgeObj bestEdge = null;
            double minW = Double.POSITIVE_INFINITY;

            for (GraphEdgeDto e : edges) {
                String u = e.source();
                String v = e.target();
                double w = e.weight() != null ? e.weight() : 1.0;
                String eId = e.id() != null ? e.id() : u + "-" + v;

                boolean uVis = visited.contains(u);
                boolean vVis = visited.contains(v);

                if ((uVis && !vVis) || (vVis && !uVis)) {
                    if (w < minW) {
                        minW = w;
                        bestEdge = new EdgeObj(eId, uVis ? u : v, uVis ? v : u, w);
                    }
                }
            }

            if (bestEdge == null) break;

            visited.add(bestEdge.v());
            mstEdges.add(bestEdge.id());
            totalWeight += bestEdge.weight();

            GraphStateSnapshot addSnap = new GraphStateSnapshot(
                    nodes, edges, false, true,
                    List.of(bestEdge.v()), new ArrayList<>(visited), List.of(bestEdge.id()), new ArrayList<>(mstEdges),
                    List.of(), List.of(), bestEdge.v(), startNode, null,
                    Map.of(), Map.of(), new ArrayList<>(mstEdges), new ArrayList<>(rejectedEdges), bestEdge.id(), List.of(), new ArrayList<>(mstEdges), bestEdge.weight(), totalWeight,
                    "Added cheapest cut edge " + bestEdge.u() + " - " + bestEdge.v() + " (wt=" + bestEdge.weight() + ") to MST. Total Weight = " + totalWeight
            );
            steps.add(new VisualizationStep(stepNum++, ActionType.INSERT, addSnap, addSnap.explanation(), Map.of(), null, null, null, null, null));
        }

        GraphStateSnapshot completeSnap = new GraphStateSnapshot(
                nodes, edges, false, true,
                List.of(), new ArrayList<>(visited), List.of(), new ArrayList<>(mstEdges),
                List.of(), List.of(), null, startNode, null,
                Map.of(), Map.of(), new ArrayList<>(mstEdges), new ArrayList<>(rejectedEdges), null, new ArrayList<>(visited), new ArrayList<>(mstEdges), 0.0, totalWeight,
                "Prim's Minimum Spanning Tree completed! Total MST Weight = " + totalWeight
        );
        steps.add(new VisualizationStep(stepNum, ActionType.COMPLETE, completeSnap, completeSnap.explanation(), Map.of(), null, null, null, null, null));

        return new VisualizationResponse(
                slug,
                VisualizationType.WEIGHTED_GRAPH,
                steps
        );
    }

    private VisualizationResponse generateKruskal(String slug, GraphVisualizationRequest graph) {
        List<GraphNodeDto> nodes = graph.nodes() != null ? graph.nodes() : List.of();
        List<GraphEdgeDto> edges = graph.edges() != null ? graph.edges() : List.of();

        if (nodes.isEmpty()) {
            GraphVisualizationRequest def = getDefaultWeightedGraph();
            nodes = def.nodes();
            edges = def.edges();
        }

        List<String> nodeIds = nodes.stream().map(GraphNodeDto::id).toList();
        List<EdgeObj> sortedEdges = new ArrayList<>();
        for (GraphEdgeDto e : edges) {
            String u = e.source();
            String v = e.target();
            double w = e.weight() != null ? e.weight() : 1.0;
            String eId = e.id() != null ? e.id() : u + "-" + v;
            sortedEdges.add(new EdgeObj(eId, u, v, w));
        }
        sortedEdges.sort(Comparator.comparingDouble(EdgeObj::weight));

        Map<String, String> parent = new HashMap<>();
        for (String id : nodeIds) parent.put(id, id);

        List<VisualizationStep> steps = new ArrayList<>();
        int stepNum = 1;

        List<String> mstEdges = new ArrayList<>();
        List<String> rejectedEdges = new ArrayList<>();
        Set<String> visitedNodes = new LinkedHashSet<>();
        double totalWeight = 0.0;

        GraphStateSnapshot initSnap = new GraphStateSnapshot(
                nodes, edges, false, true,
                List.of(), List.of(), List.of(), List.of(),
                List.of(), List.of(), null, null, null,
                Map.of(), Map.of(), List.of(), List.of(), null, List.of(), List.of(), 0.0, 0.0,
                "Initialized Kruskal's MST: Sorted " + sortedEdges.size() + " edges by weight."
        );
        steps.add(new VisualizationStep(stepNum++, ActionType.INITIAL, initSnap, initSnap.explanation(), Map.of(), null, null, null, null, null));

        for (EdgeObj e : sortedEdges) {
            String rootU = find(parent, e.u());
            String rootV = find(parent, e.v());

            if (!rootU.equals(rootV)) {
                parent.put(rootU, rootV);
                mstEdges.add(e.id());
                visitedNodes.add(e.u());
                visitedNodes.add(e.v());
                totalWeight += e.weight();

                GraphStateSnapshot acceptSnap = new GraphStateSnapshot(
                        nodes, edges, false, true,
                        List.of(e.u(), e.v()), new ArrayList<>(visitedNodes), List.of(e.id()), new ArrayList<>(mstEdges),
                        List.of(), List.of(), null, null, null,
                        Map.of(), Map.of(), new ArrayList<>(mstEdges), new ArrayList<>(rejectedEdges), e.id(), List.of(), new ArrayList<>(mstEdges), e.weight(), totalWeight,
                        "Accepted edge " + e.u() + " - " + e.v() + " (wt=" + e.weight() + ") into MST. Total Weight = " + totalWeight
                );
                steps.add(new VisualizationStep(stepNum++, ActionType.INSERT, acceptSnap, acceptSnap.explanation(), Map.of(), null, null, null, null, null));
            } else {
                rejectedEdges.add(e.id());

                GraphStateSnapshot rejectSnap = new GraphStateSnapshot(
                        nodes, edges, false, true,
                        List.of(e.u(), e.v()), new ArrayList<>(visitedNodes), List.of(e.id()), new ArrayList<>(mstEdges),
                        List.of(), List.of(), null, null, null,
                        Map.of(), Map.of(), new ArrayList<>(mstEdges), new ArrayList<>(rejectedEdges), e.id(), List.of(), new ArrayList<>(mstEdges), e.weight(), totalWeight,
                        "Rejected edge " + e.u() + " - " + e.v() + " (wt=" + e.weight() + ") as it creates a cycle."
                );
                steps.add(new VisualizationStep(stepNum++, ActionType.SELECT, rejectSnap, rejectSnap.explanation(), Map.of(), null, null, null, null, null));
            }
        }

        GraphStateSnapshot completeSnap = new GraphStateSnapshot(
                nodes, edges, false, true,
                List.of(), new ArrayList<>(visitedNodes), List.of(), new ArrayList<>(mstEdges),
                List.of(), List.of(), null, null, null,
                Map.of(), Map.of(), new ArrayList<>(mstEdges), new ArrayList<>(rejectedEdges), null, new ArrayList<>(visitedNodes), new ArrayList<>(mstEdges), 0.0, totalWeight,
                "Kruskal's Minimum Spanning Tree completed! Final MST Weight = " + totalWeight
        );
        steps.add(new VisualizationStep(stepNum, ActionType.COMPLETE, completeSnap, completeSnap.explanation(), Map.of(), null, null, null, null, null));

        return new VisualizationResponse(
                slug,
                VisualizationType.WEIGHTED_GRAPH,
                steps
        );
    }

    private String find(Map<String, String> parent, String i) {
        if (parent.get(i).equals(i)) return i;
        String root = find(parent, parent.get(i));
        parent.put(i, root);
        return root;
    }
}
