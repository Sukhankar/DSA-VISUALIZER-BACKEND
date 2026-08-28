package com.codeloom.dsa.analytics.dto;

import java.time.LocalDate;

public record DailyActivityDto(
        LocalDate date,
        int count,
        int xpEarned
) {}
