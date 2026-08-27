-- Phase 7: User Progress & Favorites Migration

CREATE TABLE user_favorites (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    algorithm_id UUID NOT NULL REFERENCES algorithms(id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_user_favorites_user_algorithm UNIQUE (user_id, algorithm_id)
);

CREATE INDEX idx_user_favorites_user_id ON user_favorites(user_id);
CREATE INDEX idx_user_favorites_algorithm_id ON user_favorites(algorithm_id);

CREATE TABLE user_algorithm_progress (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    algorithm_id UUID NOT NULL REFERENCES algorithms(id) ON DELETE CASCADE,
    status VARCHAR(20) NOT NULL DEFAULT 'NOT_STARTED',
    progress_percentage INT NOT NULL DEFAULT 0,
    last_step INT,
    started_at TIMESTAMP WITH TIME ZONE,
    completed_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_user_algorithm_progress_user_algorithm UNIQUE (user_id, algorithm_id)
);

CREATE INDEX idx_user_algorithm_progress_user_id ON user_algorithm_progress(user_id);
CREATE INDEX idx_user_algorithm_progress_algorithm_id ON user_algorithm_progress(algorithm_id);
CREATE INDEX idx_user_algorithm_progress_status ON user_algorithm_progress(status);
