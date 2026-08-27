package com.codeloom.dsa.visualization;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

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

    @ParameterizedTest
    @ValueSource(strings = {"bubble-sort", "selection-sort", "insertion-sort", "merge-sort", "quick-sort"})
    @DisplayName("1. Verify Sorting Generators produce complete step sequence")
    void visualizeSortingGenerators_returnsSteps(String algorithmSlug) throws Exception {
        Map<String, Object> request = Map.of("input", List.of(5, 1, 4, 2, 8));

        mockMvc.perform(post("/api/v1/algorithms/" + algorithmSlug + "/visualize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.algorithm", is(algorithmSlug)))
                .andExpect(jsonPath("$.visualizationType", is("ARRAY")))
                .andExpect(jsonPath("$.steps", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.steps[0].action", is("INITIAL")))
                .andExpect(jsonPath("$.steps[-1].action", is("COMPLETE")))
                .andExpect(jsonPath("$.steps[-1].array", is(List.of(1, 2, 4, 5, 8))));
    }

    @Test
    @DisplayName("2. Verify Linear Search visualization generates complete step sequence")
    void visualizeLinearSearch_returnsSteps() throws Exception {
        Map<String, Object> request = Map.of("input", List.of(10, 20, 30, 40), "target", 30);

        mockMvc.perform(post("/api/v1/algorithms/linear-search/visualize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.algorithm", is("linear-search")))
                .andExpect(jsonPath("$.steps", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.steps[0].action", is("INITIAL")))
                .andExpect(jsonPath("$.steps[-1].action", is("COMPLETE")));
    }

    @Test
    @DisplayName("3. Verify Binary Search visualization generates complete step sequence")
    void visualizeBinarySearch_returnsSteps() throws Exception {
        Map<String, Object> request = Map.of("input", List.of(10, 20, 30, 40, 50), "target", 40);

        mockMvc.perform(post("/api/v1/algorithms/binary-search/visualize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.algorithm", is("binary-search")))
                .andExpect(jsonPath("$.steps", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.steps[0].action", is("INITIAL")))
                .andExpect(jsonPath("$.steps[-1].action", is("COMPLETE")));
    }

    @Test
    @DisplayName("4. Verify Binary Search rejects unsorted input")
    void visualizeBinarySearch_unsortedInput_returnsBadRequest() throws Exception {
        Map<String, Object> request = Map.of("input", List.of(5, 1, 4, 2), "target", 4);

        mockMvc.perform(post("/api/v1/algorithms/binary-search/visualize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("5. Verify Linear Search missing target returns 400 Bad Request")
    void visualizeLinearSearch_missingTarget_returnsBadRequest() throws Exception {
        Map<String, Object> request = Map.of("input", List.of(1, 2, 3));

        mockMvc.perform(post("/api/v1/algorithms/linear-search/visualize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("6. Verify BFS with Graph DTO produces graph visualization steps")
    void visualizeGraphBfs_withGraphDto_returnsSteps() throws Exception {
        Map<String, Object> graphDto = Map.of(
                "nodes", List.of("A", "B", "C", "D"),
                "edges", List.of(
                        Map.of("from", "A", "to", "B"),
                        Map.of("from", "A", "to", "C"),
                        Map.of("from", "B", "to", "D")
                ),
                "startNode", "A"
        );
        Map<String, Object> request = Map.of("graph", graphDto);

        mockMvc.perform(post("/api/v1/algorithms/breadth-first-search/visualize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.algorithm", is("breadth-first-search")))
                .andExpect(jsonPath("$.visualizationType", is("GRAPH")))
                .andExpect(jsonPath("$.steps", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.steps[0].action", is("INITIAL")))
                .andExpect(jsonPath("$.steps[-1].action", is("COMPLETE")));
    }

    @Test
    @DisplayName("7. Verify DFS with Graph DTO produces graph visualization steps")
    void visualizeGraphDfs_withGraphDto_returnsSteps() throws Exception {
        Map<String, Object> graphDto = Map.of(
                "nodes", List.of("A", "B", "C", "D"),
                "edges", List.of(
                        Map.of("from", "A", "to", "B"),
                        Map.of("from", "A", "to", "C"),
                        Map.of("from", "B", "to", "D")
                ),
                "startNode", "A"
        );
        Map<String, Object> request = Map.of("graph", graphDto);

        mockMvc.perform(post("/api/v1/algorithms/depth-first-search/visualize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.algorithm", is("depth-first-search")))
                .andExpect(jsonPath("$.visualizationType", is("GRAPH")))
                .andExpect(jsonPath("$.steps", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.steps[0].action", is("INITIAL")))
                .andExpect(jsonPath("$.steps[-1].action", is("COMPLETE")));
    }

    @Test
    @DisplayName("8. Verify Graph BFS with invalid start node returns 400 Bad Request")
    void visualizeGraphBfs_invalidStartNode_returnsBadRequest() throws Exception {
        Map<String, Object> graphDto = Map.of(
                "nodes", List.of("A", "B", "C"),
                "edges", List.of(),
                "startNode", "Z"
        );
        Map<String, Object> request = Map.of("graph", graphDto);

        mockMvc.perform(post("/api/v1/algorithms/breadth-first-search/visualize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("9. Verify empty input returns 400 Bad Request")
    void visualizeEmptyInput_returnsBadRequest() throws Exception {
        Map<String, Object> request = Map.of("input", List.of());

        mockMvc.perform(post("/api/v1/algorithms/bubble-sort/visualize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
