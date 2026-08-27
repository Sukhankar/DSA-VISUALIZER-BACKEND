package com.codeloom.dsa.progress.controller;

import com.codeloom.dsa.progress.dto.FavoriteAlgorithmResponse;
import com.codeloom.dsa.progress.service.UserFavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/me/favorites")
@Tag(name = "User Favorites", description = "User algorithm favorites management APIs")
@SecurityRequirement(name = "bearerAuth")
public class UserFavoriteController {

    private final UserFavoriteService userFavoriteService;

    public UserFavoriteController(UserFavoriteService userFavoriteService) {
        this.userFavoriteService = userFavoriteService;
    }

    @PostMapping("/{algorithmSlug}")
    @Operation(summary = "Add algorithm to favorites", description = "Bookmarks an algorithm for the currently authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Algorithm added to favorites"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Algorithm not found"),
            @ApiResponse(responseCode = "409", description = "Algorithm is already in user favorites")
    })
    public ResponseEntity<FavoriteAlgorithmResponse> addFavorite(
            Authentication authentication,
            @Parameter(description = "Algorithm slug (e.g. 'bubble-sort')")
            @PathVariable String algorithmSlug
    ) {
        FavoriteAlgorithmResponse response = userFavoriteService.addFavorite(authentication.getName(), algorithmSlug);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{algorithmSlug}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove algorithm from favorites", description = "Removes a bookmarked algorithm from the user's favorites.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Favorite removed successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Algorithm or favorite record not found")
    })
    public void removeFavorite(
            Authentication authentication,
            @Parameter(description = "Algorithm slug (e.g. 'bubble-sort')")
            @PathVariable String algorithmSlug
    ) {
        userFavoriteService.removeFavorite(authentication.getName(), algorithmSlug);
    }

    @GetMapping
    @Operation(summary = "List user favorites", description = "Retrieves all favorited algorithms for the currently authenticated user in reverse chronological order.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Favorites list retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<FavoriteAlgorithmResponse>> listFavorites(
            Authentication authentication
    ) {
        List<FavoriteAlgorithmResponse> response = userFavoriteService.listFavorites(authentication.getName());
        return ResponseEntity.ok(response);
    }
}
