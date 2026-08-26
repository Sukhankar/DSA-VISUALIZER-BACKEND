package com.codeloom.dsa.algorithm.controller;

import com.codeloom.dsa.algorithm.dto.CreateAlgorithmCategoryRequest;
import com.codeloom.dsa.algorithm.dto.UpdateAlgorithmCategoryRequest;
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
class AdminAlgorithmCategoryControllerTest {

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
    @DisplayName("1. Anonymous user cannot access admin category APIs")
    void anonymousUser_accessingAdminCategoryApi_returnsForbidden() throws Exception {
        CreateAlgorithmCategoryRequest request = new CreateAlgorithmCategoryRequest(
                "Dynamic Programming",
                "dynamic-programming",
                "DP Algorithms"
        );

        mockMvc.perform(post("/api/v1/admin/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(2)
    @DisplayName("2. ROLE_USER receives forbidden response when creating category")
    @WithMockUser(roles = "USER")
    void roleUser_createCategory_returnsForbidden() throws Exception {
        CreateAlgorithmCategoryRequest request = new CreateAlgorithmCategoryRequest(
                "Dynamic Programming",
                "dynamic-programming",
                "DP Algorithms"
        );

        mockMvc.perform(post("/api/v1/admin/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(3)
    @DisplayName("3. ROLE_USER receives forbidden response when updating category")
    @WithMockUser(roles = "USER")
    void roleUser_updateCategory_returnsForbidden() throws Exception {
        UpdateAlgorithmCategoryRequest request = new UpdateAlgorithmCategoryRequest(
                "Sorting Updated",
                "sorting",
                "Description"
        );

        mockMvc.perform(put("/api/v1/admin/categories/sorting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(4)
    @DisplayName("4. ROLE_USER receives forbidden response when deleting category")
    @WithMockUser(roles = "USER")
    void roleUser_deleteCategory_returnsForbidden() throws Exception {
        mockMvc.perform(delete("/api/v1/admin/categories/sorting"))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(5)
    @DisplayName("5. ROLE_ADMIN can create a category")
    @WithMockUser(roles = "ADMIN")
    void roleAdmin_createCategory_returnsCreated() throws Exception {
        CreateAlgorithmCategoryRequest request = new CreateAlgorithmCategoryRequest(
                "Dynamic Programming Test",
                "dynamic-programming-test",
                "Algorithms based on dynamic programming"
        );

        mockMvc.perform(post("/api/v1/admin/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Dynamic Programming Test"))
                .andExpect(jsonPath("$.slug").value("dynamic-programming-test"));
    }

    @Test
    @Order(6)
    @DisplayName("6. Duplicate category name is rejected")
    @WithMockUser(roles = "ADMIN")
    void roleAdmin_createDuplicateCategoryName_returnsBadRequest() throws Exception {
        CreateAlgorithmCategoryRequest request = new CreateAlgorithmCategoryRequest(
                "Dynamic Programming Test",
                "dp-unique-slug",
                "Description"
        );

        mockMvc.perform(post("/api/v1/admin/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Category name already exists: Dynamic Programming Test"));
    }

    @Test
    @Order(7)
    @DisplayName("7. Duplicate category slug is rejected")
    @WithMockUser(roles = "ADMIN")
    void roleAdmin_createDuplicateCategorySlug_returnsBadRequest() throws Exception {
        CreateAlgorithmCategoryRequest request = new CreateAlgorithmCategoryRequest(
                "DP Unique Name",
                "dynamic-programming-test",
                "Description"
        );

        mockMvc.perform(post("/api/v1/admin/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Category slug already exists: dynamic-programming-test"));
    }

    @Test
    @Order(8)
    @DisplayName("8. ROLE_ADMIN can update a category")
    @WithMockUser(roles = "ADMIN")
    void roleAdmin_updateCategory_returnsOk() throws Exception {
        UpdateAlgorithmCategoryRequest request = new UpdateAlgorithmCategoryRequest(
                "Dynamic Programming Updated",
                "dynamic-programming-test",
                "Updated description"
        );

        mockMvc.perform(put("/api/v1/admin/categories/dynamic-programming-test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Dynamic Programming Updated"));
    }

    @Test
    @Order(9)
    @DisplayName("9. Updating a category slug works correctly")
    @WithMockUser(roles = "ADMIN")
    void roleAdmin_updateCategorySlug_worksCorrectly() throws Exception {
        UpdateAlgorithmCategoryRequest request = new UpdateAlgorithmCategoryRequest(
                "Dynamic Programming Updated",
                "dynamic-programming-renamed",
                "Updated description"
        );

        mockMvc.perform(put("/api/v1/admin/categories/dynamic-programming-test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("dynamic-programming-renamed"));
    }

    @Test
    @Order(10)
    @DisplayName("10. Missing category returns 404")
    @WithMockUser(roles = "ADMIN")
    void roleAdmin_updateMissingCategory_returnsNotFound() throws Exception {
        UpdateAlgorithmCategoryRequest request = new UpdateAlgorithmCategoryRequest(
                "Non Existent",
                "non-existent",
                "Description"
        );

        mockMvc.perform(put("/api/v1/admin/categories/non-existent-category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Category not found: non-existent-category"));
    }

    @Test
    @Order(11)
    @DisplayName("11. Category containing algorithms cannot be deleted")
    @WithMockUser(roles = "ADMIN")
    void roleAdmin_deleteCategoryWithAlgorithms_returnsBadRequest() throws Exception {
        mockMvc.perform(delete("/api/v1/admin/categories/sorting"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Cannot delete category because it contains algorithms"));
    }

    @Test
    @Order(12)
    @DisplayName("12. Empty category can be deleted successfully")
    @WithMockUser(roles = "ADMIN")
    void roleAdmin_deleteEmptyCategory_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/admin/categories/dynamic-programming-renamed"))
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(13)
    @DisplayName("13. Existing public category endpoint remains accessible")
    void publicUser_getCategories_returnsOk() throws Exception {
        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk());
    }
}
