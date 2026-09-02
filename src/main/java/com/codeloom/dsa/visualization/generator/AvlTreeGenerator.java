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
public class AvlTreeGenerator implements VisualizationGenerator {

    private static final List<String> SUPPORTED_SLUGS = List.of(
            "avl-tree-rotations",
            "avl-tree",
            "avl-tree-insertion",
            "avl-trees"
    );

    @Override
    public boolean supports(String algorithmSlug) {
        if (algorithmSlug == null) return false;
        String s = algorithmSlug.toLowerCase();
        return SUPPORTED_SLUGS.contains(s) || s.contains("avl");
    }

    private static class AvlNode {
        int val;
        int height;
        AvlNode left;
        AvlNode right;

        AvlNode(int val) {
            this.val = val;
            this.height = 1;
        }
    }

    private static class RootHolder {
        AvlNode root;
    }

    @Override
    public VisualizationResponse generate(String algorithmSlug, VisualizationRequest request) {
        List<Integer> rawInput = (request != null && request.input() != null && !request.input().isEmpty())
                ? request.input()
                : List.of(23, 5, 35, 28, 46, 26, 55, 99, 100);

        List<Integer> input = new ArrayList<>();
        for (Integer val : rawInput) {
            if (val != null && !input.contains(val)) {
                input.add(val);
            }
        }
        if (input.isEmpty()) {
            input = List.of(23, 5, 35, 28, 46, 26, 55, 99, 100);
        }

        List<VisualizationStep> steps = new ArrayList<>();
        int stepNum = 1;

        // Initial State
        steps.add(new VisualizationStep(
                stepNum++,
                ActionType.INITIAL,
                List.of(),
                new ArrayList<>(input),
                "Initial AVL Tree ready for element insertions. Sequence: " + input.toString()
        ));

        RootHolder holder = new RootHolder();

        for (int i = 0; i < input.size(); i++) {
            int val = input.get(i);

            List<Integer> currentSnapshot = toLevelOrderList(holder.root);

            steps.add(new VisualizationStep(
                    stepNum++,
                    ActionType.SELECT,
                    List.of(val),
                    new ArrayList<>(currentSnapshot),
                    "Inserting element " + val + " into AVL Tree"
            ));

            holder.root = insert(holder.root, val, steps, stepNum, holder);
            stepNum = steps.size() + 1;

            currentSnapshot = toLevelOrderList(holder.root);
            int bf = getBalanceFactor(holder.root);
            int h = getHeight(holder.root);

            steps.add(new VisualizationStep(
                    stepNum++,
                    ActionType.INSERT,
                    List.of(val),
                    new ArrayList<>(currentSnapshot),
                    String.format("Inserted %d into AVL Tree. Root val: %d | Root Height: %d | Root BF: %d",
                            val, (holder.root != null ? holder.root.val : val), h, bf)
            ));
        }

        List<Integer> finalSnapshot = toLevelOrderList(holder.root);

        steps.add(new VisualizationStep(
                stepNum,
                ActionType.COMPLETE,
                List.of(),
                finalSnapshot,
                "AVL Tree construction and rebalancing completed! All nodes satisfy height balance invariant (|BF| <= 1)."
        ));

        return new VisualizationResponse(
                algorithmSlug,
                VisualizationType.AVL_TREE,
                steps
        );
    }

    private int getHeight(AvlNode node) {
        return node == null ? 0 : node.height;
    }

    private int getBalanceFactor(AvlNode node) {
        return node == null ? 0 : getHeight(node.left) - getHeight(node.right);
    }

    private void updateHeight(AvlNode node) {
        if (node != null) {
            node.height = 1 + Math.max(getHeight(node.left), getHeight(node.right));
        }
    }

    private AvlNode rightRotate(AvlNode y, List<VisualizationStep> steps, int stepNum, RootHolder holder) {
        AvlNode x = y.left;
        AvlNode T2 = x.right;

        x.right = y;
        y.left = T2;

        updateHeight(y);
        updateHeight(x);

        List<Integer> snapshot = toLevelOrderList(holder.root);

        steps.add(new VisualizationStep(
                stepNum,
                ActionType.SWAP,
                List.of(y.val, x.val),
                new ArrayList<>(snapshot),
                String.format("LL/Right Rotation performed at pivot node %d. Promoted node %d as new subtree root.", y.val, x.val)
        ));

        return x;
    }

    private AvlNode leftRotate(AvlNode x, List<VisualizationStep> steps, int stepNum, RootHolder holder) {
        AvlNode y = x.right;
        AvlNode T2 = y.left;

        y.left = x;
        x.right = T2;

        updateHeight(x);
        updateHeight(y);

        List<Integer> snapshot = toLevelOrderList(holder.root);

        steps.add(new VisualizationStep(
                stepNum,
                ActionType.SWAP,
                List.of(x.val, y.val),
                new ArrayList<>(snapshot),
                String.format("RR/Left Rotation performed at pivot node %d. Promoted node %d as new subtree root.", x.val, y.val)
        ));

        return y;
    }

    private AvlNode insert(AvlNode node, int val, List<VisualizationStep> steps, int stepNum, RootHolder holder) {
        if (node == null) {
            return new AvlNode(val);
        }

        List<Integer> snapshot = toLevelOrderList(holder.root);

        if (val < node.val) {
            steps.add(new VisualizationStep(
                    stepNum++,
                    ActionType.COMPARE,
                    List.of(node.val),
                    new ArrayList<>(snapshot),
                    String.format("Comparing %d < %d: Navigating to left subtree", val, node.val)
            ));
            node.left = insert(node.left, val, steps, stepNum, holder);
        } else if (val > node.val) {
            steps.add(new VisualizationStep(
                    stepNum++,
                    ActionType.COMPARE,
                    List.of(node.val),
                    new ArrayList<>(snapshot),
                    String.format("Comparing %d > %d: Navigating to right subtree", val, node.val)
            ));
            node.right = insert(node.right, val, steps, stepNum, holder);
        } else {
            return node;
        }

        updateHeight(node);
        int balance = getBalanceFactor(node);

        // Left Left Case
        if (balance > 1 && val < node.left.val) {
            return rightRotate(node, steps, stepNum++, holder);
        }

        // Right Right Case
        if (balance < -1 && val > node.right.val) {
            return leftRotate(node, steps, stepNum++, holder);
        }

        // Left Right Case
        if (balance > 1 && val > node.left.val) {
            steps.add(new VisualizationStep(
                    stepNum++,
                    ActionType.UPDATE,
                    List.of(node.val),
                    new ArrayList<>(snapshot),
                    String.format("LR Imbalance detected at node %d (BF=%d). Performing Left Rotation on child %d first.", node.val, balance, node.left.val)
            ));
            node.left = leftRotate(node.left, steps, stepNum++, holder);
            return rightRotate(node, steps, stepNum++, holder);
        }

        // Right Left Case
        if (balance < -1 && val < node.right.val) {
            steps.add(new VisualizationStep(
                    stepNum++,
                    ActionType.UPDATE,
                    List.of(node.val),
                    new ArrayList<>(snapshot),
                    String.format("RL Imbalance detected at node %d (BF=%d). Performing Right Rotation on child %d first.", node.val, balance, node.right.val)
            ));
            node.right = rightRotate(node.right, steps, stepNum++, holder);
            return leftRotate(node, steps, stepNum++, holder);
        }

        return node;
    }

    private List<Integer> toLevelOrderList(AvlNode root) {
        if (root == null) return List.of();
        List<Integer> result = new ArrayList<>();
        List<AvlNode> queue = new ArrayList<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            AvlNode curr = queue.remove(0);
            if (curr != null) {
                result.add(curr.val);
                queue.add(curr.left);
                queue.add(curr.right);
            }
        }
        return result;
    }
}
