package com.codeloom.dsa.visualization;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
class VisualizationIntegrationTest {

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
    @DisplayName("1. Verify Bubble Sort visualization generates complete step sequence")
    void visualizeBubbleSort_returnsFullStepSequence() throws Exception {
        Map<String, Object> request = Map.of("input", List.of(5, 1, 4, 2));

        mockMvc.perform(post("/api/v1/algorithms/bubble-sort/visualize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.algorithm", is("bubble-sort")))
                .andExpect(jsonPath("$.visualizationType", is("ARRAY")))
                .andExpect(jsonPath("$.steps", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.steps[0].action", is("INITIAL")))
                .andExpect(jsonPath("$.steps[0].array", is(List.of(5, 1, 4, 2))))
                .andExpect(jsonPath("$.steps[1].action", is("COMPARE")))
                .andExpect(jsonPath("$.steps[1].indices", is(List.of(0, 1))))
                .andExpect(jsonPath("$.steps[2].action", is("SWAP")))
                .andExpect(jsonPath("$.steps[2].array", is(List.of(1, 5, 4, 2))))
                .andExpect(jsonPath("$.steps[-1].action", is("COMPLETE")))
                .andExpect(jsonPath("$.steps[-1].array", is(List.of(1, 2, 4, 5))));
    }

    @Test
    @DisplayName("2. Verify empty input returns 400 Bad Request")
    void visualizeEmptyInput_returnsBadRequest() throws Exception {
        Map<String, Object> request = Map.of("input", List.of());

        mockMvc.perform(post("/api/v1/algorithms/bubble-sort/visualize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("3. Verify input exceeding max size returns 400 Bad Request")
    void visualizeExceedingMaxInputSize_returnsBadRequest() throws Exception {
        List<Integer> largeInput = new ArrayList<>();
        for (int i = 0; i < 51; i++) {
            largeInput.add(i);
        }
        Map<String, Object> request = Map.of("input", largeInput);

        mockMvc.perform(post("/api/v1/algorithms/bubble-sort/visualize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("4. Verify non-existent algorithm slug returns 404 Not Found")
    void visualizeNonExistentAlgorithm_returnsNotFound() throws Exception {
        Map<String, Object> request = Map.of("input", List.of(3, 2, 1));

        mockMvc.perform(post("/api/v1/algorithms/unknown-algorithm/visualize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("5. Verify unsupported algorithm returns empty steps")
    void visualizeUnsupportedAlgorithm_returnsEmptySteps() throws Exception {
        Map<String, Object> request = Map.of("input", List.of(1, 2, 3));

        mockMvc.perform(post("/api/v1/algorithms/binary-search/visualize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.algorithm", is("binary-search")))
                .andExpect(jsonPath("$.visualizationType", is("ARRAY")))
                .andExpect(jsonPath("$.steps", hasSize(0)));
    }
}
