package com.codeloom.dsa.visualization.generator;

import com.codeloom.dsa.visualization.dto.VisualizationRequest;
import com.codeloom.dsa.visualization.dto.VisualizationResponse;

public interface VisualizationGenerator {

    boolean supports(String algorithmSlug);

    VisualizationResponse generate(String algorithmSlug, VisualizationRequest request);
}
