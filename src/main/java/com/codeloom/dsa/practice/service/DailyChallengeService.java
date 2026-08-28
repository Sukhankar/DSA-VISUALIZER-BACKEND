package com.codeloom.dsa.practice.service;

import com.codeloom.dsa.analytics.service.GamificationService;

import com.codeloom.dsa.practice.dto.DailyChallengeDto;
import com.codeloom.dsa.practice.entity.DailyChallenge;
import com.codeloom.dsa.practice.entity.UserDailyChallenge;
import com.codeloom.dsa.practice.repository.DailyChallengeRepository;
import com.codeloom.dsa.practice.repository.UserDailyChallengeRepository;
import com.codeloom.dsa.problem.dto.ProblemSummaryResponse;
import com.codeloom.dsa.problem.entity.Problem;
import com.codeloom.dsa.problem.entity.ProblemSubmission;
import com.codeloom.dsa.problem.entity.SubmissionVerdict;
import com.codeloom.dsa.problem.repository.ProblemRepository;
import com.codeloom.dsa.problem.service.ProblemService;
import com.codeloom.dsa.user.entity.User;
import com.codeloom.dsa.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class DailyChallengeService {

    private final DailyChallengeRepository dailyChallengeRepository;
    private final UserDailyChallengeRepository userDailyChallengeRepository;
    private final ProblemRepository problemRepository;
    private final ProblemService problemService;
    private final GamificationService gamificationService;
    private final UserRepository userRepository;

    public DailyChallengeService(
            DailyChallengeRepository dailyChallengeRepository,
            UserDailyChallengeRepository userDailyChallengeRepository,
            ProblemRepository problemRepository,
            ProblemService problemService,
            GamificationService gamificationService,
            UserRepository userRepository
    ) {
        this.dailyChallengeRepository = dailyChallengeRepository;
        this.userDailyChallengeRepository = userDailyChallengeRepository;
        this.problemRepository = problemRepository;
        this.problemService = problemService;
        this.gamificationService = gamificationService;
        this.userRepository = userRepository;
    }

    public DailyChallenge getOrCreateTodayChallenge() {
        LocalDate today = LocalDate.now();
        return dailyChallengeRepository.findByChallengeDate(today)
                .orElseGet(() -> {
                    List<Problem> activeProblems = problemRepository.findAll();
                    if (activeProblems.isEmpty()) {
                        throw new IllegalStateException("No active problems available to create daily challenge");
                    }
                    // Rotate through active problems using day-of-year offset
                    int dayIndex = Math.abs(today.getDayOfYear() - 1) % activeProblems.size();
                    Problem selectedProblem = activeProblems.get(dayIndex);
                    DailyChallenge challenge = new DailyChallenge(today, selectedProblem, 100);
                    return dailyChallengeRepository.save(challenge);
                });
    }

    @Transactional(readOnly = true)
    public DailyChallengeDto getTodayChallengeDto(String userIdentifier) {
        DailyChallenge challenge = getOrCreateTodayChallenge();
        boolean completed = false;
        String status = "PENDING";

        if (userIdentifier != null && !userIdentifier.isBlank()) {
            User user = userRepository.findByEmail(userIdentifier)
                    .or(() -> userRepository.findByUsername(userIdentifier))
                    .orElse(null);

            if (user != null) {
                UserDailyChallenge userChallenge = userDailyChallengeRepository
                        .findByUserIdAndDailyChallengeId(user.getId(), challenge.getId())
                        .orElse(null);
                if (userChallenge != null && userChallenge.getStatus() == com.codeloom.dsa.practice.entity.SessionProblemStatus.SOLVED) {
                    completed = true;
                    status = "COMPLETED";
                }
            }
        }

        ProblemSummaryResponse problemDto = problemService.toSummaryResponse(challenge.getProblem());
        return new DailyChallengeDto(
                challenge.getId(),
                challenge.getChallengeDate(),
                problemDto,
                challenge.getBonusXp(),
                completed,
                status
        );
    }

    public void processSubmissionForDailyChallenge(User user, ProblemSubmission submission) {
        if (submission.getVerdict() != SubmissionVerdict.ACCEPTED) {
            return;
        }

        DailyChallenge todayChallenge = getOrCreateTodayChallenge();
        if (todayChallenge.getProblem().getId().equals(submission.getProblem().getId())) {
            UserDailyChallenge userChallenge = userDailyChallengeRepository
                    .findByUserIdAndDailyChallengeId(user.getId(), todayChallenge.getId())
                    .orElseGet(() -> new UserDailyChallenge(user, todayChallenge));

            if (userChallenge.getStatus() != com.codeloom.dsa.practice.entity.SessionProblemStatus.SOLVED) {
                userChallenge.markCompleted(submission);
                userDailyChallengeRepository.save(userChallenge);

                // Award bonus XP for Daily Challenge
                gamificationService.awardXp(
                        user,
                        todayChallenge.getBonusXp(),
                        "DAILY_CHALLENGE",
                        "Completed Daily Challenge for " + todayChallenge.getChallengeDate()
                );
            }
        }
    }
}
