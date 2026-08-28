package com.codeloom.dsa.analytics.controller;

import com.codeloom.dsa.analytics.dto.*;
import com.codeloom.dsa.analytics.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/overview")
    public ResponseEntity<AnalyticsOverviewResponse> getOverview(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        AnalyticsOverviewResponse response = analyticsService.getAnalyticsOverview(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/heatmap")
    public ResponseEntity<List<DailyActivityDto>> getActivityHeatmap(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        List<DailyActivityDto> response = analyticsService.getActivityHeatmap(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/skills")
    public ResponseEntity<List<TopicSkillDto>> getTopicSkills(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        List<TopicSkillDto> response = analyticsService.getTopicSkills(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/badges")
    public ResponseEntity<List<BadgeDto>> getBadges(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        List<BadgeDto> response = analyticsService.getBadges(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<LeaderboardUserDto>> getLeaderboard(
            @RequestParam(defaultValue = "10") int limit
    ) {
        List<LeaderboardUserDto> response = analyticsService.getLeaderboard(limit);
        return ResponseEntity.ok(response);
    }
}
