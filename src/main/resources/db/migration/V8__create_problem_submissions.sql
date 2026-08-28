-- ==============================================================================
-- Flyway Migration V8: Code Submission, Execution & Attempt Tracking Domain
-- ==============================================================================

-- 1. Create problem_test_cases table
CREATE TABLE IF NOT EXISTS problem_test_cases (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    problem_id UUID NOT NULL REFERENCES problems(id) ON DELETE CASCADE,
    test_case_number INT NOT NULL,
    input_data TEXT NOT NULL,
    output_data TEXT NOT NULL,
    is_hidden BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_problem_testcase UNIQUE(problem_id, test_case_number)
);

-- 2. Create problem_submissions table
CREATE TABLE IF NOT EXISTS problem_submissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    problem_id UUID NOT NULL REFERENCES problems(id) ON DELETE CASCADE,
    language VARCHAR(20) NOT NULL,
    source_code TEXT NOT NULL,
    status VARCHAR(20) NOT NULL,
    verdict VARCHAR(30) NOT NULL,
    execution_time_ms INT,
    memory_used_kb INT,
    total_tests INT NOT NULL DEFAULT 0,
    passed_tests INT NOT NULL DEFAULT 0,
    submitted_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP WITH TIME ZONE
);

-- 3. Create problem_code_drafts table
CREATE TABLE IF NOT EXISTS problem_code_drafts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    problem_id UUID NOT NULL REFERENCES problems(id) ON DELETE CASCADE,
    language VARCHAR(20) NOT NULL,
    source_code TEXT NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_user_problem_lang UNIQUE(user_id, problem_id, language)
);

-- Indexes for performance
CREATE INDEX IF NOT EXISTS idx_submissions_user ON problem_submissions(user_id);
CREATE INDEX IF NOT EXISTS idx_submissions_problem ON problem_submissions(problem_id);
CREATE INDEX IF NOT EXISTS idx_submissions_verdict ON problem_submissions(verdict);
CREATE INDEX IF NOT EXISTS idx_test_cases_problem ON problem_test_cases(problem_id);
CREATE INDEX IF NOT EXISTS idx_code_drafts_user_problem ON problem_code_drafts(user_id, problem_id);

-- ==============================================================================
-- 4. Seed Test Cases for Problem 1: Two Sum
-- ==============================================================================
INSERT INTO problem_test_cases (problem_id, test_case_number, input_data, output_data, is_hidden)
VALUES
('a1b2c3d4-0001-4000-8000-000000000001'::uuid, 1, 'nums = [2,7,11,15], target = 9', '[0,1]', FALSE),
('a1b2c3d4-0001-4000-8000-000000000001'::uuid, 2, 'nums = [3,2,4], target = 6', '[1,2]', FALSE),
('a1b2c3d4-0001-4000-8000-000000000001'::uuid, 3, 'nums = [3,3], target = 6', '[0,1]', FALSE),
('a1b2c3d4-0001-4000-8000-000000000001'::uuid, 4, 'nums = [1,5,8,12,19,25], target = 27', '[2,4]', TRUE),
('a1b2c3d4-0001-4000-8000-000000000001'::uuid, 5, 'nums = [-3,4,3,90], target = 0', '[0,2]', TRUE)
ON CONFLICT (problem_id, test_case_number) DO NOTHING;

-- ==============================================================================
-- 5. Seed Test Cases for Problem 2: Binary Search
-- ==============================================================================
INSERT INTO problem_test_cases (problem_id, test_case_number, input_data, output_data, is_hidden)
VALUES
('a1b2c3d4-0002-4000-8000-000000000002'::uuid, 1, 'nums = [-1,0,3,5,9,12], target = 9', '4', FALSE),
('a1b2c3d4-0002-4000-8000-000000000002'::uuid, 2, 'nums = [-1,0,3,5,9,12], target = 2', '-1', FALSE),
('a1b2c3d4-0002-4000-8000-000000000002'::uuid, 3, 'nums = [5], target = 5', '0', TRUE),
('a1b2c3d4-0002-4000-8000-000000000002'::uuid, 4, 'nums = [2,5], target = 5', '1', TRUE)
ON CONFLICT (problem_id, test_case_number) DO NOTHING;

-- ==============================================================================
-- 6. Seed Test Cases for Problem 3: Valid Parentheses
-- ==============================================================================
INSERT INTO problem_test_cases (problem_id, test_case_number, input_data, output_data, is_hidden)
VALUES
('a1b2c3d4-0003-4000-8000-000000000003'::uuid, 1, 's = "()"', 'true', FALSE),
('a1b2c3d4-0003-4000-8000-000000000003'::uuid, 2, 's = "()[]{}"', 'true', FALSE),
('a1b2c3d4-0003-4000-8000-000000000003'::uuid, 3, 's = "(]"', 'false', FALSE),
('a1b2c3d4-0003-4000-8000-000000000003'::uuid, 4, 's = "{[]}"', 'true', TRUE),
('a1b2c3d4-0003-4000-8000-000000000003'::uuid, 5, 's = "([)]"', 'false', TRUE)
ON CONFLICT (problem_id, test_case_number) DO NOTHING;

-- ==============================================================================
-- 7. Seed Test Cases for Problem 4: Maximum Subarray
-- ==============================================================================
INSERT INTO problem_test_cases (problem_id, test_case_number, input_data, output_data, is_hidden)
VALUES
('a1b2c3d4-0004-4000-8000-000000000004'::uuid, 1, 'nums = [-2,1,-3,4,-1,2,1,-5,4]', '6', FALSE),
('a1b2c3d4-0004-4000-8000-000000000004'::uuid, 2, 'nums = [1]', '1', FALSE),
('a1b2c3d4-0004-4000-8000-000000000004'::uuid, 3, 'nums = [5,4,-1,7,8]', '23', FALSE),
('a1b2c3d4-0004-4000-8000-000000000004'::uuid, 4, 'nums = [-1,-2,-3,-4]', '-1', TRUE)
ON CONFLICT (problem_id, test_case_number) DO NOTHING;
