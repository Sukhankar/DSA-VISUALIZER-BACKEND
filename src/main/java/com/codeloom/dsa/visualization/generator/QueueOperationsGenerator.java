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
public class QueueOperationsGenerator implements VisualizationGenerator {

    private static final List<String> SUPPORTED_SLUGS = List.of(
            "queue-operations",
            "queue-enqueue-dequeue",
            "circular-queue",
            "queue"
    );

    @Override
    public boolean supports(String algorithmSlug) {
        if (algorithmSlug == null) return false;
        String s = algorithmSlug.toLowerCase();
        return SUPPORTED_SLUGS.contains(s) || s.contains("queue");
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
                "Initial empty FIFO Queue. Operations sequence: ENQUEUE elements " + input.toString()
        ));

        List<Integer> queueState = new ArrayList<>();

        // ENQUEUE operations
        for (int i = 0; i < input.size(); i++) {
            int val = input.get(i);
            queueState.add(val);

            steps.add(new VisualizationStep(
                    stepNum++,
                    ActionType.INSERT,
                    List.of(0, queueState.size() - 1),
                    new ArrayList<>(queueState),
                    String.format("ENQUEUE operation: Added element [%d] at REAR. Front: [%d], Rear: [%d]",
                            val, queueState.get(0), val)
            ));
        }

        // DEQUEUE operation to demonstrate FIFO exit
        if (!queueState.isEmpty()) {
            int dequeued = queueState.remove(0);
            steps.add(new VisualizationStep(
                    stepNum++,
                    ActionType.DELETE,
                    List.of(0),
                    new ArrayList<>(queueState),
                    String.format("DEQUEUE operation: Removed element [%d] from FRONT. New Front: %s",
                            dequeued, queueState.isEmpty() ? "None (Empty)" : "[" + queueState.get(0) + "]")
            ));
        }

        // COMPLETE
        steps.add(new VisualizationStep(
                stepNum,
                ActionType.COMPLETE,
                List.of(),
                new ArrayList<>(queueState),
                "Queue FIFO operations execution completed!"
        ));

        return new VisualizationResponse(
                algorithmSlug,
                VisualizationType.QUEUE,
                steps
        );
    }
}
