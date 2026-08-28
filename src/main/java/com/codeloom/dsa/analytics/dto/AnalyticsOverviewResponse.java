package com.codeloom.dsa.analytics.dto;

import java.util.List;

public record AnalyticsOverviewResponse(
        UserStreakDto userStreak,
        UserXpDto userXp,
        long totalBadgesUnlocked,
        long totalBadgesAvailable,
        List<BadgeDto> recentBadges,
        List<TopicSkillDto> topicSkills
) {}
