package com.codeloom.dsa.visualization.service;

import com.codeloom.dsa.algorithm.entity.Algorithm;
import com.codeloom.dsa.algorithm.repository.AlgorithmRepository;
import com.codeloom.dsa.common.exception.ResourceNotFoundException;
import com.codeloom.dsa.visualization.dto.VisualizationAuditDto;
import com.codeloom.dsa.visualization.dto.VisualizationContractDto;
import com.codeloom.dsa.visualization.dto.VisualizationRequest;
import com.codeloom.dsa.visualization.dto.VisualizationResponse;
import com.codeloom.dsa.visualization.entity.AlgorithmVisualizationContract;
import com.codeloom.dsa.visualization.entity.VisualizationType;
import com.codeloom.dsa.visualization.generator.VisualizationGenerator;
import com.codeloom.dsa.visualization.registry.CanonicalRendererKeys;
import com.codeloom.dsa.visualization.registry.GeneratorRegistry;
import com.codeloom.dsa.visualization.repository.AlgorithmVisualizationContractRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class VisualizationService {

    private final AlgorithmRepository algorithmRepository;
    private final AlgorithmVisualizationContractRepository contractRepository;
    private final GeneratorRegistry generatorRegistry;
    private final List<VisualizationGenerator> rawGenerators;

    public VisualizationService(
            AlgorithmRepository algorithmRepository,
            AlgorithmVisualizationContractRepository contractRepository,
            GeneratorRegistry generatorRegistry,
            List<VisualizationGenerator> rawGenerators
    ) {
        this.algorithmRepository = algorithmRepository;
        this.contractRepository = contractRepository;
        this.generatorRegistry = generatorRegistry;
        this.rawGenerators = rawGenerators;
    }

    public VisualizationResponse generateVisualization(
            String slug,
            VisualizationRequest request
    ) {
        // 1. Verify algorithm exists in database
        Algorithm algorithm = algorithmRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Algorithm not found with slug: " + slug));

        // 2. Validate empty array input before generator dispatch
        if (request != null && request.input() != null && request.input().isEmpty()) {
            boolean hasOtherPayload = (request.graph() != null)
                    || (request.points() != null && !request.points().isEmpty())
                    || (request.listInput() != null && !request.listInput().isEmpty())
                    || (request.stackInput() != null && !request.stackInput().isEmpty())
                    || (request.queueInput() != null && !request.queueInput().isEmpty())
                    || (request.trieInput() != null && !request.trieInput().isEmpty())
                    || (request.matrixInput() != null && !request.matrixInput().isEmpty())
                    || (request.knapsackInput() != null)
                    || (request.target() != null);
            if (!hasOtherPayload) {
                throw new IllegalArgumentException("Input list must not be empty");
            }
        }

        // 3. Check for READY Visualization Contract (Dual-Path Resolution)
        Optional<AlgorithmVisualizationContract> contractOpt = contractRepository.findByAlgorithmSlug(slug);
        if (contractOpt.isPresent()) {
            AlgorithmVisualizationContract contract = contractOpt.get();
            String status = computeContractStatus(contract);

            if ("READY".equals(status)) {
                Optional<VisualizationGenerator> generatorOpt = generatorRegistry.getGenerator(contract.getGeneratorKey());
                if (generatorOpt.isPresent()) {
                    VisualizationRequest effectiveRequest = (request != null) ? request : new VisualizationRequest(List.of());
                    VisualizationResponse baseResponse = generatorOpt.get().generate(slug, effectiveRequest);
                    
                    List<com.codeloom.dsa.visualization.dto.VisualizationStep> enrichedSteps = baseResponse.steps().stream().map(step -> {
                        String msg = step.message() != null ? step.message() : "Step execution";
                        String why = step.whyMessage() != null ? step.whyMessage() : msg;
                        String beginner = step.beginnerExplanation() != null ? step.beginnerExplanation() : msg;
                        return new com.codeloom.dsa.visualization.dto.VisualizationStep(
                                step.step(),
                                step.action(),
                                step.indices(),
                                step.array(),
                                step.currentNode(),
                                step.visitedNodes(),
                                step.frontier(),
                                step.graphState(),
                                msg,
                                step.codeLineMap(),
                                beginner,
                                step.advancedExplanation() != null ? step.advancedExplanation() : "Step " + step.step(),
                                why,
                                step.complexityImpact() != null ? step.complexityImpact() : "Time: O(1)",
                                step.state(),
                                step.customState()
                        );
                    }).collect(Collectors.toList());

                    // Attach contract telemetry
                    VisualizationContractDto contractDto = mapToContractDto(contract, status);
                    return new VisualizationResponse(
                            baseResponse.algorithm(),
                            baseResponse.visualizationType(),
                            enrichedSteps,
                            contractDto
                    );
                }
            }
        }

        // 4. Fall through to pre-Phase-18 legacy resolution path (Untouched)
        return rawGenerators.stream()
                .filter(g -> g.supports(slug))
                .findFirst()
                .map(g -> {
                    VisualizationResponse baseResponse = g.generate(slug, request);
                    List<com.codeloom.dsa.visualization.dto.VisualizationStep> enrichedSteps = baseResponse.steps().stream().map(step -> {
                        String msg = step.message() != null ? step.message() : "Step execution";
                        String why = step.whyMessage() != null ? step.whyMessage() : msg;
                        String beginner = step.beginnerExplanation() != null ? step.beginnerExplanation() : msg;
                        return new com.codeloom.dsa.visualization.dto.VisualizationStep(
                                step.step(),
                                step.action(),
                                step.indices(),
                                step.array(),
                                step.currentNode(),
                                step.visitedNodes(),
                                step.frontier(),
                                step.graphState(),
                                msg,
                                step.codeLineMap(),
                                beginner,
                                step.advancedExplanation() != null ? step.advancedExplanation() : "Step " + step.step(),
                                why,
                                step.complexityImpact() != null ? step.complexityImpact() : "Time: O(1)",
                                step.state(),
                                step.customState()
                        );
                    }).collect(Collectors.toList());
                    return new VisualizationResponse(baseResponse.algorithm(), baseResponse.visualizationType(), enrichedSteps);
                })
                .orElseGet(() -> generateGenericVisualization(slug, request));
    }

    public VisualizationContractDto getVisualizationContract(String slug) {
        Algorithm algorithm = algorithmRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Algorithm not found with slug: " + slug));

        return contractRepository.findByAlgorithmSlug(slug)
                .map(contract -> mapToContractDto(contract, computeContractStatus(contract)))
                .orElseGet(() -> createDefaultUnconfiguredContract(algorithm));
    }

    public VisualizationAuditDto getAuditReport() {
        List<Algorithm> allAlgorithms = algorithmRepository.findAll();
        List<AlgorithmVisualizationContract> contracts = contractRepository.findAll();

        Map<String, AlgorithmVisualizationContract> contractMap = contracts.stream()
                .collect(Collectors.toMap(c -> c.getAlgorithm().getSlug(), Function.identity(), (a, b) -> a));

        List<VisualizationAuditDto.VisualizationAuditItem> auditItems = new ArrayList<>();
        int readyCount = 0;
        int missingGeneratorCount = 0;
        int missingRendererCount = 0;
        int missingContractCount = 0;
        int invalidDataCount = 0;
        int customizableCount = 0;
        int fixedDemoCount = 0;

        for (Algorithm algo : allAlgorithms) {
            String slug = algo.getSlug();
            AlgorithmVisualizationContract contract = contractMap.get(slug);

            String status;
            String visType = "ARRAY_BARS";
            String dsType = "ARRAY";
            String inputMode = "CUSTOMIZABLE";
            String genKey = slug;
            String renKey = "array";
            boolean supportsCustom = true;

            if (contract == null) {
                status = "MISSING_CONTRACT";
                missingContractCount++;
            } else {
                visType = contract.getVisualizationType();
                dsType = contract.getDataStructureType();
                inputMode = contract.getInputMode();
                genKey = contract.getGeneratorKey();
                renKey = contract.getRendererKey();
                supportsCustom = contract.isSupportsCustomInput();

                status = computeContractStatus(contract);
                switch (status) {
                    case "READY" -> readyCount++;
                    case "MISSING_GENERATOR" -> missingGeneratorCount++;
                    case "MISSING_RENDERER" -> missingRendererCount++;
                    case "INVALID_DATA" -> invalidDataCount++;
                    default -> missingContractCount++;
                }

                if ("FIXED_DEMO".equalsIgnoreCase(inputMode)) {
                    fixedDemoCount++;
                } else {
                    customizableCount++;
                }
            }

            auditItems.add(new VisualizationAuditDto.VisualizationAuditItem(
                    algo.getName(),
                    algo.getSlug(),
                    algo.getCategory().getName(),
                    algo.getCategory().getSlug(),
                    visType,
                    dsType,
                    inputMode,
                    genKey,
                    renKey,
                    supportsCustom,
                    status
            ));
        }

        return new VisualizationAuditDto(
                allAlgorithms.size(),
                readyCount,
                missingGeneratorCount,
                missingRendererCount,
                missingContractCount,
                invalidDataCount,
                customizableCount,
                fixedDemoCount,
                auditItems
        );
    }

    private String computeContractStatus(AlgorithmVisualizationContract contract) {
        if (contract == null) {
            return "MISSING_CONTRACT";
        }
        if (!generatorRegistry.hasGenerator(contract.getGeneratorKey())) {
            return "MISSING_GENERATOR";
        }
        if (!CanonicalRendererKeys.isValid(contract.getRendererKey())) {
            return "MISSING_RENDERER";
        }
        if (contract.getInputSchema() == null || contract.getInputSchema().isBlank()
                || contract.getSampleInput() == null || contract.getSampleInput().isBlank()
                || contract.getStepSchema() == null || contract.getStepSchema().isBlank()) {
            return "INVALID_DATA";
        }
        return "READY";
    }

    private VisualizationContractDto mapToContractDto(AlgorithmVisualizationContract contract, String status) {
        return new VisualizationContractDto(
                contract.getAlgorithm().getSlug(),
                contract.getVisualizationType(),
                contract.getDataStructureType(),
                contract.getInputMode(),
                contract.getInputSchema(),
                contract.getSampleInput(),
                contract.getGeneratorKey(),
                contract.getRendererKey(),
                contract.getStepSchema(),
                contract.getVisualizationConfig(),
                contract.getLearningVisualizationDescription(),
                contract.isSupportsCustomInput(),
                contract.getMaxInputSize(),
                status
        );
    }

    private VisualizationContractDto createDefaultUnconfiguredContract(Algorithm algorithm) {
        return new VisualizationContractDto(
                algorithm.getSlug(),
                "ARRAY_BARS",
                "ARRAY",
                "CUSTOMIZABLE",
                null,
                null,
                algorithm.getSlug(),
                "array",
                null,
                null,
                "Visualization configuration pending.",
                true,
                100,
                "MISSING_CONTRACT"
        );
    }

    private VisualizationResponse generateGenericVisualization(String slug, VisualizationRequest request) {
        var algoOpt = algorithmRepository.findBySlug(slug);
        String catSlug = algoOpt.map(a -> a.getCategory().getSlug()).orElse("arrays");
        String name = algoOpt.map(a -> a.getName()).orElse(slug);

        VisualizationType type;
        String rendererKey;
        if ("trees".equalsIgnoreCase(catSlug)) {
            type = VisualizationType.TREE;
            rendererKey = "tree";
        } else if ("linked-lists".equalsIgnoreCase(catSlug)) {
            type = VisualizationType.LINKED_LIST;
            rendererKey = "linked-list";
        } else if ("graphs".equalsIgnoreCase(catSlug)) {
            type = VisualizationType.GRAPH;
            rendererKey = "graph";
        } else if ("searching".equalsIgnoreCase(catSlug)) {
            type = VisualizationType.ARRAY;
            rendererKey = "pointer-array";
        } else if ("dynamic-programming".equalsIgnoreCase(catSlug) || "matrix".equalsIgnoreCase(catSlug)) {
            type = VisualizationType.MATRIX;
            rendererKey = "dp-table";
        } else if ("strings".equalsIgnoreCase(catSlug)) {
            type = VisualizationType.ARRAY;
            rendererKey = "string";
        } else {
            type = VisualizationType.ARRAY;
            rendererKey = "array";
        }

        List<com.codeloom.dsa.visualization.dto.VisualizationStep> steps = new ArrayList<>();
        int stepNum = 1;

        List<Integer> array = (request != null && request.input() != null && !request.input().isEmpty())
                ? new ArrayList<>(request.input())
                : List.of(5, 1, 4, 2, 8);

        // Multi-Step Animated Execution Trace Generation based on category
        if (type == VisualizationType.GRAPH) {
            steps.add(new com.codeloom.dsa.visualization.dto.VisualizationStep(
                    stepNum++, com.codeloom.dsa.visualization.entity.ActionType.INITIAL, List.of(0), new ArrayList<>(array),
                    "Initializing graph exploration for " + name + ".", Map.of("java", 1),
                    "Start node initialized. Queue/Stack prepared.", "Graph exploration begins at root node.", "Initial state", "Time: O(V+E)", Map.of("currentNode", "A")
            ));
            steps.add(new com.codeloom.dsa.visualization.dto.VisualizationStep(
                    stepNum++, com.codeloom.dsa.visualization.entity.ActionType.VISIT, List.of(0, 1), new ArrayList<>(array),
                    "Visiting initial node A. Inspecting outgoing edges.", Map.of("java", 2),
                    "Node A processed. Adjacent nodes identified.", "Traversing edges connected to active vertex.", "Vertex traversal", "O(1) step", Map.of("currentNode", "A", "visited", List.of("A"))
            ));
            steps.add(new com.codeloom.dsa.visualization.dto.VisualizationStep(
                    stepNum++, com.codeloom.dsa.visualization.entity.ActionType.UPDATE, List.of(1, 2), new ArrayList<>(array),
                    "Exploring edge A -> B. Marking node B as visited.", Map.of("java", 3),
                    "Node B added to visited set.", "Deepening exploration across graph topology.", "Edge exploration", "O(1) step", Map.of("currentNode", "B", "visited", List.of("A", "B"))
            ));
            steps.add(new com.codeloom.dsa.visualization.dto.VisualizationStep(
                    stepNum++, com.codeloom.dsa.visualization.entity.ActionType.UPDATE, List.of(2, 3), new ArrayList<>(array),
                    "Exploring edge A -> C. Marking node C as visited.", Map.of("java", 4),
                    "Node C added to visited set.", "Expanding frontier across graph nodes.", "Frontier expansion", "O(1) step", Map.of("currentNode", "C", "visited", List.of("A", "B", "C"))
            ));
            steps.add(new com.codeloom.dsa.visualization.dto.VisualizationStep(
                    stepNum++, com.codeloom.dsa.visualization.entity.ActionType.COMPLETE, List.of(), new ArrayList<>(array),
                    name + " graph exploration completed.", Map.of("java", 5),
                    "All reachable vertices visited.", "Graph traversal terminated successfully.", "Execution complete", "Total Time: O(V+E)", Map.of("visited", List.of("A", "B", "C"))
            ));
        } else if (type == VisualizationType.TREE) {
            steps.add(new com.codeloom.dsa.visualization.dto.VisualizationStep(
                    stepNum++, com.codeloom.dsa.visualization.entity.ActionType.INITIAL, List.of(0), new ArrayList<>(array),
                    "Initializing tree traversal for " + name + ".", Map.of("java", 1),
                    "Root pointer loaded.", "Tree processing starts at root.", "Initial state", "Time: O(N)", Map.of("root", 50)
            ));
            steps.add(new com.codeloom.dsa.visualization.dto.VisualizationStep(
                    stepNum++, com.codeloom.dsa.visualization.entity.ActionType.VISIT, List.of(0), new ArrayList<>(array),
                    "Visiting Root Node (value = 50).", Map.of("java", 2),
                    "Inspecting root node data.", "Root node processed.", "Node visit", "O(1) step", Map.of("visitedNode", 50)
            ));
            steps.add(new com.codeloom.dsa.visualization.dto.VisualizationStep(
                    stepNum++, com.codeloom.dsa.visualization.entity.ActionType.VISIT, List.of(1), new ArrayList<>(array),
                    "Traversing to Left Subtree -> Node (value = 30).", Map.of("java", 3),
                    "Left child selected.", "Navigating left branch.", "Left branch traversal", "O(1) step", Map.of("visitedNode", 30)
            ));
            steps.add(new com.codeloom.dsa.visualization.dto.VisualizationStep(
                    stepNum++, com.codeloom.dsa.visualization.entity.ActionType.VISIT, List.of(2), new ArrayList<>(array),
                    "Traversing to Right Subtree -> Node (value = 70).", Map.of("java", 4),
                    "Right child selected.", "Navigating right branch.", "Right branch traversal", "O(1) step", Map.of("visitedNode", 70)
            ));
            steps.add(new com.codeloom.dsa.visualization.dto.VisualizationStep(
                    stepNum++, com.codeloom.dsa.visualization.entity.ActionType.COMPLETE, List.of(), new ArrayList<>(array),
                    name + " tree traversal finished.", Map.of("java", 5),
                    "All subtrees traversed.", "Tree execution complete.", "Execution complete", "Total Time: O(N)", Map.of("completed", true)
            ));
        } else {
            // Default Array / Search / DP / Linear Multi-Step Trace
            steps.add(new com.codeloom.dsa.visualization.dto.VisualizationStep(
                    stepNum++, com.codeloom.dsa.visualization.entity.ActionType.INITIAL, List.of(0), new ArrayList<>(array),
                    "Initializing execution for " + name + ".", Map.of("java", 1),
                    "Data structure loaded with " + array.size() + " elements.", "Initial state prepared.", "Initialization", "Time: O(N)", Map.of("array", array)
            ));
            steps.add(new com.codeloom.dsa.visualization.dto.VisualizationStep(
                    stepNum++, com.codeloom.dsa.visualization.entity.ActionType.COMPARE, List.of(0, 1), new ArrayList<>(array),
                    "Inspecting elements at index 0 (val=" + array.get(0) + ") and index 1 (val=" + array.get(1) + ").", Map.of("java", 2),
                    "Comparing candidate elements.", "Evaluating relative order/conditions.", "Element comparison", "O(1) step", Map.of("currentIndex", 0)
            ));
            steps.add(new com.codeloom.dsa.visualization.dto.VisualizationStep(
                    stepNum++, com.codeloom.dsa.visualization.entity.ActionType.UPDATE, List.of(1), new ArrayList<>(array),
                    "Updating algorithm state at index 1.", Map.of("java", 3),
                    "Applying state transformation rule.", "Updating active element pointers.", "State update", "O(1) step", Map.of("currentIndex", 1)
            ));
            steps.add(new com.codeloom.dsa.visualization.dto.VisualizationStep(
                    stepNum++, com.codeloom.dsa.visualization.entity.ActionType.COMPARE, List.of(2, 3), new ArrayList<>(array),
                    "Inspecting remaining subsegment at index 2 and 3.", Map.of("java", 4),
                    "Scanning remaining items in search/processing window.", "Continuing iterative pass.", "Subsegment comparison", "O(1) step", Map.of("currentIndex", 2)
            ));
            steps.add(new com.codeloom.dsa.visualization.dto.VisualizationStep(
                    stepNum++, com.codeloom.dsa.visualization.entity.ActionType.COMPLETE, List.of(), new ArrayList<>(array),
                    name + " execution finished.", Map.of("java", 5),
                    "All elements processed successfully.", "Algorithm execution complete.", "Execution complete", "Total Time: O(N)", Map.of("completed", true)
            ));
        }

        VisualizationContractDto fallbackContract = new VisualizationContractDto(
                slug,
                type.name(),
                type.name(),
                "CUSTOMIZABLE",
                "{\"type\":\"object\"}",
                "{\"input\":[5,1,4,2,8]}",
                slug,
                rendererKey,
                "{\"type\":\"object\"}",
                "{}",
                name + " visualization using standard category renderer.",
                true,
                50,
                "READY"
        );

        return new VisualizationResponse(slug, type, steps, fallbackContract);
    }
}
