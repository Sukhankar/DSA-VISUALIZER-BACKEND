package com.codeloom.dsa.visualization.registry;

import com.codeloom.dsa.visualization.generator.VisualizationGenerator;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GeneratorRegistry {

    private final List<VisualizationGenerator> generators;
    private final Map<String, VisualizationGenerator> keyToGeneratorMap = new ConcurrentHashMap<>();

    public GeneratorRegistry(List<VisualizationGenerator> generators) {
        this.generators = generators;
        registerGenerators();
    }

    private void registerGenerators() {
        for (VisualizationGenerator g : generators) {
            String className = g.getClass().getSimpleName();
            // E.g. BubbleSortGenerator -> bubble-sort
            String keyFromClass = className.replace("Generator", "")
                    .replaceAll("(.)(\\p{Upper})", "$1-$2")
                    .toLowerCase();
            keyToGeneratorMap.put(keyFromClass, g);
        }
    }

    public Optional<VisualizationGenerator> getGenerator(String generatorKey) {
        if (generatorKey == null || generatorKey.isBlank()) {
            return Optional.empty();
        }
        String normalizedKey = generatorKey.trim().toLowerCase();
        
        // 1. Direct map lookup
        if (keyToGeneratorMap.containsKey(normalizedKey)) {
            return Optional.of(keyToGeneratorMap.get(normalizedKey));
        }

        // 2. Fallback to generator.supports(generatorKey)
        return generators.stream()
                .filter(g -> g.supports(normalizedKey) || g.supports(generatorKey))
                .findFirst();
    }

    public boolean hasGenerator(String generatorKey) {
        return getGenerator(generatorKey).isPresent();
    }

    public List<VisualizationGenerator> getAllGenerators() {
        return Collections.unmodifiableList(generators);
    }
}
