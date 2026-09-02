package com.codeloom.dsa.visualization;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
class VisualizationContractIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    @DisplayName("1. Verify pilot algorithm (bubble-sort) contract returns READY status")
    void getVisualizationContract_bubbleSort_returnsReadyContract() throws Exception {
        mockMvc.perform(get("/api/v1/algorithms/bubble-sort/visualization-contract"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.algorithmSlug", is("bubble-sort")))
                .andExpect(jsonPath("$.status", is("READY")))
                .andExpect(jsonPath("$.generatorKey", is("bubble-sort")))
                .andExpect(jsonPath("$.rendererKey", is("array")));
    }

    @Test
    @DisplayName("2. Verify non-migrated algorithm contract returns MISSING_CONTRACT status")
    void getVisualizationContract_nonMigrated_returnsMissingContract() throws Exception {
        mockMvc.perform(get("/api/v1/algorithms/heap-sort-med/visualization-contract"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.algorithmSlug", is("heap-sort-med")))
                .andExpect(jsonPath("$.status", is("MISSING_CONTRACT")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("3. Verify live visualization audit endpoint returns aggregated summary")
    void getAuditReport_returnsLiveSummary() throws Exception {
        mockMvc.perform(get("/api/v1/admin/visualizations/audit"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAlgorithms", greaterThan(0)))
                .andExpect(jsonPath("$.readyCount", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.items", hasSize(greaterThan(0))));
    }

    @Test
    @DisplayName("4. Verify bubble-sort dual-path resolution attaches contract telemetry")
    void visualize_bubbleSort_attachesContractTelemetry() throws Exception {
        Map<String, Object> request = Map.of("input", List.of(5, 1, 4, 2, 8));

        mockMvc.perform(post("/api/v1/algorithms/bubble-sort/visualize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.algorithm", is("bubble-sort")))
                .andExpect(jsonPath("$.contract.status", is("READY")))
                .andExpect(jsonPath("$.contract.rendererKey", is("array")))
                .andExpect(jsonPath("$.steps", hasSize(greaterThan(0))));
    }
}
