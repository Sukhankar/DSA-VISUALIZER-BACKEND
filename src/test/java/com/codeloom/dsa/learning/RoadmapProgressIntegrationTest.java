package com.codeloom.dsa.learning;

import com.codeloom.dsa.learning.dto.OnboardingAssessmentRequest;
import com.codeloom.dsa.learning.entity.ExperienceLevel;
import com.codeloom.dsa.learning.entity.PrimaryGoal;
import com.codeloom.dsa.learning.service.RoadmapProgressService;
import com.codeloom.dsa.roadmap.dto.AssessmentResultDto;
import com.codeloom.dsa.roadmap.dto.UserRoadmapDto;
import com.codeloom.dsa.user.entity.User;
import com.codeloom.dsa.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class RoadmapProgressIntegrationTest {

    @Autowired
    private RoadmapProgressService roadmapProgressService;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testuser_roadmap_" + UUID.randomUUID().toString().substring(0, 8));
        testUser.setEmail("roadmap_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com");
        testUser.setPassword("password123");
        testUser = userRepository.save(testUser);
    }

    @Test
    @DisplayName("Should process onboarding assessment and unlock initial module")
    void testProcessOnboardingAssessment() {
        OnboardingAssessmentRequest req = new OnboardingAssessmentRequest();
        req.setExperienceLevel(ExperienceLevel.BEGINNER);
        req.setPreferredLanguage("Java");
        req.setPrimaryGoal(PrimaryGoal.LEARN_DSA);
        req.setDailyLearningMinutes(30);

        AssessmentResultDto result = roadmapProgressService.processOnboardingAssessment(testUser, req);
        assertThat(result).isNotNull();
        assertThat(result.getRecommendedModuleSlug()).isNotNull();

        UserRoadmapDto roadmap = roadmapProgressService.getUserRoadmap(testUser);
        assertThat(roadmap).isNotNull();
        assertThat(roadmap.getModules()).isNotEmpty();
    }
}
