package com.codeloom.dsa.algorithm.dto;

import com.codeloom.dsa.learning.entity.ExperienceLevel;
import java.util.List;
import java.util.UUID;

public record AlgorithmLearningResponse(
        UUID algorithmId,
        String algorithmName,
        String algorithmSlug,
        ExperienceLevel level,
        String introduction,
        String problemStatement,
        String intuition,
        String whyItWorks,
        List<String> howItWorks,
        String pseudocode,
        String complexitySummary,
        String whenToUse,
        String whenNotToUse,
        String advantages,
        String limitations,
        String commonMistakes,
        String interviewTips,
        String implementationNotes,
        AdvancedTheoryResponse advancedTheory,
        List<PracticeRecommendationResponse> practiceRecommendations
) {
    public record AdvancedTheoryResponse(
            String mathematicalFoundation,
            String invariant,
            String correctnessProof,
            String recurrence,
            String recurrenceSolution,
            String optimization,
            String memoryAnalysis,
            String advancedTradeoffs,
            String competitiveProgrammingNotes
    ) {}

    public record PracticeRecommendationResponse(
            String problemTitle,
            String problemSlug,
            String difficulty,
            String platform
    ) {}
}
