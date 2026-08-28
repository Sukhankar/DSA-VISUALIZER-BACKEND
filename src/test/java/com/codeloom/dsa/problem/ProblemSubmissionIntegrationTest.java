package com.codeloom.dsa.problem;

import com.codeloom.dsa.problem.dto.RunCodeRequest;
import com.codeloom.dsa.problem.dto.SubmitCodeRequest;
import com.codeloom.dsa.problem.entity.SubmissionStatus;
import com.codeloom.dsa.problem.entity.SubmissionVerdict;
import com.codeloom.dsa.user.entity.Role;
import com.codeloom.dsa.user.entity.User;
import com.codeloom.dsa.user.repository.RoleRepository;
import com.codeloom.dsa.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ProblemSubmissionIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        String email = "subuser@codeloom.com";
        if (userRepository.findByEmail(email).isEmpty()) {
            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));
            User user = new User(email, "subuser", "encodedpassword");
            user.addRole(userRole);
            userRepository.save(user);
        }
    }

    @Test
    @DisplayName("Run sample code for Two Sum problem - returns sample test results")
    void runSampleCode_TwoSum_ReturnsResults() throws Exception {
        RunCodeRequest request = new RunCodeRequest(
                "JAVA",
                "public class Solution { public int[] solve(int[] nums, int target) { return new int[]{0,1}; } }"
        );

        mockMvc.perform(post("/api/v1/problems/two-sum/run")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.passed").value(true))
                .andExpect(jsonPath("$.verdict").value("ACCEPTED"))
                .andExpect(jsonPath("$.totalTests").value(3))
                .andExpect(jsonPath("$.passedTests").value(3));
    }

    @Test
    @WithMockUser(username = "subuser@codeloom.com", roles = {"USER"})
    @DisplayName("Submit solution for Two Sum problem - records submission verdict")
    void submitSolution_TwoSum_RecordsSubmission() throws Exception {
        SubmitCodeRequest request = new SubmitCodeRequest(
                "JAVA",
                "public class Solution { public int[] solve(int[] nums, int target) { return new int[]{0,1}; } }"
        );

        mockMvc.perform(post("/api/v1/problems/two-sum/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.problemSlug").value("two-sum"))
                .andExpect(jsonPath("$.language").value("JAVA"))
                .andExpect(jsonPath("$.status").value(SubmissionStatus.COMPLETED.name()))
                .andExpect(jsonPath("$.verdict").value(SubmissionVerdict.ACCEPTED.name()))
                .andExpect(jsonPath("$.totalTests").value(5))
                .andExpect(jsonPath("$.passedTests").value(5));
    }

    @Test
    @WithMockUser(username = "subuser@codeloom.com", roles = {"USER"})
    @DisplayName("Get user problem stats - returns statistics breakdown")
    void getUserProblemStats_ReturnsMetrics() throws Exception {
        mockMvc.perform(get("/api/v1/users/me/problem-stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalSolved").exists())
                .andExpect(jsonPath("$.acceptanceRate").exists());
    }
}
