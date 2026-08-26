package com.codeloom.dsa.algorithm.controller;

import com.codeloom.dsa.algorithm.dto.CreateAlgorithmRequest;
import com.codeloom.dsa.algorithm.dto.UpdateAlgorithmRequest;
import com.codeloom.dsa.algorithm.entity.Difficulty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AdminAlgorithmControllerTest {

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
    @Order(1)
    @DisplayName("Phase 1 — Regular USER accessing ADMIN endpoint is blocked with 403 Forbidden")
    @WithMockUser(roles = "USER")
    void regularUser_accessingAdminEndpoint_returnsForbidden() throws Exception {
        CreateAlgorithmRequest request = new CreateAlgorithmRequest(
                "Merge Sort",
                "merge-sort-test",
                "Divide and conquer sorting algorithm",
                Difficulty.MEDIUM,
                "O(n log n)",
                "O(n)",
                "sorting"
        );

        mockMvc.perform(post("/api/v1/admin/algorithms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(2)
    @DisplayName("Phase 1 — Unauthenticated request accessing ADMIN endpoint returns 403 Forbidden")
    void unauthenticatedUser_accessingAdminEndpoint_returnsUnauthorized() throws Exception {
        CreateAlgorithmRequest request = new CreateAlgorithmRequest(
                "Merge Sort",
                "merge-sort-test",
                "Divide and conquer sorting algorithm",
                Difficulty.MEDIUM,
                "O(n log n)",
                "O(n)",
                "sorting"
        );

        mockMvc.perform(post("/api/v1/admin/algorithms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(3)
    @DisplayName("Phase 4 — ADMIN creating algorithm returns 201 Created")
    @WithMockUser(roles = "ADMIN")
    void adminUser_createAlgorithm_returnsCreated() throws Exception {
        CreateAlgorithmRequest request = new CreateAlgorithmRequest(
                "Merge Sort Test",
                "merge-sort-test",
                "Divide and conquer sorting algorithm",
                Difficulty.MEDIUM,
                "O(n log n)",
                "O(n)",
                "sorting"
        );

        mockMvc.perform(post("/api/v1/admin/algorithms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.slug").value("merge-sort-test"))
                .andExpect(jsonPath("$.name").value("Merge Sort Test"))
                .andExpect(jsonPath("$.categorySlug").value("sorting"));
    }

    @Test
    @Order(4)
    @DisplayName("Phase 5 — Duplicate slug creation returns 400 Bad Request")
    @WithMockUser(roles = "ADMIN")
    void adminUser_createDuplicateSlug_returnsBadRequest() throws Exception {
        CreateAlgorithmRequest request = new CreateAlgorithmRequest(
                "Merge Sort Test Copy",
                "merge-sort-test",
                "Duplicate slug test",
                Difficulty.EASY,
                "O(n log n)",
                "O(n)",
                "sorting"
        );

        mockMvc.perform(post("/api/v1/admin/algorithms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Algorithm slug already exists: merge-sort-test"));
    }

    @Test
    @Order(5)
    @DisplayName("Phase 6 & 7 — ADMIN updating algorithm and changing slug returns 200 OK")
    @WithMockUser(roles = "ADMIN")
    void adminUser_updateAlgorithm_returnsOk() throws Exception {
        UpdateAlgorithmRequest updateRequest = new UpdateAlgorithmRequest(
                "Merge Sort Updated",
                "merge-sort-updated",
                "Updated description for merge sort",
                Difficulty.HARD,
                "O(n log n)",
                "O(n)",
                "sorting"
        );

        mockMvc.perform(put("/api/v1/admin/algorithms/merge-sort-test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("merge-sort-updated"))
                .andExpect(jsonPath("$.name").value("Merge Sort Updated"))
                .andExpect(jsonPath("$.difficulty").value("HARD"));
    }

    @Test
    @Order(6)
    @DisplayName("Phase 7 — Public endpoint retrieves algorithm by updated slug")
    void publicUser_getUpdatedAlgorithm_returnsOk() throws Exception {
        mockMvc.perform(get("/api/v1/algorithms/merge-sort-updated"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Merge Sort Updated"));
    }

    @Test
    @Order(7)
    @DisplayName("Phase 7 — Public endpoint requesting old slug returns 404 Not Found")
    void publicUser_getOldSlug_returnsNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/algorithms/merge-sort-test"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(8)
    @DisplayName("Phase 8 — ADMIN deleting algorithm returns 204 No Content")
    @WithMockUser(roles = "ADMIN")
    void adminUser_deleteAlgorithm_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/admin/algorithms/merge-sort-updated"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/algorithms/merge-sort-updated"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(9)
    @DisplayName("Phase 9 — Public endpoints remain accessible without JWT")
    void publicEndpoints_remainAccessibleWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/algorithms"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(10)
    @DisplayName("Phase 10 — Invalid category slug during creation returns 404 Not Found")
    @WithMockUser(roles = "ADMIN")
    void adminUser_createWithNonExistentCategory_returnsNotFound() throws Exception {
        CreateAlgorithmRequest request = new CreateAlgorithmRequest(
                "Binary Search Test",
                "binary-search-test",
                "Description",
                Difficulty.EASY,
                "O(log n)",
                "O(1)",
                "invalid-category-slug"
        );

        mockMvc.perform(post("/api/v1/admin/algorithms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Category not found: invalid-category-slug"));
    }
}
