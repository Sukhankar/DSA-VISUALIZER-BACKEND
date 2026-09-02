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
                    
                    // Attach contract telemetry
                    VisualizationContractDto contractDto = mapToContractDto(contract, status);
                    return new VisualizationResponse(
                            baseResponse.algorithm(),
                            baseResponse.visualizationType(),
                            baseResponse.steps(),
                            contractDto
                    );
                }
            }
        }

        // 4. Fall through to pre-Phase-18 legacy resolution path (Untouched)
        return rawGenerators.stream()
                .filter(g -> g.supports(slug))
                .findFirst()
                .map(g -> g.generate(slug, request))
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
        if ("trees".equalsIgnoreCase(catSlug)) {
            type = VisualizationType.TREE;
        } else if ("linked-lists".equalsIgnoreCase(catSlug)) {
            type = VisualizationType.LINKED_LIST;
        } else if ("graphs".equalsIgnoreCase(catSlug)) {
            type = VisualizationType.GRAPH;
        } else {
            type = VisualizationType.ARRAY;
        }

        List<com.codeloom.dsa.visualization.dto.VisualizationStep> steps = new ArrayList<>();
        int stepNum = 1;

        List<Integer> array = (request != null && request.input() != null && !request.input().isEmpty())
                ? new ArrayList<>(request.input())
                : List.of(5, 1, 4, 2, 8);

        steps.add(new com.codeloom.dsa.visualization.dto.VisualizationStep(stepNum++, com.codeloom.dsa.visualization.entity.ActionType.INITIAL, List.of(), new ArrayList<>(array), "Initializing execution for " + name + "."));
        steps.add(new com.codeloom.dsa.visualization.dto.VisualizationStep(stepNum++, com.codeloom.dsa.visualization.entity.ActionType.COMPLETE, List.of(), new ArrayList<>(array), name + " execution finished."));

        return new VisualizationResponse(slug, type, steps);
    }
}
