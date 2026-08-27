package com.codeloom.dsa.config;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
class OpenApiIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    @DisplayName("GET /v3/api-docs returns 200 OK with valid OpenAPI specification")
    void getApiDocs_returnsValidOpenApiSpec() throws Exception {
        mockMvc.perform(get("/v3/api-docs")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("DSA Visualizer API"))
                .andExpect(jsonPath("$.info.version").value("v1"))
                .andExpect(jsonPath("$.components.securitySchemes.bearerAuth").exists())
                .andExpect(jsonPath("$.paths['/api/v1/auth/login']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/auth/register']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/categories']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/algorithms']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/algorithms/{slug}']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/algorithms/{slug}/visualize']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/users/me/favorites']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/users/me/progress']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/users/me/dashboard']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/admin/algorithms']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/admin/categories']").exists());
    }
}
