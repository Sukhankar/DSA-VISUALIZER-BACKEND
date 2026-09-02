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
class Phase18B1PilotIntegrationTest {

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
    @DisplayName("1. Verify two-sum contract returns READY and generates >2 steps with explanations")
    void visualize_twoSum_returnsReadyContractAndRichSteps() throws Exception {
        mockMvc.perform(get("/api/v1/algorithms/two-sum/visualization-contract"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("READY")))
                .andExpect(jsonPath("$.generatorKey", is("two-sum")))
                .andExpect(jsonPath("$.rendererKey", is("array")));

        Map<String, Object> request = Map.of("input", List.of(2, 7, 11, 15), "target", 9);
        mockMvc.perform(post("/api/v1/algorithms/two-sum/visualize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contract.status", is("READY")))
                .andExpect(jsonPath("$.steps", hasSize(greaterThan(2))))
                .andExpect(jsonPath("$.steps[0].whyMessage", notNullValue()))
                .andExpect(jsonPath("$.steps[0].beginnerExplanation", notNullValue()));
    }

    @Test
    @DisplayName("2. Verify binary-search contract returns READY and uses pointer-array renderer")
    void visualize_binarySearch_returnsReadyContractAndPointerArrayRenderer() throws Exception {
        mockMvc.perform(get("/api/v1/algorithms/binary-search/visualization-contract"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("READY")))
                .andExpect(jsonPath("$.generatorKey", is("binary-search")))
                .andExpect(jsonPath("$.rendererKey", is("pointer-array")));

        Map<String, Object> request = Map.of("input", List.of(1, 3, 5, 7, 9, 11, 13), "target", 7);
        mockMvc.perform(post("/api/v1/algorithms/binary-search/visualize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contract.status", is("READY")))
                .andExpect(jsonPath("$.steps", hasSize(greaterThan(2))))
                .andExpect(jsonPath("$.steps[1].whyMessage", containsString("mid")));
    }

    @Test
    @DisplayName("3. Verify linear-search contract returns READY and generates sequential steps")
    void visualize_linearSearch_returnsReadyContractAndSequentialSteps() throws Exception {
        mockMvc.perform(get("/api/v1/algorithms/linear-search/visualization-contract"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("READY")))
                .andExpect(jsonPath("$.rendererKey", is("pointer-array")));

        Map<String, Object> request = Map.of("input", List.of(5, 2, 8, 1, 9), "target", 8);
        mockMvc.perform(post("/api/v1/algorithms/linear-search/visualize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contract.status", is("READY")))
                .andExpect(jsonPath("$.steps", hasSize(greaterThan(2))));
    }

    @Test
    @DisplayName("4. Verify kadanes-algorithm contract returns READY and tracks currentSum/maxSum")
    void visualize_kadanesAlgorithm_returnsReadyContractAndWindowTracking() throws Exception {
        mockMvc.perform(get("/api/v1/algorithms/kadanes-algorithm/visualization-contract"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("READY")))
                .andExpect(jsonPath("$.rendererKey", is("array")));

        Map<String, Object> request = Map.of("input", List.of(-2, 1, -3, 4, -1, 2, 1, -5, 4));
        mockMvc.perform(post("/api/v1/algorithms/kadanes-algorithm/visualize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contract.status", is("READY")))
                .andExpect(jsonPath("$.steps", hasSize(greaterThan(2))));
    }

    @Test
    @DisplayName("5. Verify kmp-algorithm contract returns READY and uses string renderer")
    void visualize_kmpAlgorithm_returnsReadyContractAndStringRenderer() throws Exception {
        mockMvc.perform(get("/api/v1/algorithms/kmp-string-matching-hard/visualization-contract"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("READY")))
                .andExpect(jsonPath("$.rendererKey", is("string")));

        Map<String, Object> request = Map.of("text", "ABABDABACDABABCABAB", "pattern", "ABABCABAB");
        mockMvc.perform(post("/api/v1/algorithms/kmp-string-matching-hard/visualize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contract.status", is("READY")))
                .andExpect(jsonPath("$.steps", hasSize(greaterThan(2))));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("6. Verify audit report readyCount reflects pilot contract migration (readyCount >= 6)")
    void getAuditReport_reflectsPilotMigration() throws Exception {
        mockMvc.perform(get("/api/v1/admin/visualizations/audit"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAlgorithms", is(218)))
                .andExpect(jsonPath("$.readyCount", greaterThanOrEqualTo(6)));
    }
}
