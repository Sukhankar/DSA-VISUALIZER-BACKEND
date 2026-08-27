package com.codeloom.dsa.progress.controller;

import com.codeloom.dsa.progress.dto.FavoriteAlgorithmResponse;
import com.codeloom.dsa.progress.service.UserFavoriteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/me/favorites")
public class UserFavoriteController {

    private final UserFavoriteService userFavoriteService;

    public UserFavoriteController(UserFavoriteService userFavoriteService) {
        this.userFavoriteService = userFavoriteService;
    }

    @PostMapping("/{algorithmSlug}")
    public ResponseEntity<FavoriteAlgorithmResponse> addFavorite(
            Authentication authentication,
            @PathVariable String algorithmSlug
    ) {
        FavoriteAlgorithmResponse response = userFavoriteService.addFavorite(authentication.getName(), algorithmSlug);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{algorithmSlug}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFavorite(
            Authentication authentication,
            @PathVariable String algorithmSlug
    ) {
        userFavoriteService.removeFavorite(authentication.getName(), algorithmSlug);
    }

    @GetMapping
    public ResponseEntity<List<FavoriteAlgorithmResponse>> listFavorites(
            Authentication authentication
    ) {
        List<FavoriteAlgorithmResponse> response = userFavoriteService.listFavorites(authentication.getName());
        return ResponseEntity.ok(response);
    }
}
