package com.codeloom.dsa.visualization.generator;

import com.codeloom.dsa.visualization.dto.VisualizationRequest;
import com.codeloom.dsa.visualization.dto.VisualizationResponse;
import com.codeloom.dsa.visualization.dto.VisualizationStep;
import com.codeloom.dsa.visualization.entity.ActionType;
import com.codeloom.dsa.visualization.entity.VisualizationType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class MergeSortGenerator implements VisualizationGenerator {

    private static final String SLUG = "merge-sort";

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

        // 2. Merge Sort Logic
        if (array.size() > 1) {
            mergeSort(array, 0, array.size() - 1, steps, stepNum);
        }

        // 3. Completion Step
        steps.add(new VisualizationStep(
                stepNum.get(),
                ActionType.COMPLETE,
                List.of(),
                new ArrayList<>(array),
                "Merge Sort completed! Array is fully sorted."
        ));

        return new VisualizationResponse(
                SLUG,
                VisualizationType.ARRAY,
                steps
        );
    }

    private void mergeSort(List<Integer> array, int left, int right, List<VisualizationStep> steps, AtomicInteger stepNum) {
        if (left >= right) {
            return;
        }

        int mid = left + (right - left) / 2;

        mergeSort(array, left, mid, steps, stepNum);
        mergeSort(array, mid + 1, right, steps, stepNum);
        merge(array, left, mid, right, steps, stepNum);
    }

    private void merge(List<Integer> array, int left, int mid, int right, List<VisualizationStep> steps, AtomicInteger stepNum) {
        List<Integer> leftSub = new ArrayList<>(array.subList(left, mid + 1));
        List<Integer> rightSub = new ArrayList<>(array.subList(mid + 1, right + 1));

        int i = 0;
        int j = 0;
        int k = left;

        while (i < leftSub.size() && j < rightSub.size()) {
            int valLeft = leftSub.get(i);
            int valRight = rightSub.get(j);
            int idxLeft = left + i;
            int idxRight = mid + 1 + j;

            steps.add(new VisualizationStep(
                    stepNum.getAndIncrement(),
                    ActionType.COMPARE,
                    List.of(idxLeft, idxRight),
                    new ArrayList<>(array),
                    String.format("Comparing %d (left subarray at %d) and %d (right subarray at %d)", valLeft, idxLeft, valRight, idxRight)
            ));

            if (valLeft <= valRight) {
                array.set(k, valLeft);
                i++;
            } else {
                array.set(k, valRight);
                j++;
            }

            steps.add(new VisualizationStep(
                    stepNum.getAndIncrement(),
                    ActionType.UPDATE,
                    List.of(k),
                    new ArrayList<>(array),
                    String.format("Placed %d into position %d", array.get(k), k)
            ));

            k++;
        }

        while (i < leftSub.size()) {
            array.set(k, leftSub.get(i));
            steps.add(new VisualizationStep(
                    stepNum.getAndIncrement(),
                    ActionType.UPDATE,
                    List.of(k),
                    new ArrayList<>(array),
                    String.format("Placed remaining left element %d into position %d", array.get(k), k)
            ));
            i++;
            k++;
        }

        while (j < rightSub.size()) {
            array.set(k, rightSub.get(j));
            steps.add(new VisualizationStep(
                    stepNum.getAndIncrement(),
                    ActionType.UPDATE,
                    List.of(k),
                    new ArrayList<>(array),
                    String.format("Placed remaining right element %d into position %d", array.get(k), k)
            ));
            j++;
            k++;
        }
    }
}
