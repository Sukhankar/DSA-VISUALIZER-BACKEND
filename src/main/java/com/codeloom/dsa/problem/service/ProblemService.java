package com.codeloom.dsa.problem.service;

import com.codeloom.dsa.algorithm.dto.RelatedAlgorithmSummary;
import com.codeloom.dsa.algorithm.entity.Difficulty;
import com.codeloom.dsa.common.exception.ResourceNotFoundException;
import com.codeloom.dsa.problem.dto.*;
import com.codeloom.dsa.problem.entity.Problem;
import com.codeloom.dsa.problem.entity.ProblemTag;
import com.codeloom.dsa.problem.repository.ProblemExampleRepository;
import com.codeloom.dsa.problem.repository.ProblemRelatedAlgorithmRepository;
import com.codeloom.dsa.problem.repository.ProblemRepository;
import com.codeloom.dsa.problem.repository.ProblemTagRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProblemService {

    private final ProblemRepository problemRepository;
    private final ProblemExampleRepository exampleRepository;
    private final ProblemTagRepository tagRepository;
    private final ProblemRelatedAlgorithmRepository relatedAlgorithmRepository;

    public ProblemService(
            ProblemRepository problemRepository,
            ProblemExampleRepository exampleRepository,
            ProblemTagRepository tagRepository,
            ProblemRelatedAlgorithmRepository relatedAlgorithmRepository
    ) {
        this.problemRepository = problemRepository;
        this.exampleRepository = exampleRepository;
        this.tagRepository = tagRepository;
        this.relatedAlgorithmRepository = relatedAlgorithmRepository;
    }

    public ProblemPageResponse getProblems(
            Difficulty difficulty,
            String categorySlug,
            String search,
            Pageable pageable
    ) {
        Page<Problem> page;

        if (categorySlug != null && !categorySlug.isBlank()) {
            page = problemRepository.findByCategorySlug(categorySlug.trim(), pageable);
        } else if (difficulty != null) {
            page = problemRepository.findByDifficulty(difficulty, pageable);
        } else if (search != null && !search.isBlank()) {
            page = problemRepository.findByTitleContainingIgnoreCase(search.trim(), pageable);
        } else {
            page = problemRepository.findAll(pageable);
        }

        List<ProblemSummaryResponse> content = page.getContent()
                .stream()
                .map(this::mapProblemSummary)
                .toList();

        return new ProblemPageResponse(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }

    public ProblemDetailResponse getProblemBySlug(String slug) {
        Problem problem = problemRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Problem not found with slug: " + slug));

        List<String> tags = tagRepository.findByProblemId(problem.getId())
                .stream()
                .map(ProblemTag::getTagName)
                .toList();

        List<ProblemExampleResponse> examples = exampleRepository
                .findByProblemIdOrderByExampleNumberAsc(problem.getId())
                .stream()
                .map(e -> new ProblemExampleResponse(
                        e.getExampleNumber(),
                        e.getInputData(),
                        e.getOutputData(),
                        e.getExplanation()
                ))
                .toList();

        List<RelatedAlgorithmSummary> related = relatedAlgorithmRepository
                .findByProblemId(problem.getId())
                .stream()
                .map(r -> new RelatedAlgorithmSummary(
                        r.getAlgorithm().getId(),
                        r.getAlgorithm().getName(),
                        r.getAlgorithm().getSlug(),
                        r.getAlgorithm().getDifficulty(),
                        r.getAlgorithm().getCategory().getName()
                ))
                .toList();

        return new ProblemDetailResponse(
                problem.getId(),
                problem.getTitle(),
                problem.getSlug(),
                problem.getDifficulty(),
                problem.getDescription(),
                problem.getConstraints(),
                problem.getInputFormat(),
                problem.getOutputFormat(),
                problem.getHints(),
                problem.getSolutionExplanation(),
                problem.getCategory() != null ? problem.getCategory().getName() : "General",
                problem.getCategory() != null ? problem.getCategory().getSlug() : "general",
                tags,
                examples,
                related
        );
    }

    public ProblemSummaryResponse toSummaryResponse(Problem problem) {
        return mapProblemSummary(problem);
    }

    private ProblemSummaryResponse mapProblemSummary(Problem problem) {

        List<String> tags = tagRepository.findByProblemId(problem.getId())
                .stream()
                .map(ProblemTag::getTagName)
                .toList();

        return new ProblemSummaryResponse(
                problem.getId(),
                problem.getTitle(),
                problem.getSlug(),
                problem.getDifficulty(),
                problem.getCategory() != null ? problem.getCategory().getName() : "General",
                problem.getCategory() != null ? problem.getCategory().getSlug() : "general",
                tags
        );
    }
}
