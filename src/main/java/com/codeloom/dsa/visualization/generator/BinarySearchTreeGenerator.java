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

    private static final String SLUG = "binary-search-tree";

    @Override
    public boolean supports(String algorithmSlug) {
        return SLUG.equalsIgnoreCase(algorithmSlug);
    }

    @Override
    public VisualizationResponse generate(String algorithmSlug, VisualizationRequest request) {
        List<Integer> array = new ArrayList<>(request.input());
        List<VisualizationStep> steps = new ArrayList<>();
        int stepNum = 1;

        List<Integer> bstNodes = new ArrayList<>();

        // 1. Initial State
        steps.add(new VisualizationStep(
                stepNum++,
                ActionType.INITIAL,
                List.of(),
                new ArrayList<>(bstNodes),
                "Initial empty Binary Search Tree root"
        ));

        // 2. BST Insertions
        for (int i = 0; i < array.size(); i++) {
            int val = array.get(i);

            steps.add(new VisualizationStep(
                    stepNum++,
                    ActionType.SELECT,
                    List.of(i),
                    new ArrayList<>(bstNodes),
                    "Inserting element " + val + " into BST"
            ));

            int currIdx = 0;
            while (currIdx < bstNodes.size()) {
                int nodeVal = bstNodes.get(currIdx);
                steps.add(new VisualizationStep(
                        stepNum++,
                        ActionType.COMPARE,
                        List.of(currIdx),
                        new ArrayList<>(bstNodes),
                        String.format("Comparing %d with BST node %d at index %d", val, nodeVal, currIdx)
                ));

                if (val < nodeVal) {
                    currIdx = 2 * currIdx + 1; // Left child
                } else {
                    currIdx = 2 * currIdx + 2; // Right child
                }
            }

            // Expand array size to fit node if needed
            while (bstNodes.size() <= currIdx) {
                bstNodes.add(null);
            }
            bstNodes.set(currIdx, val);

            // Filter non-null elements snapshot for response clean presentation
            List<Integer> snapshot = bstNodes.stream().filter(n -> n != null).toList();

            steps.add(new VisualizationStep(
                    stepNum++,
                    ActionType.INSERT,
                    List.of(currIdx),
                    snapshot,
                    String.format("Inserted node %d into BST at tree index %d", val, currIdx)
            ));
        }

        List<Integer> finalSnapshot = bstNodes.stream().filter(n -> n != null).toList();

        // 3. Completion Step
        steps.add(new VisualizationStep(
                stepNum,
                ActionType.COMPLETE,
                List.of(),
                finalSnapshot,
                "Binary Search Tree construction completed!"
        ));

        return new VisualizationResponse(
                SLUG,
                VisualizationType.TREE,
                steps
        );
    }
}
