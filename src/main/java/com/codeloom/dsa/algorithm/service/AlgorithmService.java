package com.codeloom.dsa.algorithm.service;

import com.codeloom.dsa.algorithm.dto.*;
import com.codeloom.dsa.algorithm.entity.Algorithm;
import com.codeloom.dsa.algorithm.entity.AlgorithmCategory;
import com.codeloom.dsa.algorithm.entity.Difficulty;
import com.codeloom.dsa.algorithm.repository.AlgorithmCategoryRepository;
import com.codeloom.dsa.algorithm.repository.AlgorithmExampleRepository;
import com.codeloom.dsa.algorithm.repository.AlgorithmImplementationRepository;
import com.codeloom.dsa.algorithm.repository.AlgorithmRepository;
import com.codeloom.dsa.algorithm.repository.RelatedAlgorithmRepository;
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
    private final AlgorithmExampleRepository exampleRepository;
    private final AlgorithmImplementationRepository implementationRepository;
    private final RelatedAlgorithmRepository relatedRepository;
    private final com.codeloom.dsa.algorithm.repository.AlgorithmLearningContentRepository learningContentRepository;
    private final com.codeloom.dsa.algorithm.repository.AlgorithmLearningAdvancedRepository learningAdvancedRepository;
    private final com.codeloom.dsa.algorithm.repository.AlgorithmLearningPracticeRepository learningPracticeRepository;

    public AlgorithmService(
            AlgorithmRepository algorithmRepository,
            AlgorithmCategoryRepository categoryRepository,
            AlgorithmExampleRepository exampleRepository,
            AlgorithmImplementationRepository implementationRepository,
            RelatedAlgorithmRepository relatedRepository,
            com.codeloom.dsa.algorithm.repository.AlgorithmLearningContentRepository learningContentRepository,
            com.codeloom.dsa.algorithm.repository.AlgorithmLearningAdvancedRepository learningAdvancedRepository,
            com.codeloom.dsa.algorithm.repository.AlgorithmLearningPracticeRepository learningPracticeRepository
    ) {
        this.algorithmRepository = algorithmRepository;
        this.categoryRepository = categoryRepository;
        this.exampleRepository = exampleRepository;
        this.implementationRepository = implementationRepository;
        this.relatedRepository = relatedRepository;
        this.learningContentRepository = learningContentRepository;
        this.learningAdvancedRepository = learningAdvancedRepository;
        this.learningPracticeRepository = learningPracticeRepository;
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
                        new ResourceNotFoundException("Algorithm not found: " + slug)
                );

        return mapAlgorithm(algorithm);
    }

    public AlgorithmDetailRichResponse getRichAlgorithmDetails(String slug) {
        Algorithm algorithm = algorithmRepository
                .findBySlug(slug)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Algorithm not found: " + slug)
                );

        List<AlgorithmExampleResponse> examples = exampleRepository
                .findByAlgorithmIdOrderByExampleNumberAsc(algorithm.getId())
                .stream()
                .map(e -> new AlgorithmExampleResponse(
                        e.getExampleNumber(),
                        e.getTitle(),
                        e.getInputData(),
                        e.getOutputData(),
                        e.getExplanation()
                ))
                .toList();

        List<AlgorithmImplementationResponse> implementations = implementationRepository
                .findByAlgorithmIdOrderByDisplayOrderAsc(algorithm.getId())
                .stream()
                .map(i -> new AlgorithmImplementationResponse(
                        i.getLanguage(),
                        i.getCode(),
                        i.getExplanation(),
                        i.getDisplayOrder()
                ))
                .toList();

        List<RelatedAlgorithmSummary> related = relatedRepository
                .findByAlgorithmId(algorithm.getId())
                .stream()
                .map(r -> new RelatedAlgorithmSummary(
                        r.getRelatedAlgorithm().getId(),
                        r.getRelatedAlgorithm().getName(),
                        r.getRelatedAlgorithm().getSlug(),
                        r.getRelatedAlgorithm().getDifficulty(),
                        r.getRelatedAlgorithm().getCategory().getName()
                ))
                .toList();

        String overview = (algorithm.getOverview() != null && !algorithm.getOverview().isBlank())
                ? algorithm.getOverview()
                : getFallbackOverview(algorithm.getName(), algorithm.getCategory().getName());

        String whenToUse = (algorithm.getWhenToUse() != null && !algorithm.getWhenToUse().isBlank())
                ? algorithm.getWhenToUse()
                : getFallbackWhenToUse(algorithm.getName(), algorithm.getCategory().getName());

        String advantages = (algorithm.getAdvantages() != null && !algorithm.getAdvantages().isBlank())
                ? algorithm.getAdvantages()
                : getFallbackAdvantages(algorithm.getName(), algorithm.getCategory().getName());

        String limitations = (algorithm.getLimitations() != null && !algorithm.getLimitations().isBlank())
                ? algorithm.getLimitations()
                : getFallbackLimitations(algorithm.getName(), algorithm.getCategory().getName());

        String constraints = (algorithm.getConstraints() != null && !algorithm.getConstraints().isBlank())
                ? algorithm.getConstraints()
                : "• 1 <= input.length <= 100000\n• Valid memory bounds and boundary checks";

        return new AlgorithmDetailRichResponse(
                algorithm.getId(),
                algorithm.getName(),
                algorithm.getSlug(),
                algorithm.getDescription(),
                overview,
                whenToUse,
                advantages,
                limitations,
                constraints,
                algorithm.getDifficulty(),
                algorithm.getTimeComplexity(),
                algorithm.getSpaceComplexity(),
                algorithm.getCategory().getName(),
                algorithm.getCategory().getSlug(),
                examples,
                implementations,
                related
        );
    }

    private String getFallbackOverview(String name, String category) {
        return name + " is a core " + category.toLowerCase() + " algorithm designed to solve fundamental computational problems efficiently. " +
               "It relies on well-defined invariants and structural state transitions to achieve optimal worst-case and average-case performance bounds. " +
               "Widely documented across classical Computer Science literature (such as Cormen CLRS and GeeksforGeeks), it serves as a building block for complex software systems.";
    }

    private String getFallbackWhenToUse(String name, String category) {
        return "Recommended for " + category + " tasks where clean asymptotic bounds, predictable state transitions, and straightforward memory management are required.";
    }

    private String getFallbackAdvantages(String name, String category) {
        return "• Robust theoretical guarantees and predictable execution flow.\n" +
               "• Invariant-based state transitions ensuring correctness across all inputs.\n" +
               "• Standard industry usage with wide library support across Java, Python, and C++.";
    }

    private String getFallbackLimitations(String name, String category) {
        return "• May require careful handling of edge cases (empty inputs, single element data, boundary conditions).\n" +
               "• Performance heavily dependent on chosen data structures and underlying hardware cache behaviors.";
    }


    @Transactional
    public AlgorithmResponse createAlgorithm(CreateAlgorithmRequest request) {
        if (algorithmRepository.existsBySlug(request.slug())) {
            throw new IllegalArgumentException("Algorithm slug already exists: " + request.slug());
        }

        AlgorithmCategory category = categoryRepository
                .findBySlug(request.categorySlug())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + request.categorySlug()));

        Algorithm algorithm = new Algorithm(
                category,
                request.name(),
                request.slug(),
                request.description(),
                request.difficulty(),
                request.timeComplexity(),
                request.spaceComplexity()
        );

        Algorithm savedAlgorithm = algorithmRepository.save(algorithm);
        return mapAlgorithm(savedAlgorithm);
    }

    @Transactional
    public AlgorithmResponse updateAlgorithm(String slug, UpdateAlgorithmRequest request) {
        Algorithm algorithm = algorithmRepository
                .findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Algorithm not found: " + slug));

        if (!algorithm.getSlug().equals(request.slug()) && algorithmRepository.existsBySlug(request.slug())) {
            throw new IllegalArgumentException("Algorithm slug already exists: " + request.slug());
        }

        AlgorithmCategory category = categoryRepository
                .findBySlug(request.categorySlug())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + request.categorySlug()));

        algorithm.update(
                category,
                request.name(),
                request.slug(),
                request.description(),
                request.difficulty(),
                request.timeComplexity(),
                request.spaceComplexity()
        );

        Algorithm updatedAlgorithm = algorithmRepository.save(algorithm);
        return mapAlgorithm(updatedAlgorithm);
    }

    @Transactional
    public void deleteAlgorithm(String slug) {
        Algorithm algorithm = algorithmRepository
                .findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Algorithm not found: " + slug));

        algorithmRepository.delete(algorithm);
    }

    @Transactional
    public AlgorithmCategoryResponse createCategory(CreateAlgorithmCategoryRequest request) {
        if (categoryRepository.existsByName(request.name())) {
            throw new IllegalArgumentException("Category name already exists: " + request.name());
        }

        if (categoryRepository.existsBySlug(request.slug())) {
            throw new IllegalArgumentException("Category slug already exists: " + request.slug());
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
    public AlgorithmCategoryResponse updateCategory(String slug, UpdateAlgorithmCategoryRequest request) {
        AlgorithmCategory category = categoryRepository
                .findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + slug));

        if (!category.getName().equalsIgnoreCase(request.name()) && categoryRepository.existsByName(request.name())) {
            throw new IllegalArgumentException("Category name already exists: " + request.name());
        }

        if (!category.getSlug().equals(request.slug()) && categoryRepository.existsBySlug(request.slug())) {
            throw new IllegalArgumentException("Category slug already exists: " + request.slug());
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
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + slug));

        if (algorithmRepository.existsByCategoryId(category.getId())) {
            throw new IllegalArgumentException("Cannot delete category because it contains algorithms");
        }

        categoryRepository.delete(category);
    }

    public AlgorithmLearningResponse getLearningContent(String slug, com.codeloom.dsa.learning.entity.ExperienceLevel level) {
        Algorithm algorithm = algorithmRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Algorithm not found: " + slug));

        com.codeloom.dsa.learning.entity.ExperienceLevel reqLevel = level != null ? level : com.codeloom.dsa.learning.entity.ExperienceLevel.BEGINNER;

        var optContent = learningContentRepository.findByAlgorithmIdAndLevel(algorithm.getId(), reqLevel);
        if (optContent.isPresent()) {
            var c = optContent.get();
            var advOpt = learningAdvancedRepository.findByLearningContentId(c.getId());
            var advRes = advOpt.map(a -> new AlgorithmLearningResponse.AdvancedTheoryResponse(
                    a.getMathematicalFoundation(),
                    a.getInvariant(),
                    a.getCorrectnessProof(),
                    a.getRecurrence(),
                    a.getRecurrenceSolution(),
                    a.getOptimization(),
                    a.getMemoryAnalysis(),
                    a.getAdvancedTradeoffs(),
                    a.getCompetitiveProgrammingNotes()
            )).orElse(null);

            var practiceList = learningPracticeRepository.findByLearningContentIdOrderByDisplayOrderAsc(c.getId())
                    .stream()
                    .map(p -> new AlgorithmLearningResponse.PracticeRecommendationResponse(
                            p.getProblemTitle(),
                            p.getProblemSlug(),
                            p.getDifficulty(),
                            p.getPlatform()
                    ))
                    .toList();

            List<String> steps = List.of(
                    "1. Initialize algorithm state & boundary pointers.",
                    "2. Compare candidate values or expand search frontier.",
                    "3. Apply state transition or swap elements into place.",
                    "4. Repeat until exit condition is satisfied."
            );
            if (c.getHowItWorks() != null && c.getHowItWorks().startsWith("[")) {
                try {
                    steps = List.of(c.getHowItWorks().replaceAll("[\\[\\]\"]", "").split(",\\s*"));
                } catch (Exception ignored) {}
            }

            return new AlgorithmLearningResponse(
                    algorithm.getId(),
                    algorithm.getName(),
                    algorithm.getSlug(),
                    reqLevel,
                    c.getIntroduction(),
                    c.getProblemStatement(),
                    c.getIntuition(),
                    c.getWhyItWorks(),
                    steps,
                    c.getPseudocode(),
                    c.getComplexitySummary(),
                    c.getWhenToUse(),
                    c.getWhenNotToUse(),
                    c.getAdvantages(),
                    c.getLimitations(),
                    c.getCommonMistakes(),
                    c.getInterviewTips(),
                    c.getImplementationNotes(),
                    advRes,
                    practiceList
            );
        }

        // Dynamic fallback generation if specific DB row is not present
        return generateDynamicLearningContent(algorithm, reqLevel);
    }

    private AlgorithmLearningResponse generateDynamicLearningContent(Algorithm algorithm, com.codeloom.dsa.learning.entity.ExperienceLevel level) {
        String name = algorithm.getName();
        String cat = algorithm.getCategory().getName();

        List<AlgorithmLearningResponse.PracticeRecommendationResponse> defaultPractice = List.of(
                new AlgorithmLearningResponse.PracticeRecommendationResponse(name + " Fundamentals", algorithm.getSlug(), "EASY", "CodeLoom Arena"),
                new AlgorithmLearningResponse.PracticeRecommendationResponse(name + " Advanced Optimization", algorithm.getSlug(), "MEDIUM", "CodeLoom Arena")
        );

        if (level == com.codeloom.dsa.learning.entity.ExperienceLevel.BEGINNER) {
            return new AlgorithmLearningResponse(
                    algorithm.getId(), name, algorithm.getSlug(), level,
                    name + " is a beginner-friendly " + cat.toLowerCase() + " algorithm.",
                    "Reorder or search input data efficiently using step-by-step state checks.",
                    "Think of it like arranging cards in your hands or finding a word in a physical dictionary.",
                    "By following consistent rules at each step, we guarantee reaching the correct result.",
                    List.of(
                            "1. Look at the current input elements.",
                            "2. Compare the elements using decision rules.",
                            "3. Update your position or swap elements.",
                            "4. Stop when the job is done!"
                    ),
                    "// Beginner Pseudocode\nfor item in array:\n  if condition:\n    process(item)",
                    "Time: " + (algorithm.getTimeComplexity() != null ? algorithm.getTimeComplexity() : "O(N)") + " | Space: " + (algorithm.getSpaceComplexity() != null ? algorithm.getSpaceComplexity() : "O(1)"),
                    "• Small datasets\n• Educational contexts & initial learning",
                    "• Massive real-time data streams",
                    "• Easy to implement\n• Low memory footprint",
                    "• Can be slow for huge inputs",
                    "• Off-by-one errors in loop boundaries\n• Incorrect conditional comparison signs",
                    "Mention that you always check empty arrays and single-element inputs first!",
                    "Start with simple arrays before attempting edge cases.",
                    null,
                    defaultPractice
            );
        } else if (level == com.codeloom.dsa.learning.entity.ExperienceLevel.INTERMEDIATE) {
            return new AlgorithmLearningResponse(
                    algorithm.getId(), name, algorithm.getSlug(), level,
                    name + " provides structured " + cat.toLowerCase() + " state transitions with formal bounds.",
                    "Given input structure I, produce output O satisfying target invariants.",
                    "Reduces redundant comparisons by maintaining active boundary pointers.",
                    "Every iteration reduces the remaining problem space according to asymptotic rules.",
                    List.of(
                            "1. Initialize boundary pointers and tracking variables.",
                            "2. Execute loop invariant checks over active interval.",
                            "3. Mutate array state or traverse child nodes.",
                            "4. Return processed target result."
                    ),
                    "function " + algorithm.getSlug().replace("-", "") + "(data):\n  initialize State\n  while not Done:\n    step(State)",
                    "Best/Avg/Worst Time: " + algorithm.getTimeComplexity() + " | Auxiliary Space: " + algorithm.getSpaceComplexity(),
                    "• Production services requiring reliable performance\n• Standard interview problem patterns",
                    "• When specialized cache-oblivious algorithms exist for specific hardware",
                    "• Proven asymptotic bounds\n• Reusable across technical domains",
                    "• Memory overhead depending on recursion stack depth",
                    "• Failing to reset state between iterations\n• Memory leaks in pointer manipulation",
                    "Focus on explaining time complexity derivations and edge-case handling in technical interviews.",
                    "Verify loop termination conditions carefully during whiteboarding.",
                    null,
                    defaultPractice
            );
        } else {
            // ADVANCED
            AlgorithmLearningResponse.AdvancedTheoryResponse adv = new AlgorithmLearningResponse.AdvancedTheoryResponse(
                    "Mathematical Formulation: State space S and transition function f: S -> S.",
                    "Invariant: At step k, property P(k) holds over processed sub-range.",
                    "Correctness Proof: Proven by Mathematical Induction over input length N.",
                    "Recurrence Relation: T(N) = aT(N/b) + f(N).",
                    "Solution via Master Theorem: " + (algorithm.getTimeComplexity() != null ? algorithm.getTimeComplexity() : "O(N log N)"),
                    "Optimization: Bitwise operations, cache line padding, and SIMD parallelization.",
                    "Memory Behavior: Locality of reference and CPU cache hierarchy implications.",
                    "Trade-off between time complexity reduction and memory stack allocation.",
                    "Competitive Programming: Watch out for integer overflow and custom comparator speed."
            );

            return new AlgorithmLearningResponse(
                    algorithm.getId(), name, algorithm.getSlug(), level,
                    "Advanced mathematical formulation, invariants, and memory behavior of " + name + ".",
                    "Strict formal specifications over arbitrary input structures.",
                    "Eliminates inversion bounds and optimizes instruction cache pipeline throughput.",
                    "Proves strict convergence to optimal state in finite computational steps.",
                    List.of(
                            "1. Establish global inductive state invariants.",
                            "2. Execute cache-aligned state updates.",
                            "3. Maintain invariant P across recursive sub-problems.",
                            "4. Output proven optimal solution."
                    ),
                    "template <typename T>\nvoid " + algorithm.getSlug().replace("-", "") + "(std::vector<T>& data) {\n  // Advanced C++ implementation\n}",
                    "Tight Bound: Theta(" + (algorithm.getTimeComplexity() != null ? algorithm.getTimeComplexity() : "N log N") + ") | Space: Theta(" + (algorithm.getSpaceComplexity() != null ? algorithm.getSpaceComplexity() : "1") + ")",
                    "• High-throughput enterprise pipelines\n• Competitive programming constraints",
                    "• Unconstrained memory environments where cache misses dominate",
                    "• Micro-architectural optimization potential\n• Strict mathematical correctness proofs",
                    "• Implementation complexity and high engineering overhead",
                    "• Ignoring CPU cache line sizes and memory alignment",
                    "Be prepared to solve follow-up constraints like O(1) extra space or streaming input.",
                    "Consider lock-free or SIMD vectorization for high-concurrency systems.",
                    adv,
                    defaultPractice
            );
        }
    }

    private AlgorithmCategoryResponse mapCategory(AlgorithmCategory category) {
        return new AlgorithmCategoryResponse(
                category.getId(),
                category.getName(),
                category.getSlug(),
                category.getDescription()
        );
    }

    private AlgorithmResponse mapAlgorithm(Algorithm algorithm) {
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