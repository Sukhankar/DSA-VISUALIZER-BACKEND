package com.codeloom.dsa.algorithm.service;

import com.codeloom.dsa.algorithm.dto.AlgorithmCategoryResponse;
import com.codeloom.dsa.algorithm.dto.AlgorithmPageResponse;
import com.codeloom.dsa.algorithm.dto.AlgorithmResponse;
import com.codeloom.dsa.algorithm.dto.CreateAlgorithmCategoryRequest;
import com.codeloom.dsa.algorithm.dto.CreateAlgorithmRequest;
import com.codeloom.dsa.algorithm.dto.UpdateAlgorithmCategoryRequest;
import com.codeloom.dsa.algorithm.dto.UpdateAlgorithmRequest;
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

    public List<AlgorithmResponse> getAllAlgorithms() {

        return algorithmRepository.findAll()
                .stream()
                .map(this::mapAlgorithm)
                .toList();
    }

    public AlgorithmPageResponse getAlgorithms(
            String categorySlug,
            Difficulty difficulty,
            String search,
            Pageable pageable
    ) {
        Page<Algorithm> page;

        if (categorySlug != null && !categorySlug.isBlank()) {
            page = algorithmRepository.findByCategorySlug(categorySlug, pageable);
        } else if (difficulty != null) {
            page = algorithmRepository.findByDifficulty(difficulty, pageable);
        } else if (search != null && !search.isBlank()) {
            page = algorithmRepository.findByNameContainingIgnoreCase(search, pageable);
        } else {
            page = algorithmRepository.findAll(pageable);
        }

        List<AlgorithmResponse> content = page.getContent()
                .stream()
                .map(this::mapAlgorithm)
                .toList();

        return new AlgorithmPageResponse(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
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

    @Transactional
    public AlgorithmResponse createAlgorithm(
            CreateAlgorithmRequest request
    ) {

        if (algorithmRepository.existsBySlug(request.slug())) {
            throw new IllegalArgumentException(
                    "Algorithm slug already exists: " + request.slug()
            );
        }

        AlgorithmCategory category = categoryRepository
                .findBySlug(request.categorySlug())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found: "
                                        + request.categorySlug()
                        )
                );

        Algorithm algorithm = new Algorithm(
                category,
                request.name(),
                request.slug(),
                request.description(),
                request.difficulty(),
                request.timeComplexity(),
                request.spaceComplexity()
        );

        Algorithm savedAlgorithm =
                algorithmRepository.save(algorithm);

        return mapAlgorithm(savedAlgorithm);
    }

    @Transactional
    public AlgorithmResponse updateAlgorithm(
            String slug,
            UpdateAlgorithmRequest request
    ) {

        Algorithm algorithm = algorithmRepository
                .findBySlug(slug)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Algorithm not found: " + slug
                        )
                );

        if (!algorithm.getSlug().equals(request.slug())
                && algorithmRepository.existsBySlug(
                request.slug()
        )) {

            throw new IllegalArgumentException(
                    "Algorithm slug already exists: "
                            + request.slug()
            );
        }

        AlgorithmCategory category = categoryRepository
                .findBySlug(request.categorySlug())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found: "
                                        + request.categorySlug()
                        )
                );

        algorithm.update(
                category,
                request.name(),
                request.slug(),
                request.description(),
                request.difficulty(),
                request.timeComplexity(),
                request.spaceComplexity()
        );

        Algorithm updatedAlgorithm =
                algorithmRepository.save(algorithm);

        return mapAlgorithm(updatedAlgorithm);
    }

    @Transactional
    public void deleteAlgorithm(String slug) {

        Algorithm algorithm = algorithmRepository
                .findBySlug(slug)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Algorithm not found: " + slug
                        )
                );

        algorithmRepository.delete(algorithm);
    }

    @Transactional
    public AlgorithmCategoryResponse createCategory(
            CreateAlgorithmCategoryRequest request
    ) {
        if (categoryRepository.existsByName(request.name())) {
            throw new IllegalArgumentException(
                    "Category name already exists: " + request.name()
            );
        }

        if (categoryRepository.existsBySlug(request.slug())) {
            throw new IllegalArgumentException(
                    "Category slug already exists: " + request.slug()
            );
        }

        AlgorithmCategory category = new AlgorithmCategory(
                request.name(),
                request.slug(),
                request.description()
        );

        AlgorithmCategory savedCategory = categoryRepository.save(category);

        return mapCategory(savedCategory);
    }

    @Transactional
    public AlgorithmCategoryResponse updateCategory(
            String slug,
            UpdateAlgorithmCategoryRequest request
    ) {
        AlgorithmCategory category = categoryRepository
                .findBySlug(slug)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found: " + slug
                        )
                );

        if (!category.getName().equalsIgnoreCase(request.name())
                && categoryRepository.existsByName(request.name())) {
            throw new IllegalArgumentException(
                    "Category name already exists: " + request.name()
            );
        }

        if (!category.getSlug().equals(request.slug())
                && categoryRepository.existsBySlug(request.slug())) {
            throw new IllegalArgumentException(
                    "Category slug already exists: " + request.slug()
            );
        }

        category.update(
                request.name(),
                request.slug(),
                request.description()
        );

        AlgorithmCategory updatedCategory = categoryRepository.save(category);

        return mapCategory(updatedCategory);
    }

    @Transactional
    public void deleteCategory(String slug) {
        AlgorithmCategory category = categoryRepository
                .findBySlug(slug)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found: " + slug
                        )
                );

        if (algorithmRepository.existsByCategoryId(category.getId())) {
            throw new IllegalArgumentException(
                    "Cannot delete category because it contains algorithms"
            );
        }

        categoryRepository.delete(category);
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