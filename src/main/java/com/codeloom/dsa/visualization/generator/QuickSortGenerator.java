package com.codeloom.dsa.visualization.generator;

import com.codeloom.dsa.visualization.dto.VisualizationRequest;
import com.codeloom.dsa.visualization.dto.VisualizationResponse;
import com.codeloom.dsa.visualization.dto.VisualizationStep;
import com.codeloom.dsa.visualization.entity.ActionType;
import com.codeloom.dsa.visualization.entity.VisualizationType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class QuickSortGenerator implements VisualizationGenerator {

    private static final String SLUG = "quick-sort";

    @Override
    public boolean supports(String algorithmSlug) {
        return SLUG.equalsIgnoreCase(algorithmSlug);
    }

    @Override
    public VisualizationResponse generate(String algorithmSlug, VisualizationRequest request) {
        List<Integer> array = new ArrayList<>(request.input());
        List<VisualizationStep> steps = new ArrayList<>();
        AtomicInteger stepNum = new AtomicInteger(1);

        // 1. Initial State
        steps.add(new VisualizationStep(
                stepNum.getAndIncrement(),
                ActionType.INITIAL,
                List.of(),
                new ArrayList<>(array),
                "Initial array state"
        ));

        // 2. Quick Sort Logic
        if (array.size() > 1) {
            quickSort(array, 0, array.size() - 1, steps, stepNum);
        }

        // 3. Completion Step
        steps.add(new VisualizationStep(
                stepNum.get(),
                ActionType.COMPLETE,
                List.of(),
                new ArrayList<>(array),
                "Quick Sort completed! Array is fully sorted."
        ));

        return new VisualizationResponse(
                SLUG,
                VisualizationType.ARRAY,
                steps
        );
    }

    private void quickSort(List<Integer> array, int low, int high, List<VisualizationStep> steps, AtomicInteger stepNum) {
        if (low < high) {
            int pivotIndex = partition(array, low, high, steps, stepNum);
            quickSort(array, low, pivotIndex - 1, steps, stepNum);
            quickSort(array, pivotIndex + 1, high, steps, stepNum);
        }
    }

    private int partition(List<Integer> array, int low, int high, List<VisualizationStep> steps, AtomicInteger stepNum) {
        int pivot = array.get(high);

        // Select pivot
        steps.add(new VisualizationStep(
                stepNum.getAndIncrement(),
                ActionType.SELECT,
                List.of(high),
                new ArrayList<>(array),
                String.format("Selected pivot %d at index %d for subarray [%d..%d]", pivot, high, low, high)
        ));

        int i = low - 1;

        for (int j = low; j < high; j++) {
            int valJ = array.get(j);

            steps.add(new VisualizationStep(
                    stepNum.getAndIncrement(),
                    ActionType.COMPARE,
                    List.of(j, high),
                    new ArrayList<>(array),
                    String.format("Comparing element %d at index %d with pivot %d at index %d", valJ, j, pivot, high)
            ));

            if (valJ <= pivot) {
                i++;
                if (i != j) {
                    int oldI = array.get(i);
                    Collections.swap(array, i, j);

                    steps.add(new VisualizationStep(
                            stepNum.getAndIncrement(),
                            ActionType.SWAP,
                            List.of(i, j),
                            new ArrayList<>(array),
                            String.format("Swapped %d and %d at indices %d and %d (%d <= pivot %d)", oldI, valJ, i, j, valJ, pivot)
                    ));
                }
            }
        }

        // Place pivot in correct position
        int oldPos = array.get(i + 1);
        Collections.swap(array, i + 1, high);

        steps.add(new VisualizationStep(
                stepNum.getAndIncrement(),
                ActionType.SWAP,
                List.of(i + 1, high),
                new ArrayList<>(array),
                String.format("Placed pivot %d into final sorted partition position index %d (swapped with %d)", pivot, i + 1, oldPos)
        ));

        return i + 1;
    }
}
