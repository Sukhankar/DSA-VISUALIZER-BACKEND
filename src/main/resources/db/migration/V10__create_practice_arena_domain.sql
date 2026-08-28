-- V10__create_practice_arena_domain.sql
-- Create practice arena domain tables for daily challenges and practice sessions

CREATE TABLE IF NOT EXISTS daily_challenges (
    id UUID PRIMARY KEY,
    challenge_date DATE NOT NULL UNIQUE,
    problem_id UUID NOT NULL REFERENCES problems(id) ON DELETE CASCADE,
    bonus_xp INT NOT NULL DEFAULT 100,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_daily_challenges (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    daily_challenge_id UUID NOT NULL REFERENCES daily_challenges(id) ON DELETE CASCADE,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    submission_id UUID REFERENCES problem_submissions(id) ON DELETE SET NULL,
    completed_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT uk_user_daily_challenge UNIQUE (user_id, daily_challenge_id)
);

CREATE TABLE IF NOT EXISTS practice_sessions (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    mode VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'IN_PROGRESS',
    difficulty VARCHAR(20),
    category_id UUID REFERENCES algorithm_categories(id) ON DELETE SET NULL,
    time_limit_seconds INT,
    total_problems INT NOT NULL DEFAULT 0,
    solved_problems INT NOT NULL DEFAULT 0,
    score INT NOT NULL DEFAULT 0,
    xp_earned INT NOT NULL DEFAULT 0,
    started_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE IF NOT EXISTS practice_session_problems (
    id UUID PRIMARY KEY,
    session_id UUID NOT NULL REFERENCES practice_sessions(id) ON DELETE CASCADE,
    problem_id UUID NOT NULL REFERENCES problems(id) ON DELETE CASCADE,
    order_index INT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'UNATTEMPTED',
    submission_id UUID REFERENCES problem_submissions(id) ON DELETE SET NULL,
    solved_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT uk_session_problem_order UNIQUE (session_id, order_index)
);

CREATE INDEX idx_daily_challenges_date ON daily_challenges(challenge_date);
CREATE INDEX idx_user_daily_challenges_user ON user_daily_challenges(user_id);
CREATE INDEX idx_practice_sessions_user ON practice_sessions(user_id, status);
CREATE INDEX idx_practice_session_problems_session ON practice_session_problems(session_id);
