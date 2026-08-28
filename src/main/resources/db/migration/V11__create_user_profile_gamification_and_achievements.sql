-- ==============================================================================
-- Flyway Migration V11: User Profile, Gamification, Achievements, Levels & Rewards
-- ==============================================================================

-- 1. Create user_profiles table
CREATE TABLE IF NOT EXISTS user_profiles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    username VARCHAR(100) NOT NULL,
    display_name VARCHAR(100),
    bio TEXT,
    avatar_url TEXT,
    country VARCHAR(50),
    github_url TEXT,
    linkedin_url TEXT,
    total_xp INT NOT NULL DEFAULT 0,
    current_level INT NOT NULL DEFAULT 1,
    current_streak INT NOT NULL DEFAULT 0,
    longest_streak INT NOT NULL DEFAULT 0,
    total_problems_solved INT NOT NULL DEFAULT 0,
    total_algorithms_completed INT NOT NULL DEFAULT 0,
    total_practice_sessions INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 2. Create user_activities table
CREATE TABLE IF NOT EXISTS user_activities (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    activity_type VARCHAR(50) NOT NULL,
    reference_type VARCHAR(50),
    reference_id VARCHAR(100),
    xp_earned INT NOT NULL DEFAULT 0,
    metadata TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 3. Create achievements table
CREATE TABLE IF NOT EXISTS achievements (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    category VARCHAR(50) NOT NULL,
    icon VARCHAR(50) NOT NULL,
    rarity VARCHAR(50) NOT NULL DEFAULT 'COMMON',
    xp_reward INT NOT NULL DEFAULT 0,
    requirement_type VARCHAR(50) NOT NULL,
    requirement_value INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 4. Create user_achievements table
CREATE TABLE IF NOT EXISTS user_achievements (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    achievement_id UUID NOT NULL REFERENCES achievements(id) ON DELETE CASCADE,
    unlocked_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_user_achievement UNIQUE(user_id, achievement_id)
);

-- 5. Alter badges table to add rarity, unlock_type, unlock_value
ALTER TABLE badges ADD COLUMN IF NOT EXISTS rarity VARCHAR(50) NOT NULL DEFAULT 'COMMON';
ALTER TABLE badges ADD COLUMN IF NOT EXISTS unlock_type VARCHAR(50);
ALTER TABLE badges ADD COLUMN IF NOT EXISTS unlock_value INT;

-- 6. Create user_xp_transactions table with idempotency protection
CREATE TABLE IF NOT EXISTS user_xp_transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    amount INT NOT NULL,
    reason VARCHAR(100) NOT NULL,
    reference_type VARCHAR(50),
    reference_id VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_user_xp_transaction UNIQUE(user_id, reason, reference_type, reference_id)
);

-- Performance Indexes
CREATE INDEX IF NOT EXISTS idx_user_profiles_user ON user_profiles(user_id);
CREATE INDEX IF NOT EXISTS idx_user_activities_user_created ON user_activities(user_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_user_achievements_user ON user_achievements(user_id);
CREATE INDEX IF NOT EXISTS idx_user_xp_trans_user ON user_xp_transactions(user_id);

-- ==============================================================================
-- 7. Seed Achievements (25+ across categories)
-- ==============================================================================
INSERT INTO achievements (code, name, description, category, icon, rarity, xp_reward, requirement_type, requirement_value) VALUES
-- LEARNING
('FIRST_ALGORITHM', 'First Algorithm', 'Completed your first algorithm study topic.', 'LEARNING', 'BookOpen', 'COMMON', 50, 'ALGORITHMS_COMPLETED', 1),
('ALGORITHM_EXPLORER', 'Algorithm Explorer', 'Completed 5 algorithm study topics.', 'LEARNING', 'Compass', 'RARE', 150, 'ALGORITHMS_COMPLETED', 5),
('ALGORITHM_MASTER', 'Algorithm Master', 'Completed 15 algorithm study topics.', 'LEARNING', 'GraduationCap', 'EPIC', 400, 'ALGORITHMS_COMPLETED', 15),
('KNOWLEDGE_SEEKER', 'Knowledge Seeker', 'Completed 30 algorithm study topics.', 'LEARNING', 'Brain', 'LEGENDARY', 1000, 'ALGORITHMS_COMPLETED', 30),

-- PRACTICE
('FIRST_PROBLEM', 'First Code Solved', 'Solved your first coding practice problem.', 'PRACTICE', 'Code', 'COMMON', 100, 'PROBLEMS_SOLVED', 1),
('TEN_PROBLEMS', 'Problem Decathlete', 'Solved 10 coding practice problems.', 'PRACTICE', 'Target', 'COMMON', 200, 'PROBLEMS_SOLVED', 10),
('FIFTY_PROBLEMS', 'Half Century Solver', 'Solved 50 coding practice problems.', 'PRACTICE', 'ShieldCheck', 'RARE', 500, 'PROBLEMS_SOLVED', 50),
('HUNDRED_PROBLEMS', 'Centurion Coder', 'Solved 100 coding practice problems.', 'PRACTICE', 'Trophy', 'EPIC', 1000, 'PROBLEMS_SOLVED', 100),
('PRACTICE_WARRIOR', 'Practice Warrior', 'Completed 10 practice arena sessions.', 'PRACTICE', 'Swords', 'RARE', 300, 'PRACTICE_SESSIONS', 10),
('PRACTICE_MASTER', 'Practice Master', 'Completed 50 practice arena sessions.', 'PRACTICE', 'Award', 'EPIC', 800, 'PRACTICE_SESSIONS', 50),

-- STREAK
('STREAK_STARTER', 'Streak Starter', 'Maintained a 3-day continuous practice streak.', 'STREAK', 'Flame', 'COMMON', 100, 'CURRENT_STREAK', 3),
('WEEK_WARRIOR_ACH', 'Week Warrior', 'Maintained a 7-day continuous practice streak.', 'STREAK', 'Zap', 'RARE', 250, 'CURRENT_STREAK', 7),
('FORTNIGHT_CHAMP', 'Fortnight Champion', 'Maintained a 14-day continuous practice streak.', 'STREAK', 'Shield', 'RARE', 500, 'CURRENT_STREAK', 14),
('MONTHLY_TITAN_ACH', 'Monthly Titan', 'Maintained a 30-day continuous practice streak.', 'STREAK', 'Crown', 'EPIC', 1000, 'CURRENT_STREAK', 30),
('CENTURY_STREAK', 'Legendary Streak', 'Maintained a 100-day continuous practice streak.', 'STREAK', 'Flame', 'LEGENDARY', 2500, 'CURRENT_STREAK', 100),

-- CHALLENGE
('DAILY_CHALLENGER', 'Daily Challenger', 'Completed 1 Daily Practice Challenge.', 'CHALLENGE', 'Calendar', 'COMMON', 100, 'DAILY_CHALLENGES', 1),
('WEEKLY_CHAMPION', 'Weekly Challenge Master', 'Completed 7 Daily Practice Challenges.', 'CHALLENGE', 'Star', 'RARE', 350, 'DAILY_CHALLENGES', 7),
('DAILY_MASTER', 'Daily Legend', 'Completed 30 Daily Practice Challenges.', 'CHALLENGE', 'Medal', 'EPIC', 1000, 'DAILY_CHALLENGES', 30),

-- VISUALIZATION
('FIRST_VISUALIZATION', 'Visual Learner', 'Completed your first algorithm step visualization.', 'VISUALIZATION', 'Eye', 'COMMON', 50, 'VISUALIZATIONS', 1),
('VISUALIZATION_EXPLORER', 'Visual Explorer', 'Executed 10 different algorithm step visualizations.', 'VISUALIZATION', 'Play', 'RARE', 200, 'VISUALIZATIONS', 10),
('VISUALIZATION_MASTER', 'Visual Genius', 'Executed 50 algorithm step visualizations.', 'VISUALIZATION', 'Layers', 'EPIC', 600, 'VISUALIZATIONS', 50),

-- XP & LEVEL
('XP_COLLECTOR', 'XP Collector', 'Earned 1,000 total XP points.', 'XP_COLLECTOR', 'Sparkles', 'COMMON', 200, 'TOTAL_XP', 1000),
('XP_EXPERT', 'XP Expert', 'Earned 5,000 total XP points.', 'XP_COLLECTOR', 'Sparkles', 'RARE', 500, 'TOTAL_XP', 5000),

('LEVEL_10', 'Level 10 Achiever', 'Reached User Level 10.', 'LEVEL', 'ArrowUpCircle', 'RARE', 300, 'LEVEL', 10),
('LEVEL_25', 'Level 25 Achiever', 'Reached User Level 25.', 'LEVEL', 'Award', 'EPIC', 750, 'LEVEL', 25),
('LEVEL_50', 'Level 50 Grandmaster', 'Reached User Level 50.', 'LEVEL', 'Crown', 'LEGENDARY', 2000, 'LEVEL', 50)
ON CONFLICT (code) DO NOTHING;

-- ==============================================================================
-- 8. Seed Additional Badges (15+ total)
-- ==============================================================================
INSERT INTO badges (code, name, description, icon_name, category, rarity, xp_reward, unlock_type, unlock_value) VALUES
('STREAK_STARTER', 'Streak Starter', 'Started your learning streak journey.', 'Flame', 'STREAK', 'COMMON', 100, 'CURRENT_STREAK', 3),
('WEEK_WARRIOR', 'Week Warrior', 'Maintained 7 consecutive active days.', 'Zap', 'STREAK', 'RARE', 250, 'CURRENT_STREAK', 7),
('MONTH_MASTER', 'Month Master', 'Maintained 30 consecutive active days.', 'Crown', 'STREAK', 'EPIC', 1000, 'CURRENT_STREAK', 30),
('PROBLEM_SOLVER', 'Problem Solver', 'Solved 50 coding problems.', 'Target', 'PRACTICE', 'EPIC', 500, 'PROBLEMS_SOLVED', 50),
('PRACTICE_MACHINE', 'Practice Machine', 'Completed 25 practice arena sessions.', 'Swords', 'PRACTICE', 'RARE', 400, 'PRACTICE_SESSIONS', 25),
('DAILY_CHAMPION', 'Daily Champion', 'Completed 15 daily practice challenges.', 'Calendar', 'CHALLENGE', 'RARE', 450, 'DAILY_CHALLENGES', 15),
('VISUAL_LEARNER', 'Visual Learner', 'Ran 20 algorithm visualizations.', 'Eye', 'VISUALIZATION', 'RARE', 250, 'VISUALIZATIONS', 20),
('LEVEL_ROOKIE', 'Level Rookie', 'Reached Level 5.', 'Star', 'MILESTONE', 'COMMON', 150, 'LEVEL', 5),
('LEVEL_VETERAN', 'Level Veteran', 'Reached Level 20.', 'Shield', 'MILESTONE', 'EPIC', 600, 'LEVEL', 20)
ON CONFLICT (code) DO NOTHING;
