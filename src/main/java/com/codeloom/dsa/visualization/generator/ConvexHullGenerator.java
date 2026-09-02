package com.codeloom.dsa.visualization.generator;

import com.codeloom.dsa.visualization.dto.PointDto;
import com.codeloom.dsa.visualization.dto.VisualizationRequest;
import com.codeloom.dsa.visualization.dto.VisualizationResponse;
import com.codeloom.dsa.visualization.dto.VisualizationStep;
import com.codeloom.dsa.visualization.entity.ActionType;
import com.codeloom.dsa.visualization.entity.VisualizationType;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ConvexHullGenerator implements VisualizationGenerator {

    @Override
    public boolean supports(String slug) {
        if (slug == null) return false;
        String s = slug.toLowerCase();
        return s.contains("convex") || s.contains("jarvis") || s.contains("graham") || s.contains("hull");
    }

    @Override
    public VisualizationResponse generate(String slug, VisualizationRequest request) {
        List<PointDto> points = (request != null && request.points() != null && request.points().size() >= 3)
                ? request.points()
                : getDefaultSamplePoints();

        // Standardize labels P1, P2, P3...
        List<Map<String, Object>> pointMaps = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            PointDto p = points.get(i);
            String label = (p.label() != null && !p.label().isBlank()) ? p.label() : "P" + (i + 1);
            Map<String, Object> map = new HashMap<>();
            map.put("label", label);
            map.put("x", p.x());
            map.put("y", p.y());
            pointMaps.add(map);
        }

        if (pointMaps.size() < 3) {
            throw new IllegalArgumentException("Convex Hull requires at least 3 points.");
        }

        List<VisualizationStep> steps = new ArrayList<>();
        int stepNum = 1;

        // Step 1: Initial state
        Map<String, Object> initialCustomState = new HashMap<>();
        initialCustomState.put("points", pointMaps);
        initialCustomState.put("hull", List.of());
        initialCustomState.put("anchorPoint", null);
        initialCustomState.put("candidatePoint", null);
        initialCustomState.put("testPoint", null);
        initialCustomState.put("crossProduct", 0.0);

        steps.add(new VisualizationStep(
                stepNum++,
                ActionType.INITIAL,
                "Initializing Convex Hull (Jarvis March) with " + pointMaps.size() + " points on 2D plane.",
                Map.of("java", 1, "python", 1, "cpp", 1, "pseudocode", 1),
                "Loading 2D Cartesian point coordinates into memory.",
                "Starting Jarvis March (Gift Wrapping). Convex Hull algorithm computes the smallest convex polygon containing all points.",
                "Initial point set loaded onto coordinate canvas.",
                "Time: O(N*H) | Space: O(N) where H is hull size",
                initialCustomState
        ));

        // Step 2: Find leftmost point
        int startIdx = 0;
        for (int i = 1; i < pointMaps.size(); i++) {
            double currX = (double) pointMaps.get(i).get("x");
            double currY = (double) pointMaps.get(i).get("y");
            double startX = (double) pointMaps.get(startIdx).get("x");
            double startY = (double) pointMaps.get(startIdx).get("y");
            if (currX < startX || (currX == startX && currY < startY)) {
                startIdx = i;
            }
        }

        String startLabel = (String) pointMaps.get(startIdx).get("label");
        List<String> hullLabels = new ArrayList<>();
        hullLabels.add(startLabel);

        Map<String, Object> step2State = new HashMap<>();
        step2State.put("points", pointMaps);
        step2State.put("hull", new ArrayList<>(hullLabels));
        step2State.put("anchorPoint", startLabel);
        step2State.put("candidatePoint", null);
        step2State.put("testPoint", null);

        steps.add(new VisualizationStep(
                stepNum++,
                ActionType.SELECT,
                "Found leftmost point " + startLabel + " at (" + pointMaps.get(startIdx).get("x") + ", " + pointMaps.get(startIdx).get("y") + "). Added to Hull.",
                Map.of("java", 3, "python", 3, "cpp", 3, "pseudocode", 2),
                "Leftmost point is guaranteed to be part of the outer convex hull boundary.",
                "Minimum X coordinate evaluation: P_left = argmin_i(x_i).",
                "Establishing anchor point to begin wrapping clockwise/counter-clockwise around points.",
                "Time: O(N) scan for min X | Space: O(1)",
                step2State
        ));

        // Jarvis March Loop
        int currentIdx = startIdx;

        do {
            int nextIdx = (currentIdx + 1) % pointMaps.size();
            String currentLabel = (String) pointMaps.get(currentIdx).get("label");

            for (int i = 0; i < pointMaps.size(); i++) {
                if (i == currentIdx) continue;

                Map<String, Object> pAnchor = pointMaps.get(currentIdx);
                Map<String, Object> pCandidate = pointMaps.get(nextIdx);
                Map<String, Object> pTest = pointMaps.get(i);

                double ax = (double) pAnchor.get("x");
                double ay = (double) pAnchor.get("y");
                double cx = (double) pCandidate.get("x");
                double cy = (double) pCandidate.get("y");
                double tx = (double) pTest.get("x");
                double ty = (double) pTest.get("y");

                // Cross product: (c - a) x (t - a)
                double cross = (cx - ax) * (ty - ay) - (cy - ay) * (tx - ax);

                boolean updateCandidate = false;
                if (cross > 0) {
                    updateCandidate = true; // Counter-clockwise turn, test point is further left
                } else if (cross == 0) {
                    // Collinear: pick point farther from anchor
                    double distCandidate = Math.hypot(cx - ax, cy - ay);
                    double distTest = Math.hypot(tx - ax, ty - ay);
                    if (distTest > distCandidate) {
                        updateCandidate = true;
                    }
                }

                String candLabelBefore = (String) pCandidate.get("label");
                String testLabel = (String) pTest.get("label");

                if (updateCandidate) {
                    nextIdx = i;
                }

                String newCandLabel = (String) pointMaps.get(nextIdx).get("label");

                Map<String, Object> stepState = new HashMap<>();
                stepState.put("points", pointMaps);
                stepState.put("hull", new ArrayList<>(hullLabels));
                stepState.put("anchorPoint", currentLabel);
                stepState.put("candidatePoint", newCandLabel);
                stepState.put("testPoint", testLabel);
                stepState.put("crossProduct", cross);
                stepState.put("candidateEdge", Map.of("from", currentLabel, "to", candLabelBefore));
                stepState.put("testEdge", Map.of("from", currentLabel, "to", testLabel));

                String turnMsg = cross > 0 ? "CCW turn (cross > 0)" : (cross < 0 ? "CW turn (cross < 0)" : "Collinear (cross = 0)");
                String actionDesc = updateCandidate
                        ? " Point " + testLabel + " is more counter-clockwise. Updating candidate to " + newCandLabel + "."
                        : " Point " + candLabelBefore + " remains candidate.";

                steps.add(new VisualizationStep(
                        stepNum++,
                        updateCandidate ? ActionType.UPDATE : ActionType.COMPARE,
                        "Testing " + currentLabel + " -> " + testLabel + ". Cross-product = " + String.format("%.2f", cross) + " (" + turnMsg + ")." + actionDesc,
                        Map.of("java", 7, "python", 7, "cpp", 7, "pseudocode", 4),
                        "Evaluating orientation vector between anchor (" + currentLabel + "), current candidate, and test point (" + testLabel + ").",
                        "Cross product (P_cand - P_anch) x (P_test - P_anch). Positive value indicates CCW orientation.",
                        "To ensure candidate edge wraps around all remaining points.",
                        "Time: O(1) orientation check | Space: O(1)",
                        stepState
                ));
            }

            currentIdx = nextIdx;
            String nextLabel = (String) pointMaps.get(currentIdx).get("label");

            if (!nextLabel.equals(startLabel)) {
                hullLabels.add(nextLabel);
            }

            Map<String, Object> hullStepState = new HashMap<>();
            hullStepState.put("points", pointMaps);
            hullStepState.put("hull", new ArrayList<>(hullLabels));
            hullStepState.put("anchorPoint", nextLabel);
            hullStepState.put("candidatePoint", null);
            hullStepState.put("testPoint", null);

            steps.add(new VisualizationStep(
                    stepNum++,
                    ActionType.INSERT,
                    "Added point " + nextLabel + " to Convex Hull polygon. Current hull: " + String.join(" -> ", hullLabels),
                    Map.of("java", 10, "python", 10, "cpp", 10, "pseudocode", 5),
                    "Found most counter-clockwise point from anchor. Adding to hull perimeter.",
                    "Hull set boundary expanded: H = H U {" + nextLabel + "}.",
                    "Advancing anchor to newly appended hull vertex.",
                    "Time: O(N) per hull vertex | Cumulative: O(N * H)",
                    hullStepState
            ));

        } while (currentIdx != startIdx && hullLabels.size() < pointMaps.size());

        // Complete step
        Map<String, Object> completeState = new HashMap<>();
        completeState.put("points", pointMaps);
        completeState.put("hull", new ArrayList<>(hullLabels));
        completeState.put("anchorPoint", null);
        completeState.put("candidatePoint", null);
        completeState.put("testPoint", null);

        steps.add(new VisualizationStep(
                stepNum++,
                ActionType.COMPLETE,
                "Convex Hull (Jarvis March) execution complete! Formed polygon with " + hullLabels.size() + " vertices.",
                Map.of("java", 12, "python", 12, "cpp", 12, "pseudocode", 6),
                "Returned to initial starting vertex " + startLabel + ". Convex Hull is fully enclosed.",
                "Post-condition verified: Polygon contains all N points within its perimeter.",
                "Wrapping complete.",
                "Overall Time Complexity: O(N * H) | Space Complexity: O(N)",
                completeState
        ));

        return new VisualizationResponse(slug, VisualizationType.CONVEX_HULL, steps);
    }

    private List<PointDto> getDefaultSamplePoints() {
        return List.of(
                new PointDto(1, 1, "P1"),
                new PointDto(2, 5, "P2"),
                new PointDto(5, 4, "P3"),
                new PointDto(7, 2, "P4"),
                new PointDto(4, 0, "P5"),
                new PointDto(2, 2, "P6")
        );
    }
}
