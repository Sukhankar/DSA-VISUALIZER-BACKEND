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
public class StackOperationsGenerator implements VisualizationGenerator {

    private static final List<String> SUPPORTED_SLUGS = List.of(
            "stack-operations",
            "stack-push-pop",
            "valid-parentheses",
            "stack"
    );

    @Override
    public boolean supports(String algorithmSlug) {
        if (algorithmSlug == null) return false;
        String s = algorithmSlug.toLowerCase();
        return SUPPORTED_SLUGS.contains(s) || s.contains("stack");
    }

    @Override
    public VisualizationResponse generate(String algorithmSlug, VisualizationRequest request) {
        List<Integer> input = (request != null && request.input() != null && !request.input().isEmpty())
                ? request.input()
                : List.of(10, 20, 30, 40);

        List<VisualizationStep> steps = new ArrayList<>();
        int stepNum = 1;

        steps.add(new VisualizationStep(
                stepNum++,
                ActionType.INITIAL,
                List.of(),
                List.of(),
                "Initial empty LIFO Stack. Operations sequence: PUSH elements " + input.toString()
        ));

        List<Integer> stackState = new ArrayList<>();

        // PUSH operations
        for (int i = 0; i < input.size(); i++) {
            int val = input.get(i);
            stackState.add(val);

            steps.add(new VisualizationStep(
                    stepNum++,
                    ActionType.INSERT,
                    List.of(stackState.size() - 1),
                    new ArrayList<>(stackState),
                    String.format("PUSH operation: Pushed element [%d] onto top of stack. Current Top: [%d]", val, val)
            ));
        }

        // PEEK operation
        if (!stackState.isEmpty()) {
            steps.add(new VisualizationStep(
                    stepNum++,
                    ActionType.VISIT,
                    List.of(stackState.size() - 1),
                    new ArrayList<>(stackState),
                    String.format("PEEK operation: Inspecting top element [%d] without removing it.", stackState.get(stackState.size() - 1))
            ));
        }

        // POP one operation to demonstrate LIFO pop
        if (!stackState.isEmpty()) {
            int popped = stackState.remove(stackState.size() - 1);
            steps.add(new VisualizationStep(
                    stepNum++,
                    ActionType.DELETE,
                    List.of(),
                    new ArrayList<>(stackState),
                    String.format("POP operation: Removed element [%d] from top of stack. New Top: %s",
                            popped, stackState.isEmpty() ? "None (Empty)" : "[" + stackState.get(stackState.size() - 1) + "]")
            ));
        }

        // COMPLETE
        steps.add(new VisualizationStep(
                stepNum,
                ActionType.COMPLETE,
                List.of(),
                new ArrayList<>(stackState),
                "Stack LIFO operations execution completed!"
        ));

        return new VisualizationResponse(
                algorithmSlug,
                VisualizationType.STACK,
                steps
        );
    }
}
