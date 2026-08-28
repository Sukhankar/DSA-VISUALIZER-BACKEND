-- Phase 17: Learning Paths, Preferences, Module Linkages Migration

-- 1. Create learning_paths table
CREATE TABLE IF NOT EXISTS learning_paths (
    id UUID PRIMARY KEY,
    slug VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    difficulty VARCHAR(50) NOT NULL DEFAULT 'BEGINNER',
    estimated_duration VARCHAR(50) DEFAULT '24 hours',
    display_order INT NOT NULL DEFAULT 1,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 2. Create user_learning_preferences table
CREATE TABLE IF NOT EXISTS user_learning_preferences (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    experience_level VARCHAR(50) NOT NULL DEFAULT 'BEGINNER',
    preferred_language VARCHAR(50) DEFAULT 'Java',
    daily_learning_minutes INT DEFAULT 30,
    primary_goal VARCHAR(100) DEFAULT 'LEARN_DSA',
    completed_assessment BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_user_learning_preferences UNIQUE (user_id)
);

-- 3. Create roadmap_module_prerequisites table
CREATE TABLE IF NOT EXISTS roadmap_module_prerequisites (
    module_id UUID NOT NULL REFERENCES roadmap_modules(id) ON DELETE CASCADE,
    prerequisite_module_id UUID NOT NULL REFERENCES roadmap_modules(id) ON DELETE CASCADE,
    PRIMARY KEY (module_id, prerequisite_module_id)
);

-- 4. Create roadmap_module_algorithms table
CREATE TABLE IF NOT EXISTS roadmap_module_algorithms (
    module_id UUID NOT NULL REFERENCES roadmap_modules(id) ON DELETE CASCADE,
    algorithm_id UUID NOT NULL REFERENCES algorithms(id) ON DELETE CASCADE,
    display_order INT NOT NULL DEFAULT 1,
    PRIMARY KEY (module_id, algorithm_id)
);

-- 5. Create roadmap_module_problems table
CREATE TABLE IF NOT EXISTS roadmap_module_problems (
    module_id UUID NOT NULL REFERENCES roadmap_modules(id) ON DELETE CASCADE,
    problem_id UUID NOT NULL REFERENCES problems(id) ON DELETE CASCADE,
    display_order INT NOT NULL DEFAULT 1,
    PRIMARY KEY (module_id, problem_id)
);

-- 6. Add columns to roadmap_modules and user_roadmap_progress
ALTER TABLE roadmap_modules ADD COLUMN IF NOT EXISTS learning_path_id UUID REFERENCES learning_paths(id);

ALTER TABLE user_roadmap_progress ADD COLUMN IF NOT EXISTS algorithm_progress INT DEFAULT 0;
ALTER TABLE user_roadmap_progress ADD COLUMN IF NOT EXISTS problem_progress INT DEFAULT 0;
ALTER TABLE user_roadmap_progress ADD COLUMN IF NOT EXISTS overall_progress INT DEFAULT 0;
ALTER TABLE user_roadmap_progress ADD COLUMN IF NOT EXISTS started_at TIMESTAMP WITHOUT TIME ZONE;
ALTER TABLE user_roadmap_progress ADD COLUMN IF NOT EXISTS last_activity_at TIMESTAMP WITHOUT TIME ZONE;

-- 7. Seed Learning Paths
INSERT INTO learning_paths (id, slug, name, description, difficulty, estimated_duration, display_order)
VALUES
('20000000-0000-0000-0000-000000000001', 'dsa-beginner', 'DSA Beginner Path', 'A structured learning journey designed for beginners to build strong data structures and algorithms fundamentals step by step.', 'BEGINNER', '24 hours', 1),
('20000000-0000-0000-0000-000000000002', 'dsa-intermediate', 'DSA Intermediate Path', 'Master non-linear data structures, complex recursion, tree traversals, and heap queues.', 'INTERMEDIATE', '36 hours', 2),
('20000000-0000-0000-0000-000000000003', 'dsa-advanced', 'DSA Advanced Path', 'Deep dive into graph algorithms, shortest paths, dynamic programming, and segment trees.', 'ADVANCED', '48 hours', 3),
('20000000-0000-0000-0000-000000000004', 'interview-preparation', 'Interview Preparation Path', 'High-frequency LeetCode patterns for top tech company coding assessments.', 'INTERMEDIATE', '30 hours', 4)
ON CONFLICT (slug) DO NOTHING;

-- Link existing beginner modules to dsa-beginner path
UPDATE roadmap_modules SET learning_path_id = '20000000-0000-0000-0000-000000000001' WHERE learning_path_id IS NULL;

-- 8. Seed missing modules for full 12-module beginner progression
INSERT INTO roadmap_modules (id, slug, title, description, order_index, tier, icon_name, category_slug, xp_reward, learning_path_id)
VALUES
('10000000-0000-0000-0000-000000000000', 'programming-fundamentals', 'Programming Fundamentals', 'Basic control flow, variables, functions, and memory layout concepts.', 0, 'BEGINNER', 'code', 'arrays', 100, '20000000-0000-0000-0000-000000000001'),
('10000000-0000-0000-0000-000000000007b', 'recursion', 'Recursion & Backtracking', 'Base cases, recursive call stacks, call stack depth, and state space trees.', 6.5, 'INTERMEDIATE', 'repeat', 'searching', 200, '20000000-0000-0000-0000-000000000001'),
('10000000-0000-0000-0000-000000000006b', 'heaps', 'Heaps & Priority Queues', 'Min-heaps, max-heaps, heapify operations, and priority queue applications.', 6.8, 'INTERMEDIATE', 'server', 'trees', 250, '20000000-0000-0000-0000-000000000001'),
('10000000-0000-0000-0000-000000000009', 'advanced-problem-solving', 'Advanced Problem Solving', 'Segment trees, trie structures, bit manipulation, and competitive DSA.', 9, 'ADVANCED', 'award', 'dynamic-programming', 400, '20000000-0000-0000-0000-000000000001')
ON CONFLICT (slug) DO NOTHING;

-- Populate roadmap_module_algorithms links with existing algorithms
INSERT INTO roadmap_module_algorithms (module_id, algorithm_id, display_order)
SELECT m.id, a.id, ROW_NUMBER() OVER (PARTITION BY m.id ORDER BY a.name)
FROM roadmap_modules m
JOIN algorithms a ON a.category_id = (SELECT id FROM algorithm_categories WHERE slug = m.category_slug LIMIT 1)
ON CONFLICT DO NOTHING;

-- Populate roadmap_module_problems links with existing problems
INSERT INTO roadmap_module_problems (module_id, problem_id, display_order)
SELECT m.id, p.id, ROW_NUMBER() OVER (PARTITION BY m.id ORDER BY p.title)
FROM roadmap_modules m
JOIN problems p ON p.category_slug = m.category_slug
ON CONFLICT DO NOTHING;
