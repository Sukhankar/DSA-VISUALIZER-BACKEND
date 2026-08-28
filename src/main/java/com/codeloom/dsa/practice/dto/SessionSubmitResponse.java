package com.codeloom.dsa.practice.dto;

import com.codeloom.dsa.problem.dto.SubmissionResponse;

public record SessionSubmitResponse(
        SubmissionResponse submission,
        PracticeSessionDto session,
        boolean sessionCompleted,
        int xpEarnedInAttempt
) {}
