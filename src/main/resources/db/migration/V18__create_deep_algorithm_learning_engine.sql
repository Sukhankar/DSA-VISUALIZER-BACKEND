-- ==============================================================================
-- Flyway Migration V18: Deep Algorithm Learning Engine (Beginner, Intermediate, Advanced)
-- ==============================================================================

-- 1. Main Learning Content Table
CREATE TABLE IF NOT EXISTS algorithm_learning_content (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    algorithm_id UUID NOT NULL REFERENCES algorithms(id) ON DELETE CASCADE,
    level VARCHAR(20) NOT NULL CHECK (level IN ('BEGINNER', 'INTERMEDIATE', 'ADVANCED')),
    introduction TEXT,
    problem_statement TEXT,
    intuition TEXT,
    why_it_works TEXT,
    how_it_works TEXT, -- JSON array of steps or formatted text
    pseudocode TEXT,
    complexity_summary TEXT,
    when_to_use TEXT,
    when_not_to_use TEXT,
    advantages TEXT,
    limitations TEXT,
    common_mistakes TEXT,
    interview_tips TEXT,
    implementation_notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_algorithm_level UNIQUE(algorithm_id, level)
);

-- 2. Advanced Theory Table (Optional for Advanced Depth)
CREATE TABLE IF NOT EXISTS algorithm_learning_advanced (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    learning_content_id UUID NOT NULL REFERENCES algorithm_learning_content(id) ON DELETE CASCADE,
    mathematical_foundation TEXT,
    invariant TEXT,
    correctness_proof TEXT,
    recurrence TEXT,
    recurrence_solution TEXT,
    optimization TEXT,
    memory_analysis TEXT,
    advanced_tradeoffs TEXT,
    competitive_programming_notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 3. Practice Recommendations Table
CREATE TABLE IF NOT EXISTS algorithm_learning_practice (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    learning_content_id UUID NOT NULL REFERENCES algorithm_learning_content(id) ON DELETE CASCADE,
    problem_title VARCHAR(255) NOT NULL,
    problem_slug VARCHAR(255) NOT NULL,
    difficulty VARCHAR(50) NOT NULL,
    platform VARCHAR(50) DEFAULT 'CodeLoom Arena',
    display_order INT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for lightning fast lookups
CREATE INDEX IF NOT EXISTS idx_learning_content_algo_level ON algorithm_learning_content(algorithm_id, level);
CREATE INDEX IF NOT EXISTS idx_learning_advanced_content_id ON algorithm_learning_advanced(learning_content_id);
CREATE INDEX IF NOT EXISTS idx_learning_practice_content_id ON algorithm_learning_practice(learning_content_id);


-- ==============================================================================
-- Seed Beginner Content for Bubble Sort
-- ==============================================================================
INSERT INTO algorithm_learning_content (
    algorithm_id, level, introduction, problem_statement, intuition, why_it_works, how_it_works,
    pseudocode, complexity_summary, when_to_use, when_not_to_use, advantages, limitations,
    common_mistakes, interview_tips, implementation_notes
)
SELECT 
    id, 
    'BEGINNER',
    'Bubble Sort is like water bubbles rising to the surface! Larger numbers gradually bubble up to the right end of the array one pass at a time.',
    'Rearrange a list of numbers so they are ordered from smallest to largest.',
    'Imagine line of students of different heights. You walk down the line compare pairs of adjacent students. If the left student is taller than the right student, they swap positions!',
    'Because every adjacent swap moves larger items to the right, each pass guarantees that the largest unsorted value arrives at its final home position.',
    '["1. Start at the first element (index 0).", "2. Compare current element with next element.", "3. If current > next, swap them!", "4. Move to next pair until end of list.", "5. Repeat process until no swaps occur."]',
    'for i from 0 to N-1:\n  for j from 0 to N-i-2:\n    if arr[j] > arr[j+1]:\n      swap(arr[j], arr[j+1])',
    'Time: O(N²) in general, but O(N) if already sorted. Space: O(1) extra space.',
    '• Small datasets (less than 20 items)\n• Learning basic sorting concepts\n• Data is almost completely sorted already',
    '• Large arrays (10,000+ items)\n• Performance-critical real-time applications',
    '• Very simple to write and remember\n• Uses no extra memory space\n• Detects sorted arrays quickly',
    '• Very slow on unsorted large data',
    '• Forgetting to reset the swapped flag inside the pass loop\n• Off-by-one loop boundaries (causing IndexOutOfBounds errors)',
    'Explain the early-exit optimization flag during your interview to demonstrate edge-case thinking!',
    'Use an early exit boolean flag `swapped`. If no swaps happen in a pass, break early.'
FROM algorithms WHERE slug = 'bubble-sort'
ON CONFLICT (algorithm_id, level) DO NOTHING;


-- ==============================================================================
-- Seed Intermediate Content for Bubble Sort
-- ==============================================================================
INSERT INTO algorithm_learning_content (
    algorithm_id, level, introduction, problem_statement, intuition, why_it_works, how_it_works,
    pseudocode, complexity_summary, when_to_use, when_not_to_use, advantages, limitations,
    common_mistakes, interview_tips, implementation_notes
)
SELECT 
    id, 
    'INTERMEDIATE',
    'Bubble Sort is an in-place, comparison-based, stable sorting algorithm. It establishes an invariant over array suffixes by iteratively moving maximum unsorted elements to their final positions.',
    'Given an array A of N comparable elements, produce a permutation A'' such that A''[0] <= A''[1] <= ... <= A''[N-1].',
    'Each outer iteration i reduces the active unsorted range to [0..N-1-i]. Inner comparisons swap inverted adjacent pairs (A[j] > A[j+1]).',
    'Every adjacent swap eliminates exactly one pair inversion. Since adjacent swaps maintain relative order of equal keys, the algorithm is stable.',
    '["1. Outer loop i from 0 to N-2.", "2. Set boolean flag swapped = false.", "3. Inner loop j from 0 to N-i-2.", "4. If A[j] > A[j+1], perform temp swap and set swapped = true.", "5. If swapped is false at inner loop exit, break outer loop."]',
    'function bubbleSort(arr):\n  n = arr.length\n  for i = 0 to n - 2:\n    swapped = false\n    for j = 0 to n - i - 2:\n      if arr[j] > arr[j + 1]:\n        swap(arr[j], arr[j + 1])\n        swapped = true\n    if not swapped: break',
    'Best: O(N) time (1 pass, 0 swaps). Worst: O(N²) time (N*(N-1)/2 swaps). Space: O(1) auxiliary.',
    '• Adaptive sorting on nearly sorted lists\n• Minimal memory hardware constraints\n• Stable key sorting requirements',
    '• Competitive programming problems where N > 1,000\n• General sorting where QuickSort or MergeSort is available',
    '• In-place sorting (O(1) memory)\n• Stable sorting preserving duplicate key order\n• Adaptive O(N) best case',
    '• Quadratic worst case O(N²)\n• Excessive memory writes compared to Selection Sort',
    '• Loop range error: running inner loop up to N-1 instead of N-i-1\n• Missing early termination check',
    'Be ready to discuss stability, inversion count reductions, and comparison with Insertion Sort.',
    'Implement using destructuring swap or temporary variable depending on language primitives.'
FROM algorithms WHERE slug = 'bubble-sort'
ON CONFLICT (algorithm_id, level) DO NOTHING;


-- ==============================================================================
-- Seed Advanced Content for Bubble Sort & Advanced Theory
-- ==============================================================================
INSERT INTO algorithm_learning_content (
    algorithm_id, level, introduction, problem_statement, intuition, why_it_works, how_it_works,
    pseudocode, complexity_summary, when_to_use, when_not_to_use, advantages, limitations,
    common_mistakes, interview_tips, implementation_notes
)
SELECT 
    id, 
    'ADVANCED',
    'Rigorous theoretical examination of Bubble Sort: Permutation Inversions, Suffix Invariants, and Worst-Case Proof bounds.',
    'Permute sequence S = <x_1, x_2, ..., x_n> into S'' = <x_1'', x_2'', ..., x_n''> under strict total order relation <=.',
    'Let I(S) be the set of inversions {(i, j) | i < j and S[i] > S[j]}. An adjacent swap of S[j] and S[j+1] strictly decreases |I(S)| by 1.',
    'Since |I(S)| is finite and upper-bounded by n(n-1)/2, adjacent swaps must terminate in a zero-inversion state (sorted sequence).',
    '["1. Maintain invariant: Subarray A[n-i..n-1] is sorted and contains the i largest elements.", "2. Execute single pass over A[0..n-i-1].", "3. Record last swap index to shrink unsorted boundary dynamically on subsequent passes."]',
    'void bubbleSortAdvanced(int[] A, int n) {\n  int newn;\n  do {\n    newn = 0;\n    for (int i = 1; i < n; i++) {\n      if (A[i-1] > A[i]) {\n        swap(A[i-1], A[i]);\n        newn = i;\n      }\n    }\n    n = newn;\n  } while (n > 1);\n}',
    'Asymptotic Bounds: Worst/Avg = Theta(N²), Best = Theta(N). Auxiliary Space = Theta(1). Total Swaps = Inversion count I.',
    '• Microcontroller firmware with extremely low code size footprint and strict O(1) stack memory restrictions.',
    '• Any production pipeline processing non-trivial N due to CPU cache pollution and high branch misprediction rates.',
    '• Optimal for 1-inversion arrays (O(N) time, 1 swap)\n• Strict memory locality during adjacent comparisons',
    '• High cache miss penalty relative to Insertion Sort\n• Quadratic comparison count',
    '• Assuming Bubble Sort is faster than Insertion Sort for small N (Insertion Sort performs 3x fewer writes)',
    'Expect follow-ups on Cocktail Shaker Sort (bidirectional bubble sort) and inversion count bounds.',
    'Optimize pass range by storing the last swap position `newn`.'
FROM algorithms WHERE slug = 'bubble-sort'
ON CONFLICT (algorithm_id, level) DO NOTHING;

-- Advanced Theory Details for Bubble Sort
INSERT INTO algorithm_learning_advanced (
    learning_content_id, mathematical_foundation, invariant, correctness_proof,
    recurrence, recurrence_solution, optimization, memory_analysis, advanced_tradeoffs, competitive_programming_notes
)
SELECT 
    lc.id,
    'Sum of inversions I = sum_{i < j} [A[i] > A[j]]. Worst case occurs when input is reverse-sorted: I_max = n(n-1)/2.',
    'Loop Invariant: At start of pass i, subarray A[n-i..n-1] contains i largest elements in sorted order.',
    'Proof by Induction: Base case i=0 is vacuously true. Assume holds for pass i. In pass i+1, max element in A[0..n-i-1] is carried to index n-i-1. Thus subarray A[n-(i+1)..n-1] is sorted.',
    'T(n) = T(n-1) + (n-1), T(1) = 0.',
    'T(n) = n(n-1)/2 = Theta(n²).',
    'Cocktail Shaker Sort (Bidirectional Bubble Sort) passes alternative left-to-right and right-to-left to mitigate "turtles" (small values at the end).',
    'Memory: In-place O(1) auxiliary space. High CPU instruction count per comparison.',
    'Trade-off: Insertion Sort has identical O(N²) worst-case time, but conducts ~N²/4 assignments compared to Bubble Sort''s ~3N²/4 assignments.',
    'Rarely used in competitive programming unless testing inverse count boundaries.'
FROM algorithm_learning_content lc
JOIN algorithms a ON lc.algorithm_id = a.id
WHERE a.slug = 'bubble-sort' AND lc.level = 'ADVANCED'
ON CONFLICT DO NOTHING;
