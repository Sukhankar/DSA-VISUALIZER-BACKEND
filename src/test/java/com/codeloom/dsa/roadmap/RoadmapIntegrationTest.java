package com.codeloom.dsa.roadmap;

import com.codeloom.dsa.auth.dto.RegisterRequest;
import com.codeloom.dsa.auth.service.UserService;
import com.codeloom.dsa.roadmap.dto.AssessmentRequestDto;
import com.codeloom.dsa.roadmap.entity.RoadmapTier;
import com.codeloom.dsa.roadmap.repository.UserAssessmentRepository;
import com.codeloom.dsa.roadmap.repository.UserRoadmapProgressRepository;
import com.codeloom.dsa.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RoadmapIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoadmapProgressRepository progressRepository;

    @Autowired
    private UserAssessmentRepository assessmentRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;

    private static final String TEST_USER_EMAIL = "roadmap_user@example.com";
    private static final String TEST_USERNAME = "roadmapuser";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        if (!userRepository.existsByEmail(TEST_USER_EMAIL)) {
            userService.register(new RegisterRequest(TEST_USER_EMAIL, TEST_USERNAME, "Password123!"));
        }
    }

    @Test
    @Order(1)
    @WithMockUser(username = TEST_USERNAME)
    void getRoadmap_ShouldReturnModulesWithProgress() throws Exception {
        mockMvc.perform(get("/api/v1/roadmap"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(8))))
                .andExpect(jsonPath("$[0].slug", is("arrays-basics")))
                .andExpect(jsonPath("$[0].status", is("IN_PROGRESS")));
    }

    @Test
    @Order(2)
    @WithMockUser(username = TEST_USERNAME)
    void getModuleDetails_ShouldReturnModuleWithSteps() throws Exception {
        mockMvc.perform(get("/api/v1/roadmap/modules/arrays-basics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug", is("arrays-basics")))
                .andExpect(jsonPath("$.steps", hasSize(greaterThanOrEqualTo(5))))
                .andExpect(jsonPath("$.steps[0].stepType", is("LEARN")));
    }

    @Test
    @Order(3)
    @WithMockUser(username = TEST_USERNAME)
    void submitAssessment_ShouldAssignRecommendedPath() throws Exception {
        AssessmentRequestDto request = new AssessmentRequestDto();
        request.setExperienceLevel(RoadmapTier.INTERMEDIATE);
        request.setPreferredLanguage("Java");
        request.setKnowsArrays(true);
        request.setKnowsSorting(true);
        request.setGoal("Master LeetCode Mediums");

        mockMvc.perform(post("/api/v1/roadmap/assessment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recommendedModuleSlug", is("sorting-algorithms")))
                .andExpect(jsonPath("$.bonusXpEarned", is(50)));
    }

    @Test
    @Order(4)
    @WithMockUser(username = TEST_USERNAME)
    void getSmartRecommendation_ShouldReturnNextRecommendedStep() throws Exception {
        mockMvc.perform(get("/api/v1/roadmap/recommendations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.moduleSlug", notNullValue()))
                .andExpect(jsonPath("$.actionUrl", notNullValue()));
    }
}
