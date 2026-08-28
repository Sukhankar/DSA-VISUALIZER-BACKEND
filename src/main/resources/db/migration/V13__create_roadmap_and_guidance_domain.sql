-- Phase 17: Learning Roadmap & Beginner Guidance System Migration

CREATE TABLE IF NOT EXISTS roadmap_modules (
    id UUID PRIMARY KEY,
    slug VARCHAR(100) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    order_index INT NOT NULL,
    tier VARCHAR(50) NOT NULL,
    icon_name VARCHAR(50) DEFAULT 'book',
    category_slug VARCHAR(100),
    prerequisite_module_id UUID REFERENCES roadmap_modules(id),
    xp_reward INT DEFAULT 100,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS roadmap_steps (
    id UUID PRIMARY KEY,
    module_id UUID NOT NULL REFERENCES roadmap_modules(id) ON DELETE CASCADE,
    step_number INT NOT NULL,
    step_type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    reference_slug VARCHAR(100),
    xp_reward INT DEFAULT 20,
    CONSTRAINT uk_module_step_number UNIQUE (module_id, step_number)
);

CREATE TABLE IF NOT EXISTS user_roadmap_progress (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    module_id UUID NOT NULL REFERENCES roadmap_modules(id) ON DELETE CASCADE,
    status VARCHAR(50) NOT NULL DEFAULT 'LOCKED',
    completion_percentage INT DEFAULT 0,
    unlocked_at TIMESTAMP WITHOUT TIME ZONE,
    completed_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_user_module_progress UNIQUE (user_id, module_id)
);

CREATE TABLE IF NOT EXISTS user_assessments (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    experience_level VARCHAR(50) NOT NULL,
    preferred_language VARCHAR(50),
    knows_arrays BOOLEAN DEFAULT FALSE,
    knows_sorting BOOLEAN DEFAULT FALSE,
    knows_trees BOOLEAN DEFAULT FALSE,
    solved_problems_before BOOLEAN DEFAULT FALSE,
    goal VARCHAR(255),
    recommended_module_slug VARCHAR(100),
    completed_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Indexing for performance
CREATE INDEX IF NOT EXISTS idx_roadmap_modules_order ON roadmap_modules(order_index);
CREATE INDEX IF NOT EXISTS idx_user_roadmap_progress_user ON user_roadmap_progress(user_id);

-- Seed Initial Roadmap Modules
INSERT INTO roadmap_modules (id, slug, title, description, order_index, tier, icon_name, category_slug, xp_reward)
VALUES 
('10000000-0000-0000-0000-000000000001', 'arrays-basics', 'Arrays & Basics', 'Master core array structures, indexing, and two-pointer techniques.', 1, 'BEGINNER', 'layout-grid', 'arrays', 100),
('10000000-0000-0000-0000-000000000002', 'sorting-algorithms', 'Sorting Algorithms', 'Understand Bubble, Selection, Insertion, Merge, and Quick sort.', 2, 'BEGINNER', 'arrow-down-up', 'sorting', 150),
('10000000-0000-0000-0000-000000000003', 'searching-techniques', 'Searching Techniques', 'Linear search, Binary search, and search space optimization.', 3, 'BEGINNER', 'search', 'searching', 150),
('10000000-0000-0000-0000-000000000004', 'linked-lists', 'Linked Lists', 'Singly and doubly linked lists, pointer manipulation, and traversal.', 4, 'INTERMEDIATE', 'link', 'linked-lists', 200),
('10000000-0000-0000-0000-000000000005', 'stacks-and-queues', 'Stacks & Queues', 'LIFO & FIFO operations, expression evaluation, and monotonic stacks.', 5, 'INTERMEDIATE', 'layers', 'arrays', 200),
('10000000-0000-0000-0000-000000000006', 'trees-and-traversals', 'Trees & Traversals', 'Binary Trees, BSTs, In-order/Pre-order/Post-order traversals.', 6, 'INTERMEDIATE', 'git-branch', 'trees', 250),
('10000000-0000-0000-0000-000000000007', 'graph-algorithms', 'Graph Algorithms', 'BFS, DFS, shortest path algorithms (Dijkstra), and cycle detection.', 7, 'ADVANCED', 'share-2', 'graphs', 300),
('10000000-0000-0000-0000-000000000008', 'dynamic-programming', 'Dynamic Programming', 'Memoization, bottom-up tabulation, subproblem breakdown, LCS.', 8, 'ADVANCED', 'cpu', 'dynamic-programming', 350)
ON CONFLICT (slug) DO NOTHING;

-- Update prerequisites
UPDATE roadmap_modules SET prerequisite_module_id = '10000000-0000-0000-0000-000000000001' WHERE slug = 'sorting-algorithms';
UPDATE roadmap_modules SET prerequisite_module_id = '10000000-0000-0000-0000-000000000002' WHERE slug = 'searching-techniques';
UPDATE roadmap_modules SET prerequisite_module_id = '10000000-0000-0000-0000-000000000003' WHERE slug = 'linked-lists';
UPDATE roadmap_modules SET prerequisite_module_id = '10000000-0000-0000-0000-000000000004' WHERE slug = 'stacks-and-queues';
UPDATE roadmap_modules SET prerequisite_module_id = '10000000-0000-0000-0000-000000000005' WHERE slug = 'trees-and-traversals';
UPDATE roadmap_modules SET prerequisite_module_id = '10000000-0000-0000-0000-000000000006' WHERE slug = 'graph-algorithms';
UPDATE roadmap_modules SET prerequisite_module_id = '10000000-0000-0000-0000-000000000007' WHERE slug = 'dynamic-programming';

-- Seed Steps for Arrays Module
INSERT INTO roadmap_steps (id, module_id, step_number, step_type, title, description, reference_slug, xp_reward)
VALUES
(gen_random_uuid(), '10000000-0000-0000-0000-000000000001', 1, 'LEARN', 'Array Basics & Memory Layout', 'Understand contiguous memory and indexing.', 'two-sum', 20),
(gen_random_uuid(), '10000000-0000-0000-0000-000000000001', 2, 'VISUALIZE', 'Watch Array Operations', 'Visualize element access and mutations.', 'bubble-sort', 20),
(gen_random_uuid(), '10000000-0000-0000-0000-000000000001', 3, 'PRACTICE', 'Solve Two Sum Problem', 'Implement two-pointer and hash map approaches.', 'two-sum', 30),
(gen_random_uuid(), '10000000-0000-0000-0000-000000000001', 4, 'CHALLENGE', 'Kadane''s Algorithm Challenge', 'Find maximum subarray sum in O(n) time.', 'kadanes-algorithm', 40),
(gen_random_uuid(), '10000000-0000-0000-0000-000000000001', 5, 'MASTER', 'Arrays Mastery Assessment', 'Earn the Arrays Master badge.', 'two-sum', 50)
ON CONFLICT (module_id, step_number) DO NOTHING;

-- Seed Steps for Sorting Module
INSERT INTO roadmap_steps (id, module_id, step_number, step_type, title, description, reference_slug, xp_reward)
VALUES
(gen_random_uuid(), '10000000-0000-0000-0000-000000000002', 1, 'LEARN', 'Sorting Fundamentals & O(n²)', 'Learn Bubble, Selection, and Insertion Sort.', 'bubble-sort', 20),
(gen_random_uuid(), '10000000-0000-0000-0000-000000000002', 2, 'VISUALIZE', 'Step through Quick & Merge Sort', 'Interactive divide-and-conquer visualizer.', 'quick-sort', 20),
(gen_random_uuid(), '10000000-0000-0000-0000-000000000002', 3, 'PRACTICE', 'Implement Merge Sort in Code', 'Write recursive divide and conquer sorting.', 'merge-sort', 30),
(gen_random_uuid(), '10000000-0000-0000-0000-000000000002', 4, 'CHALLENGE', 'Partition Array Challenge', 'Quick sort partitioning logic practice.', 'quick-sort', 40),
(gen_random_uuid(), '10000000-0000-0000-0000-000000000002', 5, 'MASTER', 'Sorting Master Verification', 'Complete sorting module challenges.', 'merge-sort', 50)
ON CONFLICT (module_id, step_number) DO NOTHING;
