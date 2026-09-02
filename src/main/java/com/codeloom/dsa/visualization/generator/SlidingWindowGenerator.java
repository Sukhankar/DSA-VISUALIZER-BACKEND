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
public class SlidingWindowGenerator implements VisualizationGenerator {

    private static final List<String> SUPPORTED_SLUGS = List.of(
            "sliding-window",
            "maximum-sum-subarray-sliding-window",
            "sliding-window-maximum",
            "minimum-size-subarray-sum"
    );

    @Override
    public boolean supports(String algorithmSlug) {
        if (algorithmSlug == null) return false;
        String s = algorithmSlug.toLowerCase();
        return SUPPORTED_SLUGS.contains(s) || s.contains("window");
    }

    @Override
    public VisualizationResponse generate(String algorithmSlug, VisualizationRequest request) {
        List<Integer> array = (request != null && request.input() != null && !request.input().isEmpty())
                ? request.input()
                : List.of(2, 1, 5, 1, 3, 2);

        int k = 3; // Window size k
        List<VisualizationStep> steps = new ArrayList<>();
        int stepNum = 1;

        steps.add(new VisualizationStep(
                stepNum++,
                ActionType.INITIAL,
                List.of(),
                new ArrayList<>(array),
                String.format("Initializing Sliding Window algorithm. Window size K = %d on array %s", k, array.toString())
        ));

        // Initial window sum
        int currentSum = 0;
        for (int i = 0; i < Math.min(k, array.size()); i++) {
            currentSum += array.get(i);
        }

        int maxSum = currentSum;
        int maxStartIdx = 0;

        steps.add(new VisualizationStep(
                stepNum++,
                ActionType.SELECT,
                List.of(0, Math.min(k - 1, array.size() - 1)),
                new ArrayList<>(array),
                String.format("Building initial window [0..%d]. Current window sum: %d", Math.min(k - 1, array.size() - 1), currentSum)
        ));

        // Slide window from i = k to array.length - 1
        for (int i = k; i < array.size(); i++) {
            int outgoingVal = array.get(i - k);
            int incomingVal = array.get(i);

            currentSum = currentSum - outgoingVal + incomingVal;

            boolean isNewMax = currentSum > maxSum;
            if (isNewMax) {
                maxSum = currentSum;
                maxStartIdx = i - k + 1;
            }

            steps.add(new VisualizationStep(
                    stepNum++,
                    isNewMax ? ActionType.UPDATE : ActionType.COMPARE,
                    List.of(i - k + 1, i),
                    new ArrayList<>(array),
                    String.format("Sliding window right to [%d..%d]. Removed %d, Added %d. Current Sum: %d | Max Sum: %d",
                            i - k + 1, i, outgoingVal, incomingVal, currentSum, maxSum)
            ));
        }

        steps.add(new VisualizationStep(
                stepNum,
                ActionType.COMPLETE,
                List.of(maxStartIdx, maxStartIdx + k - 1),
                new ArrayList<>(array),
                String.format("Sliding Window complete! Maximum Subarray Sum of size K=%d is %d starting at index %d.",
                        k, maxSum, maxStartIdx)
        ));

        return new VisualizationResponse(
                algorithmSlug,
                VisualizationType.TWO_POINTER,
                steps
        );
    }
}
