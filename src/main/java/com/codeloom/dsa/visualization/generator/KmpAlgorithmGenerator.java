package com.codeloom.dsa.visualization.generator;

import com.codeloom.dsa.visualization.dto.VisualizationRequest;
import com.codeloom.dsa.visualization.dto.VisualizationResponse;
import com.codeloom.dsa.visualization.dto.VisualizationStep;
import com.codeloom.dsa.visualization.entity.ActionType;
import com.codeloom.dsa.visualization.entity.VisualizationType;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class KmpAlgorithmGenerator implements VisualizationGenerator {

    private static final String SLUG = "kmp-string-matching-hard";

    @Override
    public boolean supports(String algorithmSlug) {
        return SLUG.equalsIgnoreCase(algorithmSlug);
    }

    @Override
    public VisualizationResponse generate(String algorithmSlug, VisualizationRequest request) {
        String text = (request != null && request.graph() == null && request.input() != null && !request.input().isEmpty())
                ? request.input().toString()
                : "ABABDABACDABABCABAB";
        String pattern = "ABABCABAB";

        List<VisualizationStep> steps = new ArrayList<>();
        int stepNum = 1;

        // Compute LPS (Longest Prefix Suffix) array
        int M = pattern.length();
        int N = text.length();
        int[] lps = new int[M];
        computeLpsArray(pattern, M, lps);

        // Convert string to character code list for array field
        List<Object> charCodes = new ArrayList<>();
        for (char c : text.toCharArray()) {
            charCodes.add((int) c);
        }

        // 1. Initial Step
        steps.add(new VisualizationStep(
                stepNum++,
                ActionType.INITIAL,
                List.of(0),
                charCodes,
                String.format("KMP Pattern Search initialized. Precomputed LPS table for pattern '%s'.", pattern),
                Map.of("java", 1, "python", 1, "cpp", 1),
                String.format("KMP avoids re-checking text characters by using LPS table: %s.", Arrays.toString(lps)),
                "LPS (Longest Proper Prefix which is also Suffix) allows pattern shift without backing up text pointer i.",
                "Text pointer i = 0, Pattern pointer j = 0.",
                "Time: O(N + M) | Space: O(M)",
                Map.of("text", text, "pattern", pattern, "lps", lps, "textIndex", 0, "patternIndex", 0)
        ));

        int i = 0; // index for text
        int j = 0; // index for pattern
        boolean matchFound = false;

        while (i < N) {
            char tChar = text.charAt(i);
            char pChar = pattern.charAt(j);

            // 2. Character Compare Step
            steps.add(new VisualizationStep(
                    stepNum++,
                    ActionType.COMPARE,
                    List.of(i, j),
                    charCodes,
                    String.format("Comparing text[%d] ('%c') with pattern[%d] ('%c').", i, tChar, j, pChar),
                    Map.of("java", 3, "python", 3, "cpp", 3),
                    String.format("Comparing text char '%c' at index %d against pattern char '%c' at index %d.", tChar, i, pChar, j),
                    "Evaluates character equality at active alignment.",
                    String.format("Text[%d] == Pattern[%d]?", i, j),
                    "Comparison takes O(1) time.",
                    Map.of("textIndex", i, "patternIndex", j, "textChar", String.valueOf(tChar), "patternChar", String.valueOf(pChar))
            ));

            if (pChar == tChar) {
                i++;
                j++;
            }

            if (j == M) {
                matchFound = true;
                int matchPos = i - j;
                steps.add(new VisualizationStep(
                        stepNum++,
                        ActionType.FOUND,
                        List.of(matchPos, i - 1),
                        charCodes,
                        String.format("Full pattern match found at text index %d!", matchPos),
                        Map.of("java", 4, "python", 4, "cpp", 4),
                        String.format("Pattern '%s' completely matched in text starting at index %d.", pattern, matchPos),
                        "All pattern characters matched successfully.",
                        String.format("Match confirmed at index %d.", matchPos),
                        "Occurs in linear time.",
                        Map.of("matchIndex", matchPos, "found", true)
                ));
                j = lps[j - 1];
            } else if (i < N && text.charAt(i) != pattern.charAt(j)) {
                if (j != 0) {
                    int oldJ = j;
                    j = lps[j - 1];
                    steps.add(new VisualizationStep(
                            stepNum++,
                            ActionType.UPDATE,
                            List.of(i, j),
                            charCodes,
                            String.format("Mismatch! Using LPS table: pattern pointer j shifted from %d to %d (LPS[%d]).", oldJ, j, oldJ - 1),
                            Map.of("java", 5, "python", 5, "cpp", 5),
                            String.format("Mismatch at pattern index %d. LPS[%d] = %d allows skipping redundant prefix checks.", oldJ, oldJ - 1, j),
                            "KMP keeps text pointer i fixed while shifting pattern pointer j according to LPS.",
                            String.format("Pattern pointer shifted to %d.", j),
                            "Zero text backtracking required.",
                            Map.of("textIndex", i, "patternIndex", j, "lpsShift", true)
                    ));
                } else {
                    i++;
                    steps.add(new VisualizationStep(
                            stepNum++,
                            ActionType.UPDATE,
                            List.of(i),
                            charCodes,
                            String.format("Mismatch at pattern index 0. Advancing text pointer i to %d.", i),
                            Map.of("java", 6, "python", 6, "cpp", 6),
                            "No pattern prefix match available. Advancing text pointer to next character.",
                            "Increments text index i.",
                            String.format("Advancing text pointer to %d.", i),
                            "O(1) step.",
                            Map.of("textIndex", i, "patternIndex", 0)
                    ));
                }
            }
        }

        steps.add(new VisualizationStep(
                stepNum,
                ActionType.COMPLETE,
                List.of(),
                charCodes,
                "KMP Algorithm pattern search completed.",
                Map.of("java", 7, "python", 7, "cpp", 7),
                "Text search completed.",
                "Knuth-Morris-Pratt O(N + M) execution verified.",
                "Process complete.",
                "Final Complexity: O(N + M) Time, O(M) Space",
                Map.of("found", matchFound)
        ));

        return new VisualizationResponse(SLUG, VisualizationType.ARRAY, steps);
    }

    private void computeLpsArray(String pat, int M, int[] lps) {
        int len = 0;
        int i = 1;
        lps[0] = 0;
        while (i < M) {
            if (pat.charAt(i) == pat.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = lps[len - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
    }
}
