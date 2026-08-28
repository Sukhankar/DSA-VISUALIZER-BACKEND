package com.codeloom.dsa.learning;

import com.codeloom.dsa.learning.dto.LearningPathResponse;
import com.codeloom.dsa.learning.service.LearningPathService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class LearningPathIntegrationTest {

    @Autowired
    private LearningPathService learningPathService;

    @Test
    @DisplayName("Should fetch all active learning paths")
    void testGetAllActivePaths() {
        List<LearningPathResponse> paths = learningPathService.getAllActivePaths();
        assertThat(paths).isNotNull();
        assertThat(paths).isNotEmpty();
        assertThat(paths.get(0).getSlug()).isNotNull();
    }

    @Test
    @DisplayName("Should fetch learning path by slug with modules")
    void testGetPathBySlug() {
        LearningPathResponse path = learningPathService.getPathBySlug("dsa-beginner");
        assertThat(path).isNotNull();
        assertThat(path.getName()).isEqualTo("DSA Beginner Path");
        assertThat(path.getModules()).isNotNull();
    }
}
