package com.codeloom.dsa.learning;

import com.codeloom.dsa.learning.dto.LearningRecommendationResponse;
import com.codeloom.dsa.learning.service.LearningRecommendationService;
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
class LearningRecommendationIntegrationTest {

    @Autowired
    private LearningRecommendationService recommendationService;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testuser_rec_" + UUID.randomUUID().toString().substring(0, 8));
        testUser.setEmail("rec_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com");
        testUser.setPassword("password123");
        testUser = userRepository.save(testUser);
    }

    @Test
    @DisplayName("Should generate smart next recommendation for user")
    void testGetNextRecommendation() {
        LearningRecommendationResponse rec = recommendationService.getNextRecommendation(testUser);
        assertThat(rec).isNotNull();
        assertThat(rec.getTitle()).isNotNull();
        assertThat(rec.getActionLabel()).isNotNull();
        assertThat(rec.getActionUrl()).isNotNull();
    }
}
