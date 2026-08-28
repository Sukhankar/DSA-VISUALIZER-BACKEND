package com.codeloom.dsa.progress.service;

import com.codeloom.dsa.algorithm.entity.Algorithm;
import com.codeloom.dsa.algorithm.repository.AlgorithmRepository;
import com.codeloom.dsa.analytics.service.GamificationService;
import com.codeloom.dsa.common.exception.ResourceNotFoundException;
import com.codeloom.dsa.progress.dto.LearningDashboardResponse;
import com.codeloom.dsa.progress.dto.ProgressResponse;
import com.codeloom.dsa.progress.dto.UpdateProgressRequest;
import com.codeloom.dsa.progress.entity.ProgressStatus;
import com.codeloom.dsa.progress.entity.UserAlgorithmProgress;
import com.codeloom.dsa.progress.repository.UserAlgorithmProgressRepository;
import com.codeloom.dsa.progress.repository.UserFavoriteRepository;
import com.codeloom.dsa.user.entity.User;
import com.codeloom.dsa.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserProgressService {

    private final UserRepository userRepository;
    private final AlgorithmRepository algorithmRepository;
    private final UserAlgorithmProgressRepository progressRepository;
    private final UserFavoriteRepository favoriteRepository;
    private final GamificationService gamificationService;

    public UserProgressService(
            UserRepository userRepository,
            AlgorithmRepository algorithmRepository,
            UserAlgorithmProgressRepository progressRepository,
            UserFavoriteRepository favoriteRepository,
            GamificationService gamificationService
    ) {
        this.userRepository = userRepository;
        this.algorithmRepository = algorithmRepository;
        this.progressRepository = progressRepository;
        this.favoriteRepository = favoriteRepository;
        this.gamificationService = gamificationService;
    }

    @Transactional
    public ProgressResponse startProgress(String userEmail, String algorithmSlug) {
        User user = getUserByEmail(userEmail);
        Algorithm algorithm = getAlgorithmBySlug(algorithmSlug);

        UserAlgorithmProgress progress = progressRepository
                .findByUserIdAndAlgorithmId(user.getId(), algorithm.getId())
                .orElseGet(() -> new UserAlgorithmProgress(user, algorithm));

        progress.start();
        UserAlgorithmProgress savedProgress = progressRepository.save(progress);

        gamificationService.processActivity(user, "ALGORITHM_VISUALIZATION", 10, "Started algorithm: " + algorithm.getName());

        return mapToProgressResponse(savedProgress);
    }

    @Transactional
    public ProgressResponse updateProgress(String userEmail, String algorithmSlug, UpdateProgressRequest request) {
        User user = getUserByEmail(userEmail);
        Algorithm algorithm = getAlgorithmBySlug(algorithmSlug);

        UserAlgorithmProgress progress = progressRepository
                .findByUserIdAndAlgorithmId(user.getId(), algorithm.getId())
                .orElseGet(() -> new UserAlgorithmProgress(user, algorithm));

        progress.updateProgress(request.progressPercentage(), request.lastStep());
        UserAlgorithmProgress savedProgress = progressRepository.save(progress);

        return mapToProgressResponse(savedProgress);
    }

    @Transactional
    public ProgressResponse completeProgress(String userEmail, String algorithmSlug) {
        User user = getUserByEmail(userEmail);
        Algorithm algorithm = getAlgorithmBySlug(algorithmSlug);

        UserAlgorithmProgress progress = progressRepository
                .findByUserIdAndAlgorithmId(user.getId(), algorithm.getId())
                .orElseGet(() -> new UserAlgorithmProgress(user, algorithm));

        progress.complete();
        UserAlgorithmProgress savedProgress = progressRepository.save(progress);

        gamificationService.processActivity(user, "ALGORITHM_VISUALIZATION", 50, "Completed algorithm: " + algorithm.getName());

        return mapToProgressResponse(savedProgress);
    }

    public ProgressResponse getProgress(String userEmail, String algorithmSlug) {
        User user = getUserByEmail(userEmail);
        Algorithm algorithm = getAlgorithmBySlug(algorithmSlug);

        return progressRepository.findByUserIdAndAlgorithmId(user.getId(), algorithm.getId())
                .map(this::mapToProgressResponse)
                .orElseGet(() -> new ProgressResponse(
                        algorithm.getId(),
                        algorithm.getName(),
                        algorithm.getSlug(),
                        ProgressStatus.NOT_STARTED,
                        0,
                        null,
                        null,
                        null,
                        null
                ));
    }

    public List<ProgressResponse> listAllProgress(String userEmail) {
        User user = getUserByEmail(userEmail);
        return progressRepository.findByUserIdOrderByUpdatedAtDesc(user.getId())
                .stream()
                .map(this::mapToProgressResponse)
                .toList();
    }

    public LearningDashboardResponse getDashboard(String userEmail) {
        User user = getUserByEmail(userEmail);

        long totalAlgorithms = algorithmRepository.count();
        long startedAlgorithms = progressRepository.countByUserIdAndStatusIn(
                user.getId(),
                List.of(ProgressStatus.IN_PROGRESS, ProgressStatus.COMPLETED)
        );
        long completedAlgorithms = progressRepository.countByUserIdAndStatus(
                user.getId(),
                ProgressStatus.COMPLETED
        );
        long favoriteAlgorithms = favoriteRepository.countByUserId(user.getId());

        double completionPercentage = 0.0;
        if (totalAlgorithms > 0) {
            completionPercentage = Math.round(((double) completedAlgorithms / totalAlgorithms * 100.0) * 100.0) / 100.0;
        }

        List<ProgressResponse> recentProgress = progressRepository
                .findTop5ByUserIdOrderByUpdatedAtDesc(user.getId())
                .stream()
                .map(this::mapToProgressResponse)
                .toList();

        return new LearningDashboardResponse(
                totalAlgorithms,
                startedAlgorithms,
                completedAlgorithms,
                favoriteAlgorithms,
                completionPercentage,
                recentProgress
        );
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    private Algorithm getAlgorithmBySlug(String slug) {
        return algorithmRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Algorithm not found with slug: " + slug));
    }

    private ProgressResponse mapToProgressResponse(UserAlgorithmProgress progress) {
        Algorithm algo = progress.getAlgorithm();
        return new ProgressResponse(
                algo.getId(),
                algo.getName(),
                algo.getSlug(),
                progress.getStatus(),
                progress.getProgressPercentage(),
                progress.getLastStep(),
                progress.getStartedAt(),
                progress.getCompletedAt(),
                progress.getUpdatedAt()
        );
    }
}
