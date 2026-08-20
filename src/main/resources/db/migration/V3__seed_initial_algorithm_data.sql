INSERT INTO algorithm_categories (
    id,
    name,
    slug,
    description
)
VALUES
(
    gen_random_uuid(),
    'Sorting',
    'sorting',
    'Algorithms for arranging data in a specific order.'
),
(
    gen_random_uuid(),
    'Searching',
    'searching',
    'Algorithms for finding elements within a collection.'
);


INSERT INTO algorithms (
    id,
    category_id,
    name,
    slug,
    description,
    difficulty,
    time_complexity,
    space_complexity
)
SELECT
    gen_random_uuid(),
    id,
    'Bubble Sort',
    'bubble-sort',
    'A simple sorting algorithm that repeatedly compares adjacent elements and swaps them when they are in the wrong order.',
    'EASY',
    'O(n²)',
    'O(1)'
FROM algorithm_categories
WHERE slug = 'sorting';


INSERT INTO algorithms (
    id,
    category_id,
    name,
    slug,
    description,
    difficulty,
    time_complexity,
    space_complexity
)
SELECT
    gen_random_uuid(),
    id,
    'Selection Sort',
    'selection-sort',
    'A sorting algorithm that repeatedly selects the smallest element from the unsorted portion.',
    'EASY',
    'O(n²)',
    'O(1)'
FROM algorithm_categories
WHERE slug = 'sorting';


INSERT INTO algorithms (
    id,
    category_id,
    name,
    slug,
    description,
    difficulty,
    time_complexity,
    space_complexity
)
SELECT
    gen_random_uuid(),
    id,
    'Insertion Sort',
    'insertion-sort',
    'A sorting algorithm that builds the final sorted array one element at a time.',
    'EASY',
    'O(n²)',
    'O(1)'
FROM algorithm_categories
WHERE slug = 'sorting';


INSERT INTO algorithms (
    id,
    category_id,
    name,
    slug,
    description,
    difficulty,
    time_complexity,
    space_complexity
)
SELECT
    gen_random_uuid(),
    id,
    'Linear Search',
    'linear-search',
    'A searching algorithm that checks elements sequentially until the target is found.',
    'EASY',
    'O(n)',
    'O(1)'
FROM algorithm_categories
WHERE slug = 'searching';


INSERT INTO algorithms (
    id,
    category_id,
    name,
    slug,
    description,
    difficulty,
    time_complexity,
    space_complexity
)
SELECT
    gen_random_uuid(),
    id,
    'Binary Search',
    'binary-search',
    'An efficient searching algorithm that repeatedly divides a sorted search space in half.',
    'MEDIUM',
    'O(log n)',
    'O(1)'
FROM algorithm_categories
WHERE slug = 'searching';
