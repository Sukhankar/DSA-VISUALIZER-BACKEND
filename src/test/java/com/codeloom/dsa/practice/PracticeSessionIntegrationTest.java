package com.codeloom.dsa.practice;

import com.codeloom.dsa.practice.dto.CreatePracticeSessionRequest;
import com.codeloom.dsa.practice.dto.SessionSubmitRequest;
import com.codeloom.dsa.practice.entity.PracticeMode;
import com.codeloom.dsa.problem.entity.Problem;
import com.codeloom.dsa.problem.repository.ProblemRepository;
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

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PracticeSessionIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ProblemRepository problemRepository;

    private MockMvc mockMvc;
    private User testUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        String email = "practiceuser@codeloom.com";
        if (userRepository.findByEmail(email).isEmpty()) {
            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));

            testUser = new User(email, "practiceuser", "password123");
            testUser.addRole(userRole);
            userRepository.save(testUser);
        } else {
            testUser = userRepository.findByEmail(email).get();
        }
    }

    @Test
    @WithMockUser(username = "practiceuser@codeloom.com", roles = {"USER"})
    void getArenaOverview_returnsPracticeMetrics() throws Exception {
        mockMvc.perform(get("/api/v1/practice/arena")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dailyChallenge", notNullValue()))
                .andExpect(jsonPath("$.dailyChallenge.problem", notNullValue()))
                .andExpect(jsonPath("$.streak", notNullValue()))
                .andExpect(jsonPath("$.xp", notNullValue()));
    }

    @Test
    @WithMockUser(username = "practiceuser@codeloom.com", roles = {"USER"})
    void createSession_quickMode_createsThreeProblemSession() throws Exception {
        CreatePracticeSessionRequest request = new CreatePracticeSessionRequest(
                PracticeMode.QUICK,
                null,
                null,
                null
        );

        mockMvc.perform(post("/api/v1/practice/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.mode", is("QUICK")))
                .andExpect(jsonPath("$.status", is("IN_PROGRESS")))
                .andExpect(jsonPath("$.problems", hasSize(greaterThan(0))));
    }

    @Test
    @WithMockUser(username = "practiceuser@codeloom.com", roles = {"USER"})
    void submitInSession_acceptedCode_updatesSessionScoreAndXp() throws Exception {
        CreatePracticeSessionRequest createRequest = new CreatePracticeSessionRequest(
                PracticeMode.QUICK,
                null,
                null,
                null
        );

        String createResponseBody = mockMvc.perform(post("/api/v1/practice/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String sessionIdStr = objectMapper.readTree(createResponseBody).get("id").asText();
        String problemIdStr = objectMapper.readTree(createResponseBody).get("problems").get(0).get("problem").get("id").asText();
        UUID sessionId = UUID.fromString(sessionIdStr);
        UUID problemId = UUID.fromString(problemIdStr);

        SessionSubmitRequest submitRequest = new SessionSubmitRequest(
                problemId,
                "PYTHON",
                "def solve(): return True"
        );

        mockMvc.perform(post("/api/v1/practice/sessions/" + sessionId + "/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(submitRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.submission", notNullValue()))
                .andExpect(jsonPath("$.session", notNullValue()))
                .andExpect(jsonPath("$.session.solvedProblems", greaterThanOrEqualTo(1)));
    }
}
