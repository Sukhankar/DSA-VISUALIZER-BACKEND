-- ==============================================================================
-- Flyway Migration V7: LeetCode-Style Practice & Problem Solving Domain
-- ==============================================================================

-- 1. Create problems table
CREATE TABLE IF NOT EXISTS problems (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    category_id UUID REFERENCES algorithm_categories(id) ON DELETE SET NULL,
    title VARCHAR(200) NOT NULL,
    slug VARCHAR(200) NOT NULL UNIQUE,
    difficulty VARCHAR(20) NOT NULL,
    description TEXT NOT NULL,
    constraints TEXT,
    input_format TEXT,
    output_format TEXT,
    hints TEXT,
    solution_explanation TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 2. Create problem_examples table
CREATE TABLE IF NOT EXISTS problem_examples (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    problem_id UUID NOT NULL REFERENCES problems(id) ON DELETE CASCADE,
    example_number INT NOT NULL,
    input_data TEXT NOT NULL,
    output_data TEXT NOT NULL,
    explanation TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_problem_example UNIQUE(problem_id, example_number)
);

-- 3. Create problem_tags table
CREATE TABLE IF NOT EXISTS problem_tags (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    problem_id UUID NOT NULL REFERENCES problems(id) ON DELETE CASCADE,
    tag_name VARCHAR(50) NOT NULL,
    CONSTRAINT unique_problem_tag UNIQUE(problem_id, tag_name)
);

-- 4. Create problem_related_algorithms table
CREATE TABLE IF NOT EXISTS problem_related_algorithms (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    problem_id UUID NOT NULL REFERENCES problems(id) ON DELETE CASCADE,
    algorithm_id UUID NOT NULL REFERENCES algorithms(id) ON DELETE CASCADE,
    CONSTRAINT unique_problem_algorithm UNIQUE(problem_id, algorithm_id)
);

-- Indexes for fast query lookup
CREATE INDEX IF NOT EXISTS idx_problems_slug ON problems(slug);
CREATE INDEX IF NOT EXISTS idx_problems_difficulty ON problems(difficulty);
CREATE INDEX IF NOT EXISTS idx_problems_category ON problems(category_id);
CREATE INDEX IF NOT EXISTS idx_problem_examples_problem ON problem_examples(problem_id);
CREATE INDEX IF NOT EXISTS idx_problem_tags_problem ON problem_tags(problem_id);

-- ==============================================================================
-- 5. Seed Problem 1: Two Sum
-- ==============================================================================
INSERT INTO problems (id, category_id, title, slug, difficulty, description, constraints, input_format, output_format, hints, solution_explanation)
VALUES (
    'a1b2c3d4-0001-4000-8000-000000000001'::uuid,
    (SELECT id FROM algorithm_categories WHERE slug = 'arrays' LIMIT 1),
    'Two Sum',
    'two-sum',
    'EASY',
    'Given an array of integers nums and an integer target, return indices of the two numbers such that they add up to target. You may assume that each input would have exactly one solution, and you may not use the same element twice. You can return the answer in any order.',
    '• 2 <= nums.length <= 10^4\n• -10^9 <= nums[i] <= 10^9\n• -10^9 <= target <= 10^9\n• Only one valid answer exists.',
    'First line: integer array nums. Second line: integer target.',
    'An array of two indices [i, j] such that nums[i] + nums[j] == target.',
    '1. A brute force approach compares every pair (O(N²)).\n2. Can we use a Hash Map to store complement values (target - nums[i]) as we iterate in O(N) time?',
    'Use a HashMap storing element value to its array index. For each element num at index i, check if (target - num) exists in the hash map. If yes, return current index i and stored index map.get(target - num).'
)
ON CONFLICT (slug) DO NOTHING;

INSERT INTO problem_examples (problem_id, example_number, input_data, output_data, explanation)
VALUES 
('a1b2c3d4-0001-4000-8000-000000000001'::uuid, 1, 'nums = [2,7,11,15], target = 9', '[0,1]', 'Because nums[0] + nums[1] == 9, we return [0, 1].'),
('a1b2c3d4-0001-4000-8000-000000000001'::uuid, 2, 'nums = [3,2,4], target = 6', '[1,2]', 'nums[1] + nums[2] == 2 + 4 = 6.'),
('a1b2c3d4-0001-4000-8000-000000000001'::uuid, 3, 'nums = [3,3], target = 6', '[0,1]', 'nums[0] + nums[1] == 3 + 3 = 6.')
ON CONFLICT (problem_id, example_number) DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_name) VALUES
('a1b2c3d4-0001-4000-8000-000000000001'::uuid, 'Array'),
('a1b2c3d4-0001-4000-8000-000000000001'::uuid, 'Hash Table')
ON CONFLICT (problem_id, tag_name) DO NOTHING;

INSERT INTO problem_related_algorithms (problem_id, algorithm_id)
SELECT 'a1b2c3d4-0001-4000-8000-000000000001'::uuid, a.id
FROM algorithms a WHERE a.slug = 'two-sum'
ON CONFLICT (problem_id, algorithm_id) DO NOTHING;

-- ==============================================================================
-- 6. Seed Problem 2: Binary Search
-- ==============================================================================
INSERT INTO problems (id, category_id, title, slug, difficulty, description, constraints, input_format, output_format, hints, solution_explanation)
VALUES (
    'a1b2c3d4-0002-4000-8000-000000000002'::uuid,
    (SELECT id FROM algorithm_categories WHERE slug = 'searching' LIMIT 1),
    'Binary Search',
    'binary-search-problem',
    'EASY',
    'Given an array of integers nums which is sorted in ascending order, and an integer target, write a function to search target in nums. If target exists, then return its index. Otherwise, return -1. You must write an algorithm with O(log n) runtime complexity.',
    '• 1 <= nums.length <= 10^4\n• -10^4 <= nums[i], target <= 10^4\n• All integers in nums are unique.\n• nums is sorted in ascending order.',
    'Sorted array nums and target integer.',
    'Index of target if found, else -1.',
    '1. Maintain left and right pointers starting at 0 and nums.length - 1.\n2. Calculate mid = left + (right - left) / 2 to prevent overflow.',
    'Compare target with nums[mid]. If target == nums[mid], return mid. If target > nums[mid], move left = mid + 1. Else move right = mid - 1.'
)
ON CONFLICT (slug) DO NOTHING;

INSERT INTO problem_examples (problem_id, example_number, input_data, output_data, explanation)
VALUES 
('a1b2c3d4-0002-4000-8000-000000000002'::uuid, 1, 'nums = [-1,0,3,5,9,12], target = 9', '4', '9 exists in nums and its index is 4.'),
('a1b2c3d4-0002-4000-8000-000000000002'::uuid, 2, 'nums = [-1,0,3,5,9,12], target = 2', '-1', '2 does not exist in nums so return -1.')
ON CONFLICT (problem_id, example_number) DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_name) VALUES
('a1b2c3d4-0002-4000-8000-000000000002'::uuid, 'Array'),
('a1b2c3d4-0002-4000-8000-000000000002'::uuid, 'Binary Search')
ON CONFLICT (problem_id, tag_name) DO NOTHING;

INSERT INTO problem_related_algorithms (problem_id, algorithm_id)
SELECT 'a1b2c3d4-0002-4000-8000-000000000002'::uuid, a.id
FROM algorithms a WHERE a.slug = 'binary-search'
ON CONFLICT (problem_id, algorithm_id) DO NOTHING;

-- ==============================================================================
-- 7. Seed Problem 3: Valid Parentheses
-- ==============================================================================
INSERT INTO problems (id, category_id, title, slug, difficulty, description, constraints, input_format, output_format, hints, solution_explanation)
VALUES (
    'a1b2c3d4-0003-4000-8000-000000000003'::uuid,
    (SELECT id FROM algorithm_categories WHERE slug = 'data-structures' LIMIT 1),
    'Valid Parentheses',
    'valid-parentheses',
    'EASY',
    'Given a string s containing just the characters ''('', '')'', ''{'', ''}'', ''['' and '']'', determine if the input string is valid. An input string is valid if: Open brackets must be closed by the same type of brackets, and open brackets must be closed in the correct order.',
    '• 1 <= s.length <= 10^4\n• s consists of parentheses only ''()[]{}''.',
    'A single string s.',
    'Boolean true or false.',
    '1. Use a Stack to push opening brackets.\n2. When encountering a closing bracket, pop from the stack and verify matching type.',
    'Iterate through the string. Push ''('', ''{'', ''['' onto a Stack. When a closing bracket is encountered, check if stack is non-empty and top element matches. At the end, return stack.isEmpty().'
)
ON CONFLICT (slug) DO NOTHING;

INSERT INTO problem_examples (problem_id, example_number, input_data, output_data, explanation)
VALUES 
('a1b2c3d4-0003-4000-8000-000000000003'::uuid, 1, 's = "()"', 'true', 'Matching pair of parentheses.'),
('a1b2c3d4-0003-4000-8000-000000000003'::uuid, 2, 's = "()[]{}"', 'true', 'All brackets properly opened and closed in order.'),
('a1b2c3d4-0003-4000-8000-000000000003'::uuid, 3, 's = "(]"', 'false', 'Mismatched bracket types.')
ON CONFLICT (problem_id, example_number) DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_name) VALUES
('a1b2c3d4-0003-4000-8000-000000000003'::uuid, 'String'),
('a1b2c3d4-0003-4000-8000-000000000003'::uuid, 'Stack')
ON CONFLICT (problem_id, tag_name) DO NOTHING;

-- ==============================================================================
-- 8. Seed Problem 4: Maximum Subarray (Kadane's Algorithm)
-- ==============================================================================
INSERT INTO problems (id, category_id, title, slug, difficulty, description, constraints, input_format, output_format, hints, solution_explanation)
VALUES (
    'a1b2c3d4-0004-4000-8000-000000000004'::uuid,
    (SELECT id FROM algorithm_categories WHERE slug = 'dynamic-programming' LIMIT 1),
    'Maximum Subarray',
    'maximum-subarray',
    'MEDIUM',
    'Given an integer array nums, find the subarray with the largest sum, and return its sum.',
    '• 1 <= nums.length <= 10^5\n• -10^4 <= nums[i] <= 10^4',
    'Integer array nums.',
    'Integer representing maximum subarray sum.',
    '1. Kadanes Algorithm: track current sum max(nums[i], current_sum + nums[i]).',
    'Maintain max_so_far and max_ending_here. For each element x in nums, max_ending_here = max(x, max_ending_here + x), max_so_far = max(max_so_far, max_ending_here).'
)
ON CONFLICT (slug) DO NOTHING;

INSERT INTO problem_examples (problem_id, example_number, input_data, output_data, explanation)
VALUES 
('a1b2c3d4-0004-4000-8000-000000000004'::uuid, 1, 'nums = [-2,1,-3,4,-1,2,1,-5,4]', '6', 'The subarray [4,-1,2,1] has the largest sum 6.'),
('a1b2c3d4-0004-4000-8000-000000000004'::uuid, 2, 'nums = [1]', '1', 'Single element subarray has sum 1.'),
('a1b2c3d4-0004-4000-8000-000000000004'::uuid, 3, 'nums = [5,4,-1,7,8]', '23', 'The subarray [5,4,-1,7,8] has sum 23.')
ON CONFLICT (problem_id, example_number) DO NOTHING;

INSERT INTO problem_tags (problem_id, tag_name) VALUES
('a1b2c3d4-0004-4000-8000-000000000004'::uuid, 'Array'),
('a1b2c3d4-0004-4000-8000-000000000004'::uuid, 'Dynamic Programming')
ON CONFLICT (problem_id, tag_name) DO NOTHING;

INSERT INTO problem_related_algorithms (problem_id, algorithm_id)
SELECT 'a1b2c3d4-0004-4000-8000-000000000004'::uuid, a.id
FROM algorithms a WHERE a.slug = 'kadanes-algorithm'
ON CONFLICT (problem_id, algorithm_id) DO NOTHING;
