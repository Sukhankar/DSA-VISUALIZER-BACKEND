package com.codeloom.dsa.profile;

import com.codeloom.dsa.profile.dto.UserProfileUpdateRequest;
import com.codeloom.dsa.user.entity.Role;
import com.codeloom.dsa.user.entity.User;
import com.codeloom.dsa.user.repository.RoleRepository;
import com.codeloom.dsa.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserProfileIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private MockMvc mockMvc;
    private User testUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        String email = "profiletest@codeloom.com";
        if (userRepository.findByEmail(email).isEmpty()) {
            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));

            testUser = new User(email, "profiletest", "password123");
            testUser.addRole(userRole);
            userRepository.save(testUser);
        } else {
            testUser = userRepository.findByEmail(email).get();
        }
    }

    @Test
    @WithMockUser(username = "profiletest@codeloom.com", roles = {"USER"})
    void getProfile_returnsDefaultProfile() throws Exception {
        mockMvc.perform(get("/api/v1/users/me/profile")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("profiletest")))
                .andExpect(jsonPath("$.currentLevel", is(1)))
                .andExpect(jsonPath("$.totalXp", is(0)))
                .andExpect(jsonPath("$.levelProgress", notNullValue()))
                .andExpect(jsonPath("$.streakStatus", notNullValue()));
    }

    @Test
    @WithMockUser(username = "profiletest@codeloom.com", roles = {"USER"})
    void updateProfile_updatesDisplayNameAndBio() throws Exception {
        UserProfileUpdateRequest request = new UserProfileUpdateRequest(
                "Alex Coder",
                "Full-stack algorithm enthusiast.",
                "https://example.com/avatar.png",
                "Canada",
                "https://github.com/alexcoder",
                "https://linkedin.com/in/alexcoder"
        );

        mockMvc.perform(put("/api/v1/users/me/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.displayName", is("Alex Coder")))
                .andExpect(jsonPath("$.bio", is("Full-stack algorithm enthusiast.")))
                .andExpect(jsonPath("$.country", is("Canada")))
                .andExpect(jsonPath("$.githubUrl", is("https://github.com/alexcoder")));
    }

    @Test
    @WithMockUser(username = "profiletest@codeloom.com", roles = {"USER"})
    void getAchievementsAndBadges_returnsSeededLists() throws Exception {
        mockMvc.perform(get("/api/v1/users/me/achievements")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))));

        mockMvc.perform(get("/api/v1/users/me/badges")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))));
    }

    @Test
    @WithMockUser(username = "profiletest@codeloom.com", roles = {"USER"})
    void getGamificationSummary_returnsCompleteSummary() throws Exception {
        mockMvc.perform(get("/api/v1/users/me/gamification")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.level", is(1)))
                .andExpect(jsonPath("$.totalAchievements", greaterThan(0)))
                .andExpect(jsonPath("$.totalBadges", greaterThan(0)));
    }
}
