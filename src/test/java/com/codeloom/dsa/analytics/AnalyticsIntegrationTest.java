package com.codeloom.dsa.analytics;

import com.codeloom.dsa.user.entity.Role;
import com.codeloom.dsa.user.entity.User;
import com.codeloom.dsa.user.repository.RoleRepository;
import com.codeloom.dsa.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class AnalyticsIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        String email = "analyticsuser@codeloom.com";
        if (userRepository.findByEmail(email).isEmpty()) {
            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));
            User user = new User(email, "analyticsuser", "encodedpassword");
            user.addRole(userRole);
            userRepository.save(user);
        }
    }

    @Test
    @WithMockUser(username = "analyticsuser@codeloom.com", roles = {"USER"})
    @DisplayName("Get analytics overview - returns streak, level, XP, and badges")
    void getOverview_ReturnsAnalyticsProfile() throws Exception {
        mockMvc.perform(get("/api/v1/analytics/overview"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userStreak").exists())
                .andExpect(jsonPath("$.userXp").exists())
                .andExpect(jsonPath("$.topicSkills").isArray());
    }

    @Test
    @WithMockUser(username = "analyticsuser@codeloom.com", roles = {"USER"})
    @DisplayName("Get activity heatmap - returns daily contribution data")
    void getActivityHeatmap_ReturnsData() throws Exception {
        mockMvc.perform(get("/api/v1/analytics/heatmap"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(username = "analyticsuser@codeloom.com", roles = {"USER"})
    @DisplayName("Get badges - returns seeded platform achievement badges")
    void getBadges_ReturnsSeededBadges() throws Exception {
        mockMvc.perform(get("/api/v1/analytics/badges"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Get public leaderboard - returns top ranked users")
    void getLeaderboard_ReturnsLeaderboard() throws Exception {
        mockMvc.perform(get("/api/v1/analytics/leaderboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
