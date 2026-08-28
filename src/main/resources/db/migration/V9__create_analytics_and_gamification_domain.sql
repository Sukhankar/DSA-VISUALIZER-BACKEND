-- ==============================================================================
-- Flyway Migration V9: Learning Analytics, Streaks, XP, Badges & Rewards Domain
-- ==============================================================================

-- 1. Create user_streaks table
CREATE TABLE IF NOT EXISTS user_streaks (
    user_id UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    current_streak INT NOT NULL DEFAULT 0,
    longest_streak INT NOT NULL DEFAULT 0,
    last_activity_date DATE,
    streak_freeze_count INT NOT NULL DEFAULT 0,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 2. Create user_xp table
CREATE TABLE IF NOT EXISTS user_xp (
    user_id UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    total_xp INT NOT NULL DEFAULT 0,
    current_level INT NOT NULL DEFAULT 1,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 3. Create xp_ledger table
CREATE TABLE IF NOT EXISTS xp_ledger (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    amount INT NOT NULL,
    source VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 4. Create badges table
CREATE TABLE IF NOT EXISTS badges (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    icon_name VARCHAR(50) NOT NULL,
    category VARCHAR(50) NOT NULL,
    xp_reward INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 5. Create user_badges table
CREATE TABLE IF NOT EXISTS user_badges (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    badge_id UUID NOT NULL REFERENCES badges(id) ON DELETE CASCADE,
    unlocked_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_user_badge UNIQUE(user_id, badge_id)
);

-- 6. Create user_daily_activity table
CREATE TABLE IF NOT EXISTS user_daily_activity (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    activity_date DATE NOT NULL,
    algorithms_viewed_count INT NOT NULL DEFAULT 0,
    problems_solved_count INT NOT NULL DEFAULT 0,
    xp_earned INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_user_daily_activity UNIQUE(user_id, activity_date)
);

-- Indexes for performance
CREATE INDEX IF NOT EXISTS idx_xp_ledger_user ON xp_ledger(user_id);
CREATE INDEX IF NOT EXISTS idx_user_badges_user ON user_badges(user_id);
CREATE INDEX IF NOT EXISTS idx_user_daily_activity_user_date ON user_daily_activity(user_id, activity_date);
CREATE INDEX IF NOT EXISTS idx_user_xp_total ON user_xp(total_xp DESC);

-- ==============================================================================
-- 7. Seed Default Gamification Badges
-- ==============================================================================
INSERT INTO badges (code, name, description, icon_name, category, xp_reward) VALUES
('FIRST_STEP', 'First Step', 'Completed your first algorithm visualization.', 'Footprints', 'LEARNING', 50),
('CODE_ROOKIE', 'Code Rookie', 'Solved your first coding problem with an accepted solution.', 'Award', 'PRACTICE', 100),
('STREAK_7', 'Week Warrior', 'Maintained an active 7-day learning streak.', 'Flame', 'STREAK', 250),
('STREAK_30', 'Monthly Titan', 'Maintained an active 30-day learning streak.', 'Zap', 'STREAK', 1000),
('ARRAY_MASTER', 'Array Master', 'Solved 3 or more array problems.', 'Grid', 'SKILL', 300),
('SORTING_EXPERT', 'Sorting Expert', 'Explored sorting algorithms and solved sorting challenges.', 'ArrowUpDown', 'SKILL', 300),
('CENTURION', 'XP Centurion', 'Earned 1,000 total XP points.', 'Crown', 'MILESTONE', 500)
ON CONFLICT (code) DO NOTHING;
