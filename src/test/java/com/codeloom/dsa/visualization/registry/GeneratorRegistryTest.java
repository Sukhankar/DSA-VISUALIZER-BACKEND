package com.codeloom.dsa.visualization.registry;

import com.codeloom.dsa.visualization.generator.BubbleSortGenerator;
import com.codeloom.dsa.visualization.generator.VisualizationGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class GeneratorRegistryTest {

    @Test
    @DisplayName("Assert GeneratorRegistry correctly discovers and looks up generators by key")
    void testGeneratorLookup() {
        BubbleSortGenerator bubbleSortGenerator = new BubbleSortGenerator();
        GeneratorRegistry registry = new GeneratorRegistry(List.of(bubbleSortGenerator));

        assertTrue(registry.hasGenerator("bubble-sort"));
        Optional<VisualizationGenerator> generatorOpt = registry.getGenerator("bubble-sort");
        assertTrue(generatorOpt.isPresent());
        assertEquals(bubbleSortGenerator, generatorOpt.get());

        assertFalse(registry.hasGenerator("non-existent-generator"));
    }
}
