-- =====================================================================
-- Migration: V16__add_algorithm_mastery.sql
-- Description: Create user_algorithm_mastery table for Phase 18 mastery tracking
-- =====================================================================

CREATE TABLE IF NOT EXISTS user_algorithm_mastery (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    algorithm_id UUID NOT NULL REFERENCES algorithms(id) ON DELETE CASCADE,
    mastered BOOLEAN NOT NULL DEFAULT FALSE,
    mastered_at TIMESTAMP WITH TIME ZONE,
    xp_awarded INTEGER NOT NULL DEFAULT 100,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_user_algorithm_mastery UNIQUE (user_id, algorithm_id)
);

CREATE INDEX IF NOT EXISTS idx_user_algo_mastery_user ON user_algorithm_mastery(user_id);
CREATE INDEX IF NOT EXISTS idx_user_algo_mastery_algo ON user_algorithm_mastery(algorithm_id);
