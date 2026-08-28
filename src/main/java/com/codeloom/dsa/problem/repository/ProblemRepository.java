package com.codeloom.dsa.problem.repository;

import com.codeloom.dsa.algorithm.entity.Difficulty;
import com.codeloom.dsa.problem.entity.Problem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, UUID> {

    Optional<Problem> findBySlug(String slug);

    List<Problem> findByCategoryId(UUID categoryId);

    List<Problem> findByDifficulty(Difficulty difficulty);

    @Query("SELECT p FROM Problem p WHERE " +

           "(:difficulty IS NULL OR p.difficulty = :difficulty) AND " +
           "(:categorySlug IS NULL OR p.category.slug = :categorySlug) AND " +
           "(:search IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Problem> findWithFilters(
            @Param("difficulty") Difficulty difficulty,
            @Param("categorySlug") String categorySlug,
            @Param("search") String search,
            Pageable pageable
    );
}
