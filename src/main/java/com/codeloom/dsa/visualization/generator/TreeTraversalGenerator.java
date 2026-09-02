package com.codeloom.dsa.visualization.generator;

import com.codeloom.dsa.visualization.dto.VisualizationRequest;
import com.codeloom.dsa.visualization.dto.VisualizationResponse;
import com.codeloom.dsa.visualization.dto.VisualizationStep;
import com.codeloom.dsa.visualization.entity.ActionType;
import com.codeloom.dsa.visualization.entity.VisualizationType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TreeTraversalGenerator implements VisualizationGenerator {

    private static final List<String> SUPPORTED_SLUGS = List.of(
            "tree-traversal",
            "binary-tree-inorder-traversal",
            "binary-tree-preorder-traversal",
            "binary-tree-postorder-traversal",
            "binary-tree-level-order-traversal",
            "tree-traversals"
    );

    @Override
    public boolean supports(String algorithmSlug) {
        if (algorithmSlug == null) return false;
        String s = algorithmSlug.toLowerCase();
        return SUPPORTED_SLUGS.contains(s) || s.contains("traversal") || s.contains("inorder") || s.contains("preorder") || s.contains("postorder");
    }

    @Override
    public VisualizationResponse generate(String algorithmSlug, VisualizationRequest request) {
        List<Integer> array = (request != null && request.input() != null && !request.input().isEmpty())
                ? new ArrayList<>(request.input())
                : List.of(15, 10, 20, 8, 12, 17, 25);

        List<VisualizationStep> steps = new ArrayList<>();
        int stepNum = 1;

        // 1. Initial State
        steps.add(new VisualizationStep(
                stepNum++,
                ActionType.INITIAL,
                List.of(),
                new ArrayList<>(array),
                "Initial Tree Traversal execution. Root node at index 0 [val=" + array.get(0) + "]"
        ));

        // 2. Traversal
        for (int i = 0; i < array.size(); i++) {
            int val = array.get(i);
            steps.add(new VisualizationStep(
                    stepNum++,
                    ActionType.VISIT,
                    List.of(i),
                    new ArrayList<>(array),
                    String.format("Traversal Step: Visited tree node [%d] at index %d", val, i)
            ));
        }

        // 3. Completion Step
        steps.add(new VisualizationStep(
                stepNum,
                ActionType.COMPLETE,
                List.of(),
                new ArrayList<>(array),
                "Tree Traversal completed!"
        ));

        return new VisualizationResponse(
                algorithmSlug,
                VisualizationType.TREE,
                steps
        );
    }
}
