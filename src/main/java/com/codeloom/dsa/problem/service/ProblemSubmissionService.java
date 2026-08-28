package com.codeloom.dsa.problem.service;

import com.codeloom.dsa.algorithm.entity.Difficulty;
import com.codeloom.dsa.analytics.service.GamificationService;
import com.codeloom.dsa.common.exception.ResourceNotFoundException;
import com.codeloom.dsa.problem.dto.*;
import com.codeloom.dsa.problem.entity.*;
import com.codeloom.dsa.problem.execution.CodeExecutionEngine;
import com.codeloom.dsa.problem.execution.ExecutionSummary;
import com.codeloom.dsa.problem.repository.ProblemCodeDraftRepository;
import com.codeloom.dsa.problem.repository.ProblemRepository;
import com.codeloom.dsa.problem.repository.ProblemSubmissionRepository;
import com.codeloom.dsa.problem.repository.ProblemTestCaseRepository;
import com.codeloom.dsa.user.entity.User;
import com.codeloom.dsa.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProblemSubmissionService {

    private final ProblemRepository problemRepository;
    private final ProblemTestCaseRepository testCaseRepository;
    private final ProblemSubmissionRepository submissionRepository;
    private final ProblemCodeDraftRepository draftRepository;
    private final UserRepository userRepository;
    private final CodeExecutionEngine executionEngine;
    private final GamificationService gamificationService;

    public ProblemSubmissionService(
            ProblemRepository problemRepository,
            ProblemTestCaseRepository testCaseRepository,
            ProblemSubmissionRepository submissionRepository,
            ProblemCodeDraftRepository draftRepository,
            UserRepository userRepository,
            CodeExecutionEngine executionEngine,
            GamificationService gamificationService
    ) {
        this.problemRepository = problemRepository;
        this.testCaseRepository = testCaseRepository;
        this.submissionRepository = submissionRepository;
        this.draftRepository = draftRepository;
        this.userRepository = userRepository;
        this.executionEngine = executionEngine;
        this.gamificationService = gamificationService;
    }

    @Transactional(readOnly = true)
    public RunCodeResponse runSampleCode(String slug, String language, String sourceCode) {
        Problem problem = problemRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Problem not found with slug: " + slug));

        List<ProblemTestCase> sampleTestCases = testCaseRepository
                .findByProblemIdAndIsHiddenFalseOrderByTestCaseNumberAsc(problem.getId());

        ExecutionSummary summary = executionEngine.evaluate(language, sourceCode, sampleTestCases);

        return new RunCodeResponse(
                summary.verdict() == SubmissionVerdict.ACCEPTED,
                summary.verdict(),
                summary.totalTests(),
                summary.passedTests(),
                summary.executionTimeMs(),
                summary.memoryUsedKb(),
                summary.testCaseResults()
        );
    }

    public SubmissionResponse submitSolution(String userEmail, String slug, String language, String sourceCode) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        Problem problem = problemRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Problem not found with slug: " + slug));

        // Save code draft
        saveCodeDraft(user, problem, language, sourceCode);

        // Fetch all test cases (both sample and hidden)
        List<ProblemTestCase> allTestCases = testCaseRepository
                .findByProblemIdOrderByTestCaseNumberAsc(problem.getId());

        // Create pending submission
        ProblemSubmission submission = new ProblemSubmission(
                user,
                problem,
                language,
                sourceCode,
                SubmissionStatus.RUNNING,
                SubmissionVerdict.ACCEPTED,
                0,
                0,
                allTestCases.size(),
                0
        );
        submission = submissionRepository.save(submission);

        // Evaluate against all test cases
        ExecutionSummary summary = executionEngine.evaluate(language, sourceCode, allTestCases);

        submission.updateResult(
                SubmissionStatus.COMPLETED,
                summary.verdict(),
                summary.executionTimeMs(),
                summary.memoryUsedKb(),
                summary.totalTests(),
                summary.passedTests()
        );

        submission = submissionRepository.save(submission);

        // Process gamification rewards for accepted submission
        if (summary.verdict() == SubmissionVerdict.ACCEPTED) {
            int xp = getXpForDifficulty(problem.getDifficulty());
            gamificationService.processActivity(
                    user,
                    "PROBLEM_SOLVED_" + problem.getDifficulty().name(),
                    xp,
                    "Solved problem: " + problem.getTitle() + " (" + problem.getDifficulty() + ")"
            );
        } else {
            gamificationService.processActivity(user, "SUBMISSION_ATTEMPT", 0, "Attempted problem: " + problem.getTitle());
        }

        return mapSubmissionResponse(submission);
    }

    @Transactional(readOnly = true)
    public List<SubmissionResponse> getProblemSubmissions(String userEmail, String slug) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        Problem problem = problemRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Problem not found with slug: " + slug));

        return submissionRepository
                .findByUserIdAndProblemIdOrderBySubmittedAtDesc(user.getId(), problem.getId())
                .stream()
                .map(this::mapSubmissionResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<SubmissionResponse> getUserSubmissions(String userEmail, Pageable pageable) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        return submissionRepository
                .findByUserIdOrderBySubmittedAtDesc(user.getId(), pageable)
                .map(this::mapSubmissionResponse);
    }

    @Transactional(readOnly = true)
    public ProblemUserStatsResponse getUserProblemStats(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        long totalSolved = submissionRepository.countDistinctSolvedProblemsByUserId(user.getId(), SubmissionVerdict.ACCEPTED);
        long easySolved = submissionRepository.countDistinctSolvedProblemsByUserIdAndDifficulty(user.getId(), SubmissionVerdict.ACCEPTED, Difficulty.EASY);
        long mediumSolved = submissionRepository.countDistinctSolvedProblemsByUserIdAndDifficulty(user.getId(), SubmissionVerdict.ACCEPTED, Difficulty.MEDIUM);
        long hardSolved = submissionRepository.countDistinctSolvedProblemsByUserIdAndDifficulty(user.getId(), SubmissionVerdict.ACCEPTED, Difficulty.HARD);

        long totalSubmissions = submissionRepository.countByUserId(user.getId());
        long acceptedSubmissions = submissionRepository.countByUserIdAndVerdict(user.getId(), SubmissionVerdict.ACCEPTED);

        double acceptanceRate = totalSubmissions > 0 ? ((double) acceptedSubmissions / totalSubmissions) * 100.0 : 0.0;

        return new ProblemUserStatsResponse(
                totalSolved,
                easySolved,
                mediumSolved,
                hardSolved,
                totalSubmissions,
                acceptedSubmissions,
                Math.round(acceptanceRate * 10.0) / 10.0
        );
    }

    private int getXpForDifficulty(Difficulty difficulty) {
        if (difficulty == null) return 100;
        return switch (difficulty) {
            case EASY -> 100;
            case MEDIUM -> 200;
            case HARD -> 400;
        };
    }

    private void saveCodeDraft(User user, Problem problem, String language, String sourceCode) {
        ProblemCodeDraft draft = draftRepository
                .findByUserIdAndProblemIdAndLanguage(user.getId(), problem.getId(), language)
                .orElse(new ProblemCodeDraft(user, problem, language, sourceCode));

        draft.updateSourceCode(sourceCode);
        draftRepository.save(draft);
    }

    private SubmissionResponse mapSubmissionResponse(ProblemSubmission sub) {
        return new SubmissionResponse(
                sub.getId(),
                sub.getProblem().getSlug(),
                sub.getProblem().getTitle(),
                sub.getLanguage(),
                sub.getSourceCode(),
                sub.getStatus(),
                sub.getVerdict(),
                sub.getExecutionTimeMs(),
                sub.getMemoryUsedKb(),
                sub.getTotalTests(),
                sub.getPassedTests(),
                sub.getSubmittedAt(),
                sub.getCompletedAt()
        );
    }
}
