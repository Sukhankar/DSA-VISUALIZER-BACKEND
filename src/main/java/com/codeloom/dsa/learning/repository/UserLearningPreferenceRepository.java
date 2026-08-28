package com.codeloom.dsa.learning.repository;

import com.codeloom.dsa.learning.entity.UserLearningPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserLearningPreferenceRepository extends JpaRepository<UserLearningPreference, UUID> {
    Optional<UserLearningPreference> findByUserId(UUID userId);
}
