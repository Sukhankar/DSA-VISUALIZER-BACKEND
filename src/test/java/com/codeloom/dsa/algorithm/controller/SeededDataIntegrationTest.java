package com.codeloom.dsa.algorithm.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class SeededDataIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    @DisplayName("1 & 2. Verify expected 8 categories exist via public API")
    void getCategories_returnsSeededCategories() throws Exception {
        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(8))))
                .andExpect(jsonPath("$[*].slug", hasItems(
                        "sorting",
                        "searching",
                        "arrays",
                        "linked-lists",
                        "trees",
                        "graphs",
                        "dynamic-programming",
                        "greedy"
                )));
    }

    @Test
    @DisplayName("3. Verify expected seeded algorithms exist")
    void getAlgorithms_returnsSeededAlgorithms() throws Exception {
        mockMvc.perform(get("/api/v1/algorithms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(10))));
    }

    @Test
    @DisplayName("4. Verify Bubble Sort belongs to Sorting category")
    void getBubbleSort_belongsToSortingCategory() throws Exception {
        mockMvc.perform(get("/api/v1/algorithms/bubble-sort"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Bubble Sort"))
                .andExpect(jsonPath("$.categorySlug").value("sorting"))
                .andExpect(jsonPath("$.categoryName").value("Sorting"));
    }

    @Test
    @DisplayName("5. Verify Binary Search belongs to Searching category")
    void getBinarySearch_belongsToSearchingCategory() throws Exception {
        mockMvc.perform(get("/api/v1/algorithms/binary-search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Binary Search"))
                .andExpect(jsonPath("$.categorySlug").value("searching"))
                .andExpect(jsonPath("$.categoryName").value("Searching"));
    }

    @Test
    @DisplayName("6. Verify Dijkstra's Algorithm belongs to Graphs category")
    void getDijkstrasAlgorithm_belongsToGraphsCategory() throws Exception {
        mockMvc.perform(get("/api/v1/algorithms/dijkstras-algorithm"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Dijkstras Algorithm"))
                .andExpect(jsonPath("$.categorySlug").value("graphs"))
                .andExpect(jsonPath("$.categoryName").value("Graphs"));
    }

    @Test
    @DisplayName("8. Verify category filtering returns only matching algorithms")
    void getAlgorithms_filteredByCategory_returnsMatchingAlgorithms() throws Exception {
        mockMvc.perform(get("/api/v1/algorithms").param("category", "sorting"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].categorySlug", everyItem(is("sorting"))));
    }

    @Test
    @DisplayName("9. Verify difficulty filtering returns only matching algorithms")
    void getAlgorithms_filteredByDifficulty_returnsMatchingAlgorithms() throws Exception {
        mockMvc.perform(get("/api/v1/algorithms").param("difficulty", "EASY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].difficulty", everyItem(is("EASY"))));
    }

    @Test
    @DisplayName("10. Verify search returns matching algorithms")
    void getAlgorithms_search_returnsMatchingAlgorithms() throws Exception {
        mockMvc.perform(get("/api/v1/algorithms").param("search", "sort"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].name", hasItem(containsStringIgnoringCase("Sort"))));
    }
}
