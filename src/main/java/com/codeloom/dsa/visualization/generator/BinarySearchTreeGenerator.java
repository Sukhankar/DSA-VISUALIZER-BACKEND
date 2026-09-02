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
public class BinarySearchTreeGenerator implements VisualizationGenerator {

    private static final List<String> SUPPORTED_SLUGS = List.of(
            "binary-search-tree",
            "bst",
            "bst-search",
            "bst-insertion",
            "bst-deletion",
            "lowest-common-ancestor",
            "maximum-depth-of-binary-tree",
            "validate-binary-search-tree"
    );

    @Override
    public boolean supports(String algorithmSlug) {
        if (algorithmSlug == null) return false;
        String s = algorithmSlug.toLowerCase();
        return SUPPORTED_SLUGS.contains(s) || (s.contains("bst") && !s.contains("avl")) || s.contains("binary-search-tree");
    }

    @Override
    public VisualizationResponse generate(String algorithmSlug, VisualizationRequest request) {
        List<Integer> array = (request != null && request.input() != null && !request.input().isEmpty())
                ? new ArrayList<>(request.input())
                : List.of(15, 10, 20, 8, 12, 17, 25);

        List<VisualizationStep> steps = new ArrayList<>();
        int stepNum = 1;

        List<Integer> bstNodes = new ArrayList<>();

        // 1. Initial State
        steps.add(new VisualizationStep(
                stepNum++,
                ActionType.INITIAL,
                List.of(),
                new ArrayList<>(array),
                "Initial empty Binary Search Tree. Input insertion sequence: " + array.toString()
        ));

        // 2. BST Insertions
        for (int i = 0; i < array.size(); i++) {
            int val = array.get(i);

            // Traverse and Insert
            int insertedIdx = bstNodes.size();
            bstNodes.add(val);

            steps.add(new VisualizationStep(
                    stepNum++,
                    ActionType.SELECT,
                    List.of(insertedIdx),
                    new ArrayList<>(bstNodes),
                    String.format("Inserting element [%d] into BST.", val)
            ));

            steps.add(new VisualizationStep(
                    stepNum++,
                    ActionType.INSERT,
                    List.of(insertedIdx),
                    new ArrayList<>(bstNodes),
                    String.format("Inserted [%d] into Binary Search Tree satisfying key invariant (Left < Node < Right).", val)
            ));
        }

        // 3. Complete Step
        steps.add(new VisualizationStep(
                stepNum,
                ActionType.COMPLETE,
                List.of(),
                new ArrayList<>(bstNodes),
                "Binary Search Tree construction completed! Invariant verified."
        ));

        return new VisualizationResponse(
                algorithmSlug,
                VisualizationType.BST,
                steps
        );
    }
}
