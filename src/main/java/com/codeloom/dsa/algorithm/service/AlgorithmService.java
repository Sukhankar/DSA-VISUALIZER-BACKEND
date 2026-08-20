package com.codeloom.dsa.algorithm.service;

import com.codeloom.dsa.algorithm.dto.AlgorithmCategoryResponse;
import com.codeloom.dsa.algorithm.dto.AlgorithmPageResponse;
import com.codeloom.dsa.algorithm.dto.AlgorithmResponse;
import com.codeloom.dsa.algorithm.entity.Algorithm;
import com.codeloom.dsa.algorithm.entity.AlgorithmCategory;
import com.codeloom.dsa.algorithm.entity.Difficulty;
import com.codeloom.dsa.algorithm.repository.AlgorithmCategoryRepository;
import com.codeloom.dsa.algorithm.repository.AlgorithmRepository;
import com.codeloom.dsa.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AlgorithmService {

    private final AlgorithmRepository algorithmRepository;
    private final AlgorithmCategoryRepository categoryRepository;

    public AlgorithmService(
            AlgorithmRepository algorithmRepository,
            AlgorithmCategoryRepository categoryRepository
    ) {
        this.algorithmRepository = algorithmRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<AlgorithmCategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(this::mapCategory)
                .toList();
    }

    public AlgorithmPageResponse getAlgorithms(
            String category,
            Difficulty difficulty,
            String search,
            Pageable pageable
    ) {

        Page<Algorithm> algorithms;

        if (search != null && !search.isBlank()) {

            algorithms = algorithmRepository
                    .findByNameContainingIgnoreCase(
                            search,
                            pageable
                    );

        } else if (category != null && !category.isBlank()) {

            algorithms = algorithmRepository
                    .findByCategorySlug(
                            category,
                            pageable
                    );

        } else if (difficulty != null) {

            algorithms = algorithmRepository
                    .findByDifficulty(
                            difficulty,
                            pageable
                    );

        } else {

            algorithms = algorithmRepository.findAll(pageable);
        }

        return new AlgorithmPageResponse(
                algorithms.getContent()
                        .stream()
                        .map(this::mapAlgorithm)
                        .toList(),
                algorithms.getNumber(),
                algorithms.getSize(),
                algorithms.getTotalElements(),
                algorithms.getTotalPages(),
                algorithms.isFirst(),
                algorithms.isLast()
        );
    }

    public AlgorithmResponse getAlgorithmBySlug(String slug) {

        Algorithm algorithm = algorithmRepository
                .findBySlug(slug)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Algorithm not found: " + slug
                        )
                );

        return mapAlgorithm(algorithm);
    }

    private AlgorithmCategoryResponse mapCategory(
            AlgorithmCategory category
    ) {
        return new AlgorithmCategoryResponse(
                category.getId(),
                category.getName(),
                category.getSlug(),
                category.getDescription()
        );
    }

    private AlgorithmResponse mapAlgorithm(
            Algorithm algorithm
    ) {
        return new AlgorithmResponse(
                algorithm.getId(),
                algorithm.getName(),
                algorithm.getSlug(),
                algorithm.getDescription(),
                algorithm.getDifficulty(),
                algorithm.getTimeComplexity(),
                algorithm.getSpaceComplexity(),
                algorithm.getCategory().getName(),
                algorithm.getCategory().getSlug()
        );
    }
}