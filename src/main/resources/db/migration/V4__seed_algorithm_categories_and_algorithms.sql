-- Seed Additional Categories
INSERT INTO algorithm_categories (id, name, slug, description)
VALUES
(gen_random_uuid(), 'Arrays', 'arrays', 'Algorithms and techniques for solving array-based problems.'),
(gen_random_uuid(), 'Linked Lists', 'linked-lists', 'Algorithms and operations involving linked list data structures.'),
(gen_random_uuid(), 'Trees', 'trees', 'Algorithms and data structures based on hierarchical tree structures.'),
(gen_random_uuid(), 'Graphs', 'graphs', 'Algorithms for traversing and solving graph-based problems.'),
(gen_random_uuid(), 'Dynamic Programming', 'dynamic-programming', 'Optimization techniques that solve complex problems using overlapping subproblems.'),
(gen_random_uuid(), 'Greedy Algorithms', 'greedy', 'Algorithms that build solutions by repeatedly making locally optimal choices.')
ON CONFLICT (slug) DO NOTHING;

-- Update existing Binary Search difficulty to EASY per Phase 3B specifications
UPDATE algorithms SET difficulty = 'EASY' WHERE slug = 'binary-search';

-- SORTING ALGORITHMS
INSERT INTO algorithms (id, category_id, name, slug, description, difficulty, time_complexity, space_complexity)
SELECT gen_random_uuid(), id, 'Merge Sort', 'merge-sort',
'An efficient, stable, divide-and-conquer algorithm that recursively splits the array into halves, sorts each half, and merges the sorted halves back together.',
'MEDIUM', 'O(n log n)', 'O(n)'
FROM algorithm_categories WHERE slug = 'sorting'
ON CONFLICT (slug) DO NOTHING;

INSERT INTO algorithms (id, category_id, name, slug, description, difficulty, time_complexity, space_complexity)
SELECT gen_random_uuid(), id, 'Quick Sort', 'quick-sort',
'A fast divide-and-conquer sorting algorithm that selects a pivot element and partitions the array into sub-arrays of elements less than and greater than the pivot.',
'MEDIUM', 'O(n log n) average', 'O(log n)'
FROM algorithm_categories WHERE slug = 'sorting'
ON CONFLICT (slug) DO NOTHING;

-- ARRAYS ALGORITHMS
INSERT INTO algorithms (id, category_id, name, slug, description, difficulty, time_complexity, space_complexity)
SELECT gen_random_uuid(), id, 'Two Sum', 'two-sum',
'A fundamental array problem that finds two numbers in an array that add up to a specific target sum using a hash map for efficient lookup.',
'EASY', 'O(n)', 'O(n)'
FROM algorithm_categories WHERE slug = 'arrays'
ON CONFLICT (slug) DO NOTHING;

INSERT INTO algorithms (id, category_id, name, slug, description, difficulty, time_complexity, space_complexity)
SELECT gen_random_uuid(), id, 'Kadanes Algorithm', 'kadanes-algorithm',
'An optimal algorithm for finding the contiguous subarray within a one-dimensional numerical array that has the largest sum.',
'MEDIUM', 'O(n)', 'O(1)'
FROM algorithm_categories WHERE slug = 'arrays'
ON CONFLICT (slug) DO NOTHING;

-- LINKED LISTS ALGORITHMS
INSERT INTO algorithms (id, category_id, name, slug, description, difficulty, time_complexity, space_complexity)
SELECT gen_random_uuid(), id, 'Linked List Traversal', 'linked-list-traversal',
'The process of visiting each node in a linked list sequentially from the head pointer to the end node.',
'EASY', 'O(n)', 'O(1)'
FROM algorithm_categories WHERE slug = 'linked-lists'
ON CONFLICT (slug) DO NOTHING;

-- TREES ALGORITHMS
INSERT INTO algorithms (id, category_id, name, slug, description, difficulty, time_complexity, space_complexity)
SELECT gen_random_uuid(), id, 'Binary Search Tree', 'binary-search-tree',
'A node-based binary tree data structure where the key in each node is greater than all keys in its left subtree and less than those in its right subtree.',
'MEDIUM', 'O(log n) average', 'O(n)'
FROM algorithm_categories WHERE slug = 'trees'
ON CONFLICT (slug) DO NOTHING;

INSERT INTO algorithms (id, category_id, name, slug, description, difficulty, time_complexity, space_complexity)
SELECT gen_random_uuid(), id, 'Tree Traversal', 'tree-traversal',
'Methods for visiting all nodes of a tree structure in specific orders including In-order, Pre-order, and Post-order traversals.',
'EASY', 'O(n)', 'O(h)'
FROM algorithm_categories WHERE slug = 'trees'
ON CONFLICT (slug) DO NOTHING;

-- GRAPHS ALGORITHMS
INSERT INTO algorithms (id, category_id, name, slug, description, difficulty, time_complexity, space_complexity)
SELECT gen_random_uuid(), id, 'Breadth-First Search', 'breadth-first-search',
'A graph traversal algorithm that explores all neighbor nodes at the present depth before moving on to nodes at the next depth level.',
'EASY', 'O(V + E)', 'O(V)'
FROM algorithm_categories WHERE slug = 'graphs'
ON CONFLICT (slug) DO NOTHING;

INSERT INTO algorithms (id, category_id, name, slug, description, difficulty, time_complexity, space_complexity)
SELECT gen_random_uuid(), id, 'Depth-First Search', 'depth-first-search',
'A graph traversal algorithm that explores as far as possible along each branch before backtracking.',
'EASY', 'O(V + E)', 'O(V)'
FROM algorithm_categories WHERE slug = 'graphs'
ON CONFLICT (slug) DO NOTHING;

INSERT INTO algorithms (id, category_id, name, slug, description, difficulty, time_complexity, space_complexity)
SELECT gen_random_uuid(), id, 'Dijkstras Algorithm', 'dijkstras-algorithm',
'An algorithm for finding the shortest paths between nodes in a weighted graph with non-negative edge weights using a priority queue.',
'MEDIUM', 'O((V + E) log V)', 'O(V)'
FROM algorithm_categories WHERE slug = 'graphs'
ON CONFLICT (slug) DO NOTHING;

-- DYNAMIC PROGRAMMING ALGORITHMS
INSERT INTO algorithms (id, category_id, name, slug, description, difficulty, time_complexity, space_complexity)
SELECT gen_random_uuid(), id, 'Fibonacci Dynamic Programming', 'fibonacci-dynamic-programming',
'Computing Fibonacci numbers using memoization or bottom-up tabulation to avoid exponential redundant sub-problem calculations.',
'EASY', 'O(n)', 'O(n)'
FROM algorithm_categories WHERE slug = 'dynamic-programming'
ON CONFLICT (slug) DO NOTHING;

INSERT INTO algorithms (id, category_id, name, slug, description, difficulty, time_complexity, space_complexity)
SELECT gen_random_uuid(), id, 'Longest Common Subsequence', 'longest-common-subsequence',
'A classic dynamic programming problem to find the longest subsequence common to two sequences.',
'MEDIUM', 'O(m × n)', 'O(m × n)'
FROM algorithm_categories WHERE slug = 'dynamic-programming'
ON CONFLICT (slug) DO NOTHING;

-- GREEDY ALGORITHMS
INSERT INTO algorithms (id, category_id, name, slug, description, difficulty, time_complexity, space_complexity)
SELECT gen_random_uuid(), id, 'Activity Selection', 'activity-selection',
'A greedy algorithm for selecting the maximum number of mutually compatible activities that share a common resource.',
'MEDIUM', 'O(n log n)', 'O(1)'
FROM algorithm_categories WHERE slug = 'greedy'
ON CONFLICT (slug) DO NOTHING;
