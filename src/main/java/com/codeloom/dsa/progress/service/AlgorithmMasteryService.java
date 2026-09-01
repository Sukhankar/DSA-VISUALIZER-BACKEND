package com.codeloom.dsa.progress.service;

import com.codeloom.dsa.algorithm.entity.Algorithm;
import com.codeloom.dsa.algorithm.repository.AlgorithmRepository;
import com.codeloom.dsa.analytics.service.GamificationService;
import com.codeloom.dsa.common.exception.ResourceNotFoundException;
import com.codeloom.dsa.progress.dto.AlgorithmMasteryDto;
import com.codeloom.dsa.progress.entity.UserAlgorithmMastery;
import com.codeloom.dsa.progress.repository.UserAlgorithmMasteryRepository;
import com.codeloom.dsa.user.entity.User;
import com.codeloom.dsa.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AlgorithmMasteryService {

    private final UserAlgorithmMasteryRepository masteryRepository;
    private final AlgorithmRepository algorithmRepository;
    private final UserRepository userRepository;
    private final GamificationService gamificationService;

    public AlgorithmMasteryService(
            UserAlgorithmMasteryRepository masteryRepository,
            AlgorithmRepository algorithmRepository,
            UserRepository userRepository,
            GamificationService gamificationService
    ) {
        this.masteryRepository = masteryRepository;
        this.algorithmRepository = algorithmRepository;
        this.userRepository = userRepository;
        this.gamificationService = gamificationService;
    }

    @Transactional(readOnly = true)
    public AlgorithmMasteryDto getMasteryStatus(String userIdentifier, String slug) {
        if (userIdentifier == null || userIdentifier.isBlank()) {
            return new AlgorithmMasteryDto(slug, false, null, 0, false);
        }

        User user = userRepository.findByEmail(userIdentifier)
                .or(() -> userRepository.findByUsername(userIdentifier))
                .orElse(null);

        if (user == null) {
            return new AlgorithmMasteryDto(slug, false, null, 0, false);
        }

        return masteryRepository.findByUserIdAndAlgorithmSlug(user.getId(), slug)
                .map(m -> new AlgorithmMasteryDto(slug, m.isMastered(), m.getMasteredAt(), m.getXpAwarded(), false))
                .orElseGet(() -> new AlgorithmMasteryDto(slug, false, null, 0, false));
    }

    public AlgorithmMasteryDto toggleMastery(String userIdentifier, String slug) {
        User user = userRepository.findByEmail(userIdentifier)
                .or(() -> userRepository.findByUsername(userIdentifier))
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userIdentifier));

        Algorithm algorithm = algorithmRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Algorithm not found with slug: " + slug));

        UserAlgorithmMastery mastery = masteryRepository.findByUserIdAndAlgorithmId(user.getId(), algorithm.getId())
                .orElseGet(() -> new UserAlgorithmMastery(user, algorithm, false));

        boolean wasMastered = mastery.isMastered();
        boolean newMasteredState = !wasMastered;
        boolean newlyMastered = false;
        int xpAwarded = 0;

        mastery.setMastered(newMasteredState);
        if (newMasteredState && !wasMastered) {
            newlyMastered = true;
            xpAwarded = 100;
            mastery.setXpAwarded(xpAwarded);
            // Award +100 XP bonus for algorithm mastery
            gamificationService.awardXp(
                    user,
                    100,
                    "ALGORITHM_MASTERY",
                    "Mastered algorithm: " + algorithm.getName()
            );
        }

        masteryRepository.save(mastery);

        return new AlgorithmMasteryDto(
                slug,
                mastery.isMastered(),
                mastery.getMasteredAt(),
                xpAwarded,
                newlyMastered
        );
    }
}
