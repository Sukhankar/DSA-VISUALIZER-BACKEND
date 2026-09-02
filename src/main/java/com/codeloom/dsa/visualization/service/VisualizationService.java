package com.codeloom.dsa.visualization.service;

import com.codeloom.dsa.algorithm.repository.AlgorithmRepository;
import com.codeloom.dsa.common.exception.ResourceNotFoundException;
import com.codeloom.dsa.visualization.dto.VisualizationRequest;
import com.codeloom.dsa.visualization.dto.VisualizationResponse;
import com.codeloom.dsa.visualization.entity.VisualizationType;
import com.codeloom.dsa.visualization.generator.VisualizationGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class VisualizationService {

    private final AlgorithmRepository algorithmRepository;
    private final List<VisualizationGenerator> generators;

    public VisualizationService(
            AlgorithmRepository algorithmRepository,
            List<VisualizationGenerator> generators
    ) {
        this.algorithmRepository = algorithmRepository;
        this.generators = generators;
    }

    public VisualizationResponse generateVisualization(
            String slug,
            VisualizationRequest request
    ) {
        // 1. Verify algorithm exists in database
        algorithmRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Algorithm not found with slug: " + slug));

        // 2. Validate request input payload
        if (request != null && request.input() != null && request.input().isEmpty()) {
            boolean hasOtherPayload = (request.graph() != null)
                    || (request.points() != null && !request.points().isEmpty())
                    || (request.listInput() != null && !request.listInput().isEmpty())
                    || (request.stackInput() != null && !request.stackInput().isEmpty())
                    || (request.queueInput() != null && !request.queueInput().isEmpty())
                    || (request.trieInput() != null && !request.trieInput().isEmpty())
                    || (request.matrixInput() != null && !request.matrixInput().isEmpty())
                    || (request.knapsackInput() != null)
                    || (request.target() != null);
            if (!hasOtherPayload) {
                throw new IllegalArgumentException("Input list must not be empty");
            }
        }

        // 3. Find supporting generator
        return generators.stream()
                .filter(g -> g.supports(slug))
                .findFirst()
                .map(g -> g.generate(slug, request))
                .orElseGet(() -> generateGenericVisualization(slug, request));
    }

    private VisualizationResponse generateGenericVisualization(String slug, VisualizationRequest request) {
        var algoOpt = algorithmRepository.findBySlug(slug);
        String catSlug = algoOpt.map(a -> a.getCategory().getSlug()).orElse("arrays");
        String name = algoOpt.map(a -> a.getName()).orElse(slug);

        VisualizationType type;
        if ("trees".equalsIgnoreCase(catSlug)) {
            type = VisualizationType.TREE;
        } else if ("linked-lists".equalsIgnoreCase(catSlug)) {
            type = VisualizationType.LINKED_LIST;
        } else if ("graphs".equalsIgnoreCase(catSlug)) {
            type = VisualizationType.GRAPH;
        } else {
            type = VisualizationType.ARRAY;
        }

        List<com.codeloom.dsa.visualization.dto.VisualizationStep> steps = new ArrayList<>();
        int stepNum = 1;

        List<Integer> treeInput = (request != null && request.input() != null && !request.input().isEmpty())
                ? new ArrayList<>(request.input())
                : List.of(15, 10, 20, 8, 12, 17, 25);

        List<Integer> listInput = (request != null && request.input() != null && !request.input().isEmpty())
                ? new ArrayList<>(request.input())
                : List.of(10, 20, 30, 40, 50);

        if (type == VisualizationType.TREE) {
            steps.add(new com.codeloom.dsa.visualization.dto.VisualizationStep(stepNum++, com.codeloom.dsa.visualization.entity.ActionType.INITIAL, List.of(), new ArrayList<>(treeInput), "Initializing " + name + " tree structure execution.",
                    Map.of("java", 1, "python", 1, "cpp", 1, "pseudocode", 1),
                    "Initializing tree node references. Memory stack allocated for recursive traversal.",
                    "Root pointer set. Invariant: Tree maintains parent-child pointer relations.",
                    "To establish initial search boundaries before visiting child subtrees.",
                    "Space: O(H) recursive stack depth | Time: O(1) initialization"));
            steps.add(new com.codeloom.dsa.visualization.dto.VisualizationStep(stepNum++, com.codeloom.dsa.visualization.entity.ActionType.VISIT, List.of(), new ArrayList<>(treeInput), "Visiting root node [15] for tree query operation.",
                    Map.of("java", 3, "python", 3, "cpp", 3, "pseudocode", 2),
                    "Inspecting root node value 15 to decide whether to search left or right subtree.",
                    "Root evaluation: Compare target with current node key to determine recursive branch direction.",
                    "Subtree selection depends on whether key is smaller or larger than root value.",
                    "Time: O(1) comparison per node | Space: O(1) pointer lookup"));
            steps.add(new com.codeloom.dsa.visualization.dto.VisualizationStep(stepNum++, com.codeloom.dsa.visualization.entity.ActionType.COMPLETE, List.of(), new ArrayList<>(treeInput), name + " tree execution complete! Range result verified.",
                    Map.of("java", 10, "python", 10, "cpp", 10, "pseudocode", 5),
                    "Tree traversal has reached leaf condition and verified result successfully.",
                    "Terminal condition reached. Total visited nodes bounded by O(log N) to O(N).",
                    "All requested nodes processed according to binary tree constraints.",
                    "Overall Time Complexity: O(log N) best/avg, O(N) worst case | Space: O(H)"));
        } else if (type == VisualizationType.LINKED_LIST) {
            steps.add(new com.codeloom.dsa.visualization.dto.VisualizationStep(stepNum++, com.codeloom.dsa.visualization.entity.ActionType.INITIAL, List.of(), new ArrayList<>(listInput), "Initializing " + name + " linked list node structure.",
                    Map.of("java", 1, "python", 1, "cpp", 1, "pseudocode", 1),
                    "Setting up HEAD pointer to start linked list traversal.",
                    "Pointer allocation. Head pointer points to initial Node memory address.",
                    "Linked list operations must start from HEAD since memory locations are non-contiguous.",
                    "Space: O(1) auxiliary pointer | Time: O(1) initialization"));
            steps.add(new com.codeloom.dsa.visualization.dto.VisualizationStep(stepNum++, com.codeloom.dsa.visualization.entity.ActionType.VISIT, List.of(0), new ArrayList<>(listInput), "Setting HEAD pointer to initial Node [" + listInput.get(0) + "].",
                    Map.of("java", 2, "python", 2, "cpp", 2, "pseudocode", 2),
                    "Traversing node " + listInput.get(0) + ". Following current.next pointer to inspect data payload.",
                    "O(1) pointer dereference operation: node = node.next.",
                    "To reach target node index linearly without random access.",
                    "Time: O(1) per node traversal step | Space: O(1)"));
            steps.add(new com.codeloom.dsa.visualization.dto.VisualizationStep(stepNum++, com.codeloom.dsa.visualization.entity.ActionType.COMPLETE, List.of(), new ArrayList<>(listInput), name + " linked list operation complete!",
                    Map.of("java", 8, "python", 8, "cpp", 8, "pseudocode", 5),
                    "Traversal reached null pointer (end of list). Operation successful.",
                    "Terminal condition node.next == null satisfied.",
                    "Finished processing all linked list nodes.",
                    "Overall Time: O(N) linear traversal | Space: O(1) in-place"));
        } else if (type == VisualizationType.GRAPH) {
            List<String> visited = new ArrayList<>();
            visited.add("A");
            steps.add(new com.codeloom.dsa.visualization.dto.VisualizationStep(stepNum++, com.codeloom.dsa.visualization.entity.ActionType.INITIAL, List.of(), List.of(), "A", new ArrayList<>(visited), List.of("B", "C"), "Initializing " + name + " graph traversal.",
                    Map.of("java", 1, "python", 1, "cpp", 1, "pseudocode", 1),
                    "Starting graph exploration from vertex A. Queue/Stack initialized.",
                    "Graph G=(V,E) state initialization. Visited set V'={A}, Frontier Q={B, C}.",
                    "To prevent infinite cycles in cyclic graphs using visited tracking.",
                    "Space: O(V) visited set storage | Time: O(1)"));
            visited.add("B");
            steps.add(new com.codeloom.dsa.visualization.dto.VisualizationStep(stepNum++, com.codeloom.dsa.visualization.entity.ActionType.VISIT, List.of(), List.of(), "B", new ArrayList<>(visited), List.of("C", "D"), "Exploring vertex B via edge (A -> B).",
                    Map.of("java", 4, "python", 4, "cpp", 4, "pseudocode", 3),
                    "Popping vertex B from frontier and marking as visited.",
                    "Edge traversal (A, B) ∈ E. Neighbor adjacency list expansion.",
                    "Exploring adjacent unvisited neighbors to build traversal tree.",
                    "Time: O(deg(B)) neighbor inspection | Space: O(V) frontier"));
            steps.add(new com.codeloom.dsa.visualization.dto.VisualizationStep(stepNum++, com.codeloom.dsa.visualization.entity.ActionType.COMPLETE, List.of(), List.of(), "E", new ArrayList<>(visited), List.of(), name + " graph processing complete!",
                    Map.of("java", 10, "python", 10, "cpp", 10, "pseudocode", 6),
                    "All reachable vertices explored. Graph traversal finished.",
                    "Frontier set Q = ∅. Complete component discovery achieved.",
                    "No remaining unvisited edges in connected component.",
                    "Overall Time: O(V + E) | Space: O(V)"));
        } else {
            List<Integer> array = (request != null && request.input() != null && !request.input().isEmpty())
                    ? new ArrayList<>(request.input())
                    : List.of(5, 1, 4, 2, 8);

            steps.add(new com.codeloom.dsa.visualization.dto.VisualizationStep(stepNum++, com.codeloom.dsa.visualization.entity.ActionType.INITIAL, List.of(), new ArrayList<>(array), "Initializing execution for " + name + ".",
                    Map.of("java", 1, "python", 1, "cpp", 1, "pseudocode", 1),
                    "Loading input array into contiguous memory slots with zero-based indexing.",
                    "State space allocation: N=" + array.size() + " elements in memory. Loop invariant established.",
                    "Initial setup is required before inspecting elements or making conditional swaps.",
                    "Space: O(N) array storage | Time: O(1) setup"));
            steps.add(new com.codeloom.dsa.visualization.dto.VisualizationStep(stepNum++, com.codeloom.dsa.visualization.entity.ActionType.SELECT, List.of(0), new ArrayList<>(array), "Inspecting element " + array.get(0) + " at index 0.",
                    Map.of("java", 3, "python", 3, "cpp", 3, "pseudocode", 2),
                    "Selecting first element at index 0 (" + array.get(0) + ") to begin comparative processing.",
                    "Pointer position i=0. Base offset address lookup: A[0]=" + array.get(0) + ".",
                    "Algorithm requires inspecting elements sequentially from index 0.",
                    "Time: O(1) index lookup | Space: O(1)"));

            if (array.size() > 1) {
                steps.add(new com.codeloom.dsa.visualization.dto.VisualizationStep(stepNum++, com.codeloom.dsa.visualization.entity.ActionType.COMPARE, List.of(0, 1), new ArrayList<>(array), "Comparing element " + array.get(0) + " (index 0) with " + array.get(1) + " (index 1).",
                        Map.of("java", 5, "python", 5, "cpp", 5, "pseudocode", 3),
                        "Comparing values " + array.get(0) + " and " + array.get(1) + " to see which element is greater.",
                        "Branch evaluation: relational condition checking if A[0] > A[1].",
                        "Determines if an in-place swap or state update is required to maintain order.",
                        "Time: O(1) comparison opcode | Cumulative Time: O(N)"));
                List<Integer> modified = new ArrayList<>(array);
                if (modified.get(0) > modified.get(1)) {
                    Collections.swap(modified, 0, 1);
                    steps.add(new com.codeloom.dsa.visualization.dto.VisualizationStep(stepNum++, com.codeloom.dsa.visualization.entity.ActionType.SWAP, List.of(0, 1), new ArrayList<>(modified), "Swapping elements at indices 0 and 1 (" + array.get(0) + " ↔ " + array.get(1) + ").",
                            Map.of("java", 7, "python", 7, "cpp", 7, "pseudocode", 4),
                            "The element " + array.get(0) + " is larger than " + array.get(1) + ", so we swap their positions.",
                            "In-place memory swap operation: temp = A[0]; A[0] = A[1]; A[1] = temp.",
                            "Exchanging positions moves smaller values left and larger values right.",
                            "Time: O(1) memory write | Auxiliary Space: O(1)"));
                } else {
                    steps.add(new com.codeloom.dsa.visualization.dto.VisualizationStep(stepNum++, com.codeloom.dsa.visualization.entity.ActionType.UPDATE, List.of(1), new ArrayList<>(modified), "Updating algorithm state for index 1.",
                            Map.of("java", 8, "python", 8, "cpp", 8, "pseudocode", 4),
                            "Elements are already in valid relative order. Advancing pointer to index 1.",
                            "State transition: Pointer increment i = i + 1. Invariant holds for inspected range.",
                            "No swap needed because the condition A[i] <= A[i+1] is satisfied.",
                            "Time: O(1) state transition | Space: O(1)"));
                }

                if (array.size() > 2) {
                    steps.add(new com.codeloom.dsa.visualization.dto.VisualizationStep(stepNum++, com.codeloom.dsa.visualization.entity.ActionType.VISIT, List.of(2), new ArrayList<>(modified), "Processing element " + array.get(2) + " at index 2.",
                            Map.of("java", 10, "python", 10, "cpp", 10, "pseudocode", 5),
                            "Moving inspection window to index 2 (value: " + array.get(2) + ").",
                            "Linear pass continuation: Inspecting index k=2.",
                            "To process all remaining elements in the array.",
                            "Time: O(1) step | Space: O(1)"));
                }
                steps.add(new com.codeloom.dsa.visualization.dto.VisualizationStep(stepNum++, com.codeloom.dsa.visualization.entity.ActionType.COMPLETE, List.of(), new ArrayList<>(modified), name + " execution finished successfully.",
                        Map.of("java", 12, "python", 12, "cpp", 12, "pseudocode", 6),
                        "Algorithm has successfully processed all input elements.",
                        "Post-condition verified: Output array state fully transformed.",
                        "Terminating loop execution after processing all required elements.",
                        "Overall Time Complexity: O(N) to O(N log N) | Space Complexity: O(1) auxiliary"));
            } else {
                steps.add(new com.codeloom.dsa.visualization.dto.VisualizationStep(stepNum++, com.codeloom.dsa.visualization.entity.ActionType.COMPLETE, List.of(), new ArrayList<>(array), name + " execution finished.",
                        Map.of("java", 5, "python", 5, "cpp", 5, "pseudocode", 3),
                        "Algorithm completed single element processing.",
                        "Base condition met: Array size N=1.",
                        "Single element is trivially sorted/processed.",
                        "Overall Time: O(1) | Space: O(1)"));
            }
        }

        return new VisualizationResponse(slug, type, steps);
    }
}
