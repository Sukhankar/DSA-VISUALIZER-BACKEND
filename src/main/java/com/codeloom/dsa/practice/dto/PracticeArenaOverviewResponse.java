package com.codeloom.dsa.practice.dto;

import com.codeloom.dsa.analytics.dto.UserStreakDto;
import com.codeloom.dsa.analytics.dto.UserXpDto;

import java.util.List;

public record PracticeArenaOverviewResponse(
        DailyChallengeDto dailyChallenge,
        UserStreakDto streak,
        UserXpDto xp,
        PracticeSessionDto activeSession,
        long totalCompletedSessions,
        List<PracticeSessionDto> recentSessions
) {}
