package com.codeloom.dsa.progress.service;

import com.codeloom.dsa.algorithm.entity.Algorithm;
import com.codeloom.dsa.algorithm.repository.AlgorithmRepository;
import com.codeloom.dsa.common.exception.ResourceNotFoundException;
import com.codeloom.dsa.progress.dto.FavoriteAlgorithmResponse;
import com.codeloom.dsa.progress.entity.UserFavorite;
import com.codeloom.dsa.progress.repository.UserFavoriteRepository;
import com.codeloom.dsa.user.entity.User;
import com.codeloom.dsa.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserFavoriteService {

    private final UserRepository userRepository;
    private final AlgorithmRepository algorithmRepository;
    private final UserFavoriteRepository userFavoriteRepository;

    public UserFavoriteService(
            UserRepository userRepository,
            AlgorithmRepository algorithmRepository,
            UserFavoriteRepository userFavoriteRepository
    ) {
        this.userRepository = userRepository;
        this.algorithmRepository = algorithmRepository;
        this.userFavoriteRepository = userFavoriteRepository;
    }

    @Transactional
    public FavoriteAlgorithmResponse addFavorite(String userEmail, String algorithmSlug) {
        User user = getUserByEmail(userEmail);
        Algorithm algorithm = getAlgorithmBySlug(algorithmSlug);

        if (userFavoriteRepository.existsByUserIdAndAlgorithmId(user.getId(), algorithm.getId())) {
            throw new IllegalStateException("Algorithm is already favorited");
        }

        UserFavorite favorite = new UserFavorite(user, algorithm);
        UserFavorite savedFavorite = userFavoriteRepository.save(favorite);

        return mapToFavoriteResponse(savedFavorite);
    }

    @Transactional
    public void removeFavorite(String userEmail, String algorithmSlug) {
        User user = getUserByEmail(userEmail);
        Algorithm algorithm = getAlgorithmBySlug(algorithmSlug);

        UserFavorite favorite = userFavoriteRepository.findByUserIdAndAlgorithmId(user.getId(), algorithm.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Favorite not found for algorithm: " + algorithmSlug));

        userFavoriteRepository.delete(favorite);
    }

    public List<FavoriteAlgorithmResponse> listFavorites(String userEmail) {
        User user = getUserByEmail(userEmail);
        return userFavoriteRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::mapToFavoriteResponse)
                .toList();
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    private Algorithm getAlgorithmBySlug(String slug) {
        return algorithmRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Algorithm not found with slug: " + slug));
    }

    private FavoriteAlgorithmResponse mapToFavoriteResponse(UserFavorite favorite) {
        Algorithm algo = favorite.getAlgorithm();
        return new FavoriteAlgorithmResponse(
                algo.getId(),
                algo.getName(),
                algo.getSlug(),
                algo.getDifficulty().name(),
                algo.getCategory().getSlug(),
                favorite.getCreatedAt()
        );
    }
}
