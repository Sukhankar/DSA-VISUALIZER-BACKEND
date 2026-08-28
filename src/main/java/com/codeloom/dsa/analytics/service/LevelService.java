package com.codeloom.dsa.analytics.service;

import com.codeloom.dsa.analytics.dto.LevelProgressDto;
import org.springframework.stereotype.Service;

@Service
public class LevelService {

    /**
     * XP required to reach level N — steeper exponential curve.
     *
     * Level 1  :      0 XP  (start)
     * Level 2  :    250 XP
     * Level 3  :    600 XP
     * Level 4  :  1,100 XP
     * Level 5  :  1,800 XP
     * Level 6  :  2,700 XP
     * Level 7  :  3,900 XP
     * Level 8  :  5,400 XP
     * Level 9  :  7,200 XP
     * Level 10 :  9,500 XP
     * Level 20 : ~50,000 XP
     * Level 50 : ~500,000 XP
     *
     * Formula (level >= 2): floor(250 * (level - 1)^1.8)
     */
    public int calculateXpRequiredForLevel(int level) {
        if (level <= 1) return 0;
        return (int) Math.floor(250.0 * Math.pow(level - 1, 1.8));
    }

    public int calculateLevel(int totalXp) {
        if (totalXp <= 0) return 1;
        int level = 1;
        while (totalXp >= calculateXpRequiredForLevel(level + 1)) {
            level++;
            // Safety cap at level 100
            if (level >= 100) break;
        }
        return level;
    }

    public LevelProgressDto getLevelProgress(int totalXp) {
        int currentLevel = calculateLevel(totalXp);
        int xpForCurrentLevel = calculateXpRequiredForLevel(currentLevel);
        int xpForNextLevel = calculateXpRequiredForLevel(currentLevel + 1);

        int xpInCurrentLevel = Math.max(0, totalXp - xpForCurrentLevel);
        int xpNeededForNextLevel = Math.max(1, xpForNextLevel - xpForCurrentLevel);

        double progress = ((double) xpInCurrentLevel / xpNeededForNextLevel) * 100.0;
        double clampedProgress = Math.min(100.0, Math.max(0.0, progress));

        return new LevelProgressDto(
                currentLevel,
                totalXp,
                xpForCurrentLevel,
                xpForNextLevel,
                xpInCurrentLevel,
                xpNeededForNextLevel - xpInCurrentLevel,
                Math.round(clampedProgress * 10.0) / 10.0
        );
    }
}
