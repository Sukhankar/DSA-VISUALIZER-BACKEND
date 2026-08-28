package com.codeloom.dsa.practice.service;

import com.codeloom.dsa.algorithm.entity.AlgorithmCategory;
import com.codeloom.dsa.algorithm.repository.AlgorithmCategoryRepository;
import com.codeloom.dsa.analytics.dto.UserStreakDto;
import com.codeloom.dsa.analytics.dto.UserXpDto;
import com.codeloom.dsa.analytics.service.AnalyticsService;
import com.codeloom.dsa.analytics.service.GamificationService;
import com.codeloom.dsa.common.exception.ResourceNotFoundException;
import com.codeloom.dsa.practice.dto.*;
import com.codeloom.dsa.practice.entity.*;
import com.codeloom.dsa.practice.repository.PracticeSessionProblemRepository;
import com.codeloom.dsa.practice.repository.PracticeSessionRepository;
import com.codeloom.dsa.problem.dto.ProblemSummaryResponse;
import com.codeloom.dsa.problem.dto.SubmissionResponse;
import com.codeloom.dsa.problem.entity.Problem;
import com.codeloom.dsa.problem.entity.ProblemSubmission;
import com.codeloom.dsa.problem.entity.SubmissionVerdict;
import com.codeloom.dsa.problem.repository.ProblemRepository;
import com.codeloom.dsa.problem.repository.ProblemSubmissionRepository;
import com.codeloom.dsa.problem.service.ProblemService;
import com.codeloom.dsa.problem.service.ProblemSubmissionService;
import com.codeloom.dsa.user.entity.User;
import com.codeloom.dsa.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PracticeSessionService {

    private final PracticeSessionRepository sessionRepository;
    private final PracticeSessionProblemRepository sessionProblemRepository;
    private final ProblemRepository problemRepository;
    private final AlgorithmCategoryRepository categoryRepository;
    private final ProblemService problemService;
    private final ProblemSubmissionService submissionService;
    private final ProblemSubmissionRepository submissionRepository;
    private final DailyChallengeService dailyChallengeService;
    private final GamificationService gamificationService;
    private final AnalyticsService analyticsService;
    private final UserRepository userRepository;

    public PracticeSessionService(
            PracticeSessionRepository sessionRepository,
            PracticeSessionProblemRepository sessionProblemRepository,
            ProblemRepository problemRepository,
            AlgorithmCategoryRepository categoryRepository,
            ProblemService problemService,
            ProblemSubmissionService submissionService,
            ProblemSubmissionRepository submissionRepository,
            DailyChallengeService dailyChallengeService,
            GamificationService gamificationService,
            AnalyticsService analyticsService,
            UserRepository userRepository
    ) {
        this.sessionRepository = sessionRepository;
        this.sessionProblemRepository = sessionProblemRepository;
        this.problemRepository = problemRepository;
        this.categoryRepository = categoryRepository;
        this.problemService = problemService;
        this.submissionService = submissionService;
        this.submissionRepository = submissionRepository;
        this.dailyChallengeService = dailyChallengeService;
        this.gamificationService = gamificationService;
        this.analyticsService = analyticsService;
        this.userRepository = userRepository;
    }

    public PracticeSessionDto createSession(String userIdentifier, CreatePracticeSessionRequest request) {
        User user = getUserByIdentifier(userIdentifier);

        // Abandon any existing active session
        sessionRepository.findFirstByUserIdAndStatusOrderByStartedAtDesc(user.getId(), SessionStatus.IN_PROGRESS)
                .ifPresent(existing -> {
                    existing.abandonSession();
                    sessionRepository.save(existing);
                });

        AlgorithmCategory category = null;
        if (request.categoryId() != null) {
            category = categoryRepository.findById(request.categoryId()).orElse(null);
        }

        Integer timeLimit = request.timeLimitSeconds();
        if (request.mode() == PracticeMode.TIMED && timeLimit == null) {
            timeLimit = 1800; // Default 30 minutes for timed mode
        }

        PracticeSession session = new PracticeSession(user, request.mode(), request.difficulty(), category, timeLimit);

        List<Problem> selectedProblems = selectProblemsForSession(request, category);
        if (selectedProblems.isEmpty()) {
            throw new IllegalArgumentException("No active problems found matching session criteria");
        }

        for (int i = 0; i < selectedProblems.size(); i++) {
            PracticeSessionProblem sessionProblem = new PracticeSessionProblem(selectedProblems.get(i), i + 1);
            session.addProblem(sessionProblem);
        }

        PracticeSession savedSession = sessionRepository.save(session);
        return toSessionDto(savedSession, user);
    }

    @Transactional(readOnly = true)
    public PracticeSessionDto getSessionDto(String userIdentifier, UUID sessionId) {
        User user = getUserByIdentifier(userIdentifier);
        PracticeSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Practice session not found with ID: " + sessionId));

        if (!session.getUser().getId().equals(user.getId())) {
            throw new IllegalAccessError("Unauthorized access to practice session");
        }

        return toSessionDto(session, user);
    }

    public SessionSubmitResponse submitInSession(String userIdentifier, UUID sessionId, SessionSubmitRequest request) {
        User user = getUserByIdentifier(userIdentifier);

        PracticeSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Practice session not found with ID: " + sessionId));

        if (!session.getUser().getId().equals(user.getId())) {
            throw new IllegalAccessError("Unauthorized access to practice session");
        }

        if (session.getStatus() != SessionStatus.IN_PROGRESS) {
            throw new IllegalStateException("Practice session is not active");
        }

        PracticeSessionProblem sessionProblem = sessionProblemRepository
                .findBySessionIdAndProblemId(sessionId, request.problemId())
                .orElseThrow(() -> new IllegalArgumentException("Problem not found in this practice session"));

        // Execute code submission
        Problem targetProblem = sessionProblem.getProblem();
        SubmissionResponse submissionDto = submissionService.submitSolution(
                user.getEmail(),
                targetProblem.getSlug(),
                request.language(),
                request.code()
        );

        ProblemSubmission submission = submissionRepository.findById(submissionDto.id()).orElse(null);
        int xpEarnedInAttempt = 0;

        if (submissionDto.verdict() == SubmissionVerdict.ACCEPTED) {
            if (sessionProblem.getStatus() != SessionProblemStatus.SOLVED) {
                sessionProblem.markSolved(submission);

                session.incrementSolvedProblems();
                int problemScore = calculateProblemScore(sessionProblem.getProblem());
                session.addScore(problemScore);

                xpEarnedInAttempt = problemScore;
                session.addXpEarned(xpEarnedInAttempt);

                // Trigger Daily Challenge check
                if (submission != null) {
                    dailyChallengeService.processSubmissionForDailyChallenge(user, submission);
                }
            }
        } else {
            if (sessionProblem.getStatus() == SessionProblemStatus.UNATTEMPTED) {
                sessionProblem.setStatus(SessionProblemStatus.ATTEMPTED);
            }
        }

        sessionProblemRepository.save(sessionProblem);

        // Check if session completed
        boolean isFullySolved = session.getProblems().stream()
                .allMatch(p -> p.getStatus() == SessionProblemStatus.SOLVED);

        if (isFullySolved) {
            session.completeSession();
            int bonusSessionXp = 150;
            session.addXpEarned(bonusSessionXp);

            gamificationService.awardXp(
                    user,
                    bonusSessionXp,
                    "PRACTICE_SESSION_COMPLETE",
                    "Completed practice session (" + session.getMode() + ")"
            );

            gamificationService.processActivity(user, "PRACTICE_SESSION_COMPLETE", 0, "Completed session");
        }

        sessionRepository.save(session);

        return new SessionSubmitResponse(
                submissionDto,
                toSessionDto(session, user),
                isFullySolved,
                xpEarnedInAttempt
        );
    }

    public PracticeSessionDto abandonSession(String userIdentifier, UUID sessionId) {
        User user = getUserByIdentifier(userIdentifier);
        PracticeSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Practice session not found with ID: " + sessionId));

        if (!session.getUser().getId().equals(user.getId())) {
            throw new IllegalAccessError("Unauthorized access to practice session");
        }

        session.abandonSession();
        return toSessionDto(sessionRepository.save(session), user);
    }

    @Transactional(readOnly = true)
    public PracticeArenaOverviewResponse getArenaOverview(String userIdentifier) {
        User user = getUserByIdentifier(userIdentifier);
        DailyChallengeDto dailyChallenge = dailyChallengeService.getTodayChallengeDto(userIdentifier);
        UserStreakDto streak = analyticsService.getUserStreakDto(user.getId());
        UserXpDto xp = analyticsService.getUserXpDto(user.getId());

        PracticeSession activeSessionEntity = sessionRepository
                .findFirstByUserIdAndStatusOrderByStartedAtDesc(user.getId(), SessionStatus.IN_PROGRESS)
                .orElse(null);

        PracticeSessionDto activeSession = activeSessionEntity != null ? toSessionDto(activeSessionEntity, user) : null;
        long totalCompleted = sessionRepository.countByUserIdAndStatus(user.getId(), SessionStatus.COMPLETED);

        List<PracticeSession> recentEntities = sessionRepository.findTop5ByUserIdOrderByStartedAtDesc(user.getId());
        List<PracticeSessionDto> recentSessions = recentEntities.stream()
                .map(s -> toSessionDto(s, user))
                .toList();

        return new PracticeArenaOverviewResponse(
                dailyChallenge,
                streak,
                xp,
                activeSession,
                totalCompleted,
                recentSessions
        );
    }

    @Transactional(readOnly = true)
    public Page<PracticeSessionDto> getUserSessionHistory(String userIdentifier, int page, int size) {
        User user = getUserByIdentifier(userIdentifier);
        PageRequest pageRequest = PageRequest.of(page, size);
        return sessionRepository.findByUserIdOrderByStartedAtDesc(user.getId(), pageRequest)
                .map(s -> toSessionDto(s, user));
    }

    private User getUserByIdentifier(String identifier) {
        return userRepository.findByEmail(identifier)
                .or(() -> userRepository.findByUsername(identifier))
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + identifier));
    }

    private List<Problem> selectProblemsForSession(CreatePracticeSessionRequest request, AlgorithmCategory category) {
        List<Problem> available = new ArrayList<>();

        if (category != null) {
            available = problemRepository.findByCategoryId(category.getId());
        } else if (request.difficulty() != null) {
            available = problemRepository.findByDifficulty(request.difficulty());
        } else {
            available = problemRepository.findAll();
        }

        if (available.isEmpty()) {
            available = problemRepository.findAll();
        }

        List<Problem> mutableList = new ArrayList<>(available);
        Collections.shuffle(mutableList);

        int problemCount = switch (request.mode()) {
            case DAILY -> 1;
            case TIMED -> Math.min(4, mutableList.size());
            default -> Math.min(3, mutableList.size());
        };

        if (request.mode() == PracticeMode.DAILY) {
            DailyChallenge daily = dailyChallengeService.getOrCreateTodayChallenge();
            return List.of(daily.getProblem());
        }

        return mutableList.subList(0, problemCount);
    }

    private int calculateProblemScore(Problem problem) {
        return switch (problem.getDifficulty()) {
            case EASY -> 100;
            case MEDIUM -> 200;
            case HARD -> 400;
        };
    }

    public PracticeSessionDto toSessionDto(PracticeSession session, User user) {
        List<PracticeSessionProblemDto> problemDtos = session.getProblems().stream()
                .map(sp -> {
                    ProblemSummaryResponse summary = problemService.toSummaryResponse(sp.getProblem());
                    UUID subId = sp.getSubmission() != null ? sp.getSubmission().getId() : null;
                    return new PracticeSessionProblemDto(
                            sp.getId(),
                            sp.getOrderIndex(),
                            summary,
                            sp.getStatus(),
                            subId,
                            sp.getSolvedAt()
                    );
                })
                .toList();

        double accuracy = session.getTotalProblems() > 0
                ? (double) session.getSolvedProblems() / session.getTotalProblems() * 100.0
                : 0.0;

        String catName = session.getCategory() != null ? session.getCategory().getName() : null;

        return new PracticeSessionDto(
                session.getId(),
                session.getMode(),
                session.getStatus(),
                session.getDifficulty(),
                catName,
                session.getTimeLimitSeconds(),
                session.getTotalProblems(),
                session.getSolvedProblems(),
                session.getScore(),
                session.getXpEarned(),
                Math.round(accuracy * 10.0) / 10.0,
                session.getStartedAt(),
                session.getCompletedAt(),
                problemDtos
        );
    }
}
