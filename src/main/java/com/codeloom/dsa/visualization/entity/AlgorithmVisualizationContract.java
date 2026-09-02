package com.codeloom.dsa.visualization.entity;

import com.codeloom.dsa.algorithm.entity.Algorithm;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "algorithm_visualization_contracts")
public class AlgorithmVisualizationContract {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "algorithm_id", nullable = false, unique = true)
    private Algorithm algorithm;

    @Column(name = "visualization_type", nullable = false, length = 64)
    private String visualizationType;

    @Column(name = "data_structure_type", nullable = false, length = 64)
    private String dataStructureType;

    @Column(name = "input_mode", nullable = false, length = 32)
    private String inputMode = "CUSTOMIZABLE";

    @Column(name = "input_schema", columnDefinition = "TEXT")
    private String inputSchema;

    @Column(name = "sample_input", columnDefinition = "TEXT")
    private String sampleInput;

    @Column(name = "generator_key", nullable = false, length = 64)
    private String generatorKey;

    @Column(name = "renderer_key", nullable = false, length = 64)
    private String rendererKey;

    @Column(name = "step_schema", columnDefinition = "TEXT")
    private String stepSchema;

    @Column(name = "visualization_config", columnDefinition = "TEXT")
    private String visualizationConfig;

    @Column(name = "learning_visualization_description", columnDefinition = "TEXT")
    private String learningVisualizationDescription;

    @Column(name = "supports_custom_input", nullable = false)
    private boolean supportsCustomInput = true;

    @Column(name = "max_input_size", nullable = false)
    private int maxInputSize = 100;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected AlgorithmVisualizationContract() {
    }

    public AlgorithmVisualizationContract(
            Algorithm algorithm,
            String visualizationType,
            String dataStructureType,
            String inputMode,
            String inputSchema,
            String sampleInput,
            String generatorKey,
            String rendererKey,
            String stepSchema,
            String visualizationConfig,
            String learningVisualizationDescription,
            boolean supportsCustomInput,
            int maxInputSize
    ) {
        this.algorithm = algorithm;
        this.visualizationType = visualizationType;
        this.dataStructureType = dataStructureType;
        this.inputMode = inputMode;
        this.inputSchema = inputSchema;
        this.sampleInput = sampleInput;
        this.generatorKey = generatorKey;
        this.rendererKey = rendererKey;
        this.stepSchema = stepSchema;
        this.visualizationConfig = visualizationConfig;
        this.learningVisualizationDescription = learningVisualizationDescription;
        this.supportsCustomInput = supportsCustomInput;
        this.maxInputSize = maxInputSize;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public String getVisualizationType() {
        return visualizationType;
    }

    public String getDataStructureType() {
        return dataStructureType;
    }

    public String getInputMode() {
        return inputMode;
    }

    public String getInputSchema() {
        return inputSchema;
    }

    public String getSampleInput() {
        return sampleInput;
    }

    public String getGeneratorKey() {
        return generatorKey;
    }

    public String getRendererKey() {
        return rendererKey;
    }

    public String getStepSchema() {
        return stepSchema;
    }

    public String getVisualizationConfig() {
        return visualizationConfig;
    }

    public String getLearningVisualizationDescription() {
        return learningVisualizationDescription;
    }

    public boolean isSupportsCustomInput() {
        return supportsCustomInput;
    }

    public int getMaxInputSize() {
        return maxInputSize;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}
