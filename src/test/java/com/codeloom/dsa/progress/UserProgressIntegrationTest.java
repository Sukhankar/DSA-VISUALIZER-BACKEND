package com.codeloom.dsa.progress;

import com.codeloom.dsa.auth.dto.RegisterRequest;
import com.codeloom.dsa.auth.service.UserService;
import com.codeloom.dsa.progress.dto.UpdateProgressRequest;
import com.codeloom.dsa.progress.repository.UserAlgorithmProgressRepository;
import com.codeloom.dsa.progress.repository.UserFavoriteRepository;
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
class UserProgressIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserFavoriteRepository favoriteRepository;

    @Autowired
    private UserAlgorithmProgressRepository progressRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;

    private static final String USER1_EMAIL = "progress_user1@example.com";
    private static final String USER2_EMAIL = "progress_user2@example.com";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        favoriteRepository.deleteAll();
        progressRepository.deleteAll();

        // Ensure test users exist
        if (!userRepository.existsByEmail(USER1_EMAIL)) {
            userService.register(new RegisterRequest(USER1_EMAIL, "progressuser1", "Password123!"));
        }
        if (!userRepository.existsByEmail(USER2_EMAIL)) {
            userService.register(new RegisterRequest(USER2_EMAIL, "progressuser2", "Password123!"));
        }
    }

    // --- Authentication Tests ---

    @Test
    @Order(1)
    @DisplayName("1. Anonymous user cannot access favorites endpoint")
    void anonymousUser_accessFavorites_returnsForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/users/me/favorites"))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(2)
    @DisplayName("2. Anonymous user cannot access progress endpoint")
    void anonymousUser_accessProgress_returnsForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/users/me/progress"))
                .andExpect(status().isForbidden());
    }

    // --- Favorites Tests ---

    @Test
    @Order(3)
    @DisplayName("3. Add favorite algorithm successfully")
    @WithMockUser(username = USER1_EMAIL, roles = "USER")
    void user_addFavorite_returnsCreated() throws Exception {
        mockMvc.perform(post("/api/v1/users/me/favorites/bubble-sort"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.slug").value("bubble-sort"))
                .andExpect(jsonPath("$.name").value("Bubble Sort"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    @Order(4)
    @DisplayName("4. Add duplicate favorite returns 409 Conflict")
    @WithMockUser(username = USER1_EMAIL, roles = "USER")
    void user_addDuplicateFavorite_returnsConflict() throws Exception {
        // Precondition: Add favorite
        mockMvc.perform(post("/api/v1/users/me/favorites/bubble-sort"))
                .andExpect(status().isCreated());

        // Duplicate attempt
        mockMvc.perform(post("/api/v1/users/me/favorites/bubble-sort"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Algorithm is already favorited"));
    }

    @Test
    @Order(5)
    @DisplayName("5. Favorite unknown algorithm returns 404 Not Found")
    @WithMockUser(username = USER1_EMAIL, roles = "USER")
    void user_favoriteUnknownAlgorithm_returnsNotFound() throws Exception {
        mockMvc.perform(post("/api/v1/users/me/favorites/unknown-algorithm"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Algorithm not found with slug: unknown-algorithm"));
    }

    @Test
    @Order(6)
    @DisplayName("6. List user favorites returns favorited algorithm")
    @WithMockUser(username = USER1_EMAIL, roles = "USER")
    void user_listFavorites_returnsFavoriteList() throws Exception {
        // Precondition: Add favorite
        mockMvc.perform(post("/api/v1/users/me/favorites/bubble-sort"))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/users/me/favorites"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].slug").value("bubble-sort"));
    }

    @Test
    @Order(7)
    @DisplayName("7. Remove favorite algorithm successfully")
    @WithMockUser(username = USER1_EMAIL, roles = "USER")
    void user_removeFavorite_returnsNoContent() throws Exception {
        // Precondition: Add favorite
        mockMvc.perform(post("/api/v1/users/me/favorites/bubble-sort"))
                .andExpect(status().isCreated());

        // Remove favorite
        mockMvc.perform(delete("/api/v1/users/me/favorites/bubble-sort"))
                .andExpect(status().isNoContent());

        // Verify list is now empty for user
        mockMvc.perform(get("/api/v1/users/me/favorites"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @Order(8)
    @DisplayName("8. Remove non-existent favorite returns 404 Not Found")
    @WithMockUser(username = USER1_EMAIL, roles = "USER")
    void user_removeNonExistentFavorite_returnsNotFound() throws Exception {
        mockMvc.perform(delete("/api/v1/users/me/favorites/bubble-sort"))
                .andExpect(status().isNotFound());
    }

    // --- Progress Tests ---

    @Test
    @Order(9)
    @DisplayName("9. Get progress for unstarted algorithm returns default NOT_STARTED status")
    @WithMockUser(username = USER1_EMAIL, roles = "USER")
    void user_getProgressUnstarted_returnsNotStarted() throws Exception {
        mockMvc.perform(get("/api/v1/users/me/progress/quick-sort"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.algorithmSlug").value("quick-sort"))
                .andExpect(jsonPath("$.status").value("NOT_STARTED"))
                .andExpect(jsonPath("$.progressPercentage").value(0));
    }

    @Test
    @Order(10)
    @DisplayName("10. Start algorithm creates IN_PROGRESS record")
    @WithMockUser(username = USER1_EMAIL, roles = "USER")
    void user_startAlgorithm_returnsInProgress() throws Exception {
        mockMvc.perform(post("/api/v1/users/me/progress/quick-sort/start"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.algorithmSlug").value("quick-sort"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.startedAt").exists());
    }

    @Test
    @Order(11)
    @DisplayName("11. Starting algorithm multiple times is idempotent")
    @WithMockUser(username = USER1_EMAIL, roles = "USER")
    void user_startAlgorithmIdempotent_preservesState() throws Exception {
        mockMvc.perform(post("/api/v1/users/me/progress/quick-sort/start"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/users/me/progress/quick-sort/start"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    @Order(12)
    @DisplayName("12. Update progress percentage and last step successfully")
    @WithMockUser(username = USER1_EMAIL, roles = "USER")
    void user_updateProgress_returnsUpdatedState() throws Exception {
        UpdateProgressRequest request = new UpdateProgressRequest(50, 4);

        mockMvc.perform(put("/api/v1/users/me/progress/quick-sort")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.progressPercentage").value(50))
                .andExpect(jsonPath("$.lastStep").value(4))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    @Order(13)
    @DisplayName("13. Update progress percentage below 0 returns 400 Bad Request")
    @WithMockUser(username = USER1_EMAIL, roles = "USER")
    void user_updateProgressBelowZero_returnsBadRequest() throws Exception {
        UpdateProgressRequest request = new UpdateProgressRequest(-10, 1);

        mockMvc.perform(put("/api/v1/users/me/progress/quick-sort")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(14)
    @DisplayName("14. Update progress percentage above 100 returns 400 Bad Request")
    @WithMockUser(username = USER1_EMAIL, roles = "USER")
    void user_updateProgressAboveHundred_returnsBadRequest() throws Exception {
        UpdateProgressRequest request = new UpdateProgressRequest(150, 1);

        mockMvc.perform(put("/api/v1/users/me/progress/quick-sort")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(15)
    @DisplayName("15. Complete algorithm sets status COMPLETED and percentage 100")
    @WithMockUser(username = USER1_EMAIL, roles = "USER")
    void user_completeAlgorithm_returnsCompleted() throws Exception {
        mockMvc.perform(post("/api/v1/users/me/progress/quick-sort/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.progressPercentage").value(100))
                .andExpect(jsonPath("$.completedAt").exists());
    }

    @Test
    @Order(16)
    @DisplayName("16. Get progress for unknown algorithm returns 404 Not Found")
    @WithMockUser(username = USER1_EMAIL, roles = "USER")
    void user_getProgressUnknownAlgorithm_returnsNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/users/me/progress/unknown-algorithm"))
                .andExpect(status().isNotFound());
    }

    // --- User Data Isolation Tests ---

    @Test
    @Order(17)
    @DisplayName("17. User 2 cannot see User 1's progress or favorites")
    @WithMockUser(username = USER2_EMAIL, roles = "USER")
    void user2_accessUserData_isIsolated() throws Exception {
        // User 2 favorites list should be empty
        mockMvc.perform(get("/api/v1/users/me/favorites"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        // User 2 progress for quick-sort should be NOT_STARTED
        mockMvc.perform(get("/api/v1/users/me/progress/quick-sort"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("NOT_STARTED"))
                .andExpect(jsonPath("$.progressPercentage").value(0));
    }

    // --- Dashboard Tests ---

    @Test
    @Order(18)
    @DisplayName("18. Learning dashboard returns accurate progress summary for User 1")
    @WithMockUser(username = USER1_EMAIL, roles = "USER")
    void user1_getDashboard_returnsCorrectSummary() throws Exception {
        // Setup User 1 activity
        mockMvc.perform(post("/api/v1/users/me/favorites/binary-search"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/users/me/progress/bubble-sort/start"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/users/me/progress/quick-sort/complete"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/users/me/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAlgorithms", greaterThan(0)))
                .andExpect(jsonPath("$.startedAlgorithms", is(2)))
                .andExpect(jsonPath("$.completedAlgorithms", is(1)))
                .andExpect(jsonPath("$.favoriteAlgorithms", is(1)))
                .andExpect(jsonPath("$.completionPercentage", greaterThan(0.0)))
                .andExpect(jsonPath("$.recentProgress", hasSize(2)));
    }
}
