package com.codeloom.dsa.visualization;

import com.codeloom.dsa.algorithm.entity.Algorithm;
import com.codeloom.dsa.algorithm.repository.AlgorithmRepository;
import com.codeloom.dsa.visualization.dto.GraphEdgeDto;
import com.codeloom.dsa.visualization.dto.GraphVisualizationRequest;
import com.codeloom.dsa.visualization.dto.VisualizationRequest;
import com.codeloom.dsa.visualization.dto.VisualizationResponse;
import com.codeloom.dsa.visualization.service.VisualizationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class Phase18BAllAlgorithmsVisualizationTest {

    @Autowired
    private VisualizationService visualizationService;

    @Autowired
    private AlgorithmRepository algorithmRepository;

    @Test
    @DisplayName("1. Verify 100% catalog coverage: Every algorithm returns >2 steps with explanations and non-null contract")
    void testAllAlgorithms_produceDynamicMultiStepVisualizations() {
        List<Algorithm> algorithms = algorithmRepository.findAll();
        assertFalse(algorithms.isEmpty(), "Algorithm catalog must not be empty");

        int totalCount = algorithms.size();
        int validCount = 0;

        GraphVisualizationRequest sampleGraph = new GraphVisualizationRequest(
                List.of("A", "B", "C"),
                List.of(new GraphEdgeDto("A", "B"), new GraphEdgeDto("B", "C")),
                "A"
        );
        VisualizationRequest defaultRequest = new VisualizationRequest(
                "ARRAY",
                List.of(1, 3, 5, 7, 9),
                5,
                sampleGraph,
                null, null, null, null, null, null, null
        );

        for (Algorithm algo : algorithms) {
            String slug = algo.getSlug();
            try {
                VisualizationResponse response = visualizationService.generateVisualization(slug, defaultRequest);

                assertNotNull(response, "Response must not be null for slug: " + slug);
                assertNotNull(response.steps(), "Steps list must not be null for slug: " + slug);
                assertTrue(response.steps().size() >= 3,
                        String.format("Algorithm '%s' produced only %d steps (expected >= 3)", slug, response.steps().size()));

                // Verify first step metadata
                var firstStep = response.steps().get(0);
                assertNotNull(firstStep.message(), "Message must not be null for slug: " + slug);
                assertNotNull(firstStep.whyMessage(), "WhyMessage must not be null for slug: " + slug);
                assertNotNull(firstStep.beginnerExplanation(), "BeginnerExplanation must not be null for slug: " + slug);

                validCount++;
            } catch (Exception e) {
                fail(String.format("Visualization failed for algorithm slug '%s': %s", slug, e.getMessage()));
            }
        }

        assertEquals(totalCount, validCount, "100% of catalog algorithms must generate valid multi-step visualizations!");
    }

    @Test
    @DisplayName("2. Verify audit report readyCount reflects seeded contracts (readyCount >= 15)")
    void testAuditReport_reflectsSeededContracts() {
        var audit = visualizationService.getAuditReport();
        assertNotNull(audit);
        assertTrue(audit.totalAlgorithms() >= 200, "Catalog should have at least 200 algorithms");
        assertTrue(audit.readyCount() >= 15, "Ready count should be at least 15 after Flyway contract seeds");
    }
}
