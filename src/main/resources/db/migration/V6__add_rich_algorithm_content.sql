-- ==============================================================================
-- Flyway Migration V6: Rich Algorithm Content, Multi-Language Code & Examples
-- ==============================================================================

-- 1. Extend algorithms table with detailed educational columns
ALTER TABLE algorithms
    ADD COLUMN IF NOT EXISTS overview TEXT,
    ADD COLUMN IF NOT EXISTS when_to_use TEXT,
    ADD COLUMN IF NOT EXISTS advantages TEXT,
    ADD COLUMN IF NOT EXISTS limitations TEXT,
    ADD COLUMN IF NOT EXISTS constraints TEXT;

-- 2. Create algorithm_examples table
CREATE TABLE IF NOT EXISTS algorithm_examples (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    algorithm_id UUID NOT NULL REFERENCES algorithms(id) ON DELETE CASCADE,
    example_number INT NOT NULL,
    title VARCHAR(150) NOT NULL,
    input_data TEXT NOT NULL,
    output_data TEXT NOT NULL,
    explanation TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_algorithm_example_num UNIQUE(algorithm_id, example_number)
);

-- 3. Create algorithm_implementations table
CREATE TABLE IF NOT EXISTS algorithm_implementations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    algorithm_id UUID NOT NULL REFERENCES algorithms(id) ON DELETE CASCADE,
    language VARCHAR(20) NOT NULL,
    code TEXT NOT NULL,
    explanation TEXT,
    display_order INT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_algorithm_language UNIQUE(algorithm_id, language)
);

-- 4. Create algorithm_related table
CREATE TABLE IF NOT EXISTS algorithm_related (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    algorithm_id UUID NOT NULL REFERENCES algorithms(id) ON DELETE CASCADE,
    related_algorithm_id UUID NOT NULL REFERENCES algorithms(id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_algorithm_relation UNIQUE(algorithm_id, related_algorithm_id)
);

-- Indexing for high-performance lookup
CREATE INDEX IF NOT EXISTS idx_examples_algorithm_id ON algorithm_examples(algorithm_id);
CREATE INDEX IF NOT EXISTS idx_implementations_algorithm_id ON algorithm_implementations(algorithm_id);
CREATE INDEX IF NOT EXISTS idx_related_algorithm_id ON algorithm_related(algorithm_id);

-- ==============================================================================
-- 5. Seed Rich Content for Bubble Sort
-- ==============================================================================
UPDATE algorithms
SET overview = 'Bubble Sort is a foundational comparison-based sorting algorithm. It operates by making repeated passes through an array, comparing adjacent element pairs, and swapping them if they are in the wrong order. During each pass, the largest remaining unsorted element "bubbles up" to its correct position at the end of the array.',
    when_to_use = 'Best suited for small datasets, educational demonstrations, or nearly-sorted arrays where an optimized version can achieve O(N) performance.',
    advantages = '• Extremely intuitive and easy to understand.\n• In-place algorithm requiring O(1) auxiliary space.\n• Stable sort (preserves the relative order of duplicate elements).\n• Adaptive: can terminate early if no swaps occur during a pass.',
    limitations = '• Poor performance on large datasets with O(N²) average and worst-case time complexity.\n• Excessive swap operations compared to Insertion Sort.',
    constraints = '• 1 <= input.length <= 50 (for step-by-step visual playback)\n• -100 <= input[i] <= 100'
WHERE slug = 'bubble-sort';

-- Seed Examples for Bubble Sort
INSERT INTO algorithm_examples (algorithm_id, example_number, title, input_data, output_data, explanation)
SELECT id, 1, 'Standard Unsorted Array', '[5, 1, 4, 2, 8]', '[1, 2, 4, 5, 8]', 'Pass 1 compares 5 and 1 (swap), 5 and 4 (swap), 5 and 2 (swap), 5 and 8 (no swap). 8 reaches its sorted position. Subsequent passes bubble 5, 4, and 2 into position.'
FROM algorithms WHERE slug = 'bubble-sort'
ON CONFLICT (algorithm_id, example_number) DO NOTHING;

INSERT INTO algorithm_examples (algorithm_id, example_number, title, input_data, output_data, explanation)
SELECT id, 2, 'Already Sorted Array', '[1, 2, 3, 4, 5]', '[1, 2, 3, 4, 5]', 'The algorithm performs a single pass over adjacent elements. Since zero swaps are made, the optimized flag detects completion early and terminates in O(N) time.'
FROM algorithms WHERE slug = 'bubble-sort'
ON CONFLICT (algorithm_id, example_number) DO NOTHING;

INSERT INTO algorithm_examples (algorithm_id, example_number, title, input_data, output_data, explanation)
SELECT id, 3, 'Reverse Sorted Array', '[5, 4, 3, 2, 1]', '[1, 2, 3, 4, 5]', 'Worst-case scenario requiring maximum swaps (N*(N-1)/2 swaps). Every element must bubble across the entire array.'
FROM algorithms WHERE slug = 'bubble-sort'
ON CONFLICT (algorithm_id, example_number) DO NOTHING;

-- Seed Code Implementations for Bubble Sort
INSERT INTO algorithm_implementations (algorithm_id, language, code, explanation, display_order)
SELECT id, 'JAVA', 
'public class BubbleSort {
    public static void bubbleSort(int[] arr) {
        int n = arr.length;
        boolean swapped;
        for (int i = 0; i < n - 1; i++) {
            swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                    swapped = true;
                }
            }
            if (!swapped) break; // Optimization
        }
    }
}', 'Standard Java implementation with swapped boolean flag early-exit optimization.', 1
FROM algorithms WHERE slug = 'bubble-sort'
ON CONFLICT (algorithm_id, language) DO NOTHING;

INSERT INTO algorithm_implementations (algorithm_id, language, code, explanation, display_order)
SELECT id, 'PYTHON',
'def bubble_sort(arr):
    n = len(arr)
    for i in range(n - 1):
        swapped = False
        for j in range(0, n - i - 1):
            if arr[j] > arr[j + 1]:
                arr[j], arr[j + 1] = arr[j + 1], arr[j]
                swapped = True
        if not swapped:
            break
    return arr', 'Clean Python implementation utilizing tuple swapping.', 2
FROM algorithms WHERE slug = 'bubble-sort'
ON CONFLICT (algorithm_id, language) DO NOTHING;

INSERT INTO algorithm_implementations (algorithm_id, language, code, explanation, display_order)
SELECT id, 'JAVASCRIPT',
'function bubbleSort(arr) {
  const n = arr.length;
  let swapped;
  for (let i = 0; i < n - 1; i++) {
    swapped = false;
    for (let j = 0; j < n - i - 1; j++) {
      if (arr[j] > arr[j + 1]) {
        [arr[j], arr[j + 1]] = [arr[j + 1], arr[j]];
        swapped = true;
      }
    }
    if (!swapped) break;
  }
  return arr;
}', 'Modern JavaScript (ES6+) implementation with destructuring swap.', 3
FROM algorithms WHERE slug = 'bubble-sort'
ON CONFLICT (algorithm_id, language) DO NOTHING;

INSERT INTO algorithm_implementations (algorithm_id, language, code, explanation, display_order)
SELECT id, 'CPP',
'#include <vector>
#include <algorithm>

void bubbleSort(std::vector<int>& arr) {
    int n = arr.size();
    bool swapped;
    for (int i = 0; i < n - 1; ++i) {
        swapped = false;
        for (int j = 0; j < n - i - 1; ++j) {
            if (arr[j] > arr[j + 1]) {
                std::swap(arr[j], arr[j + 1]);
                swapped = true;
            }
        }
        if (!swapped) break;
    }
}', 'Efficient C++ implementation using std::swap.', 4
FROM algorithms WHERE slug = 'bubble-sort'
ON CONFLICT (algorithm_id, language) DO NOTHING;

-- Seed Related Algorithms for Bubble Sort
INSERT INTO algorithm_related (algorithm_id, related_algorithm_id)
SELECT a1.id, a2.id
FROM algorithms a1, algorithms a2
WHERE a1.slug = 'bubble-sort' AND a2.slug IN ('selection-sort', 'insertion-sort', 'quick-sort')
ON CONFLICT (algorithm_id, related_algorithm_id) DO NOTHING;

-- ==============================================================================
-- 6. Seed Rich Content for Binary Search
-- ==============================================================================
UPDATE algorithms
SET overview = 'Binary Search is an efficient algorithm for finding an item from a sorted list of items. It works by repeatedly dividing in half the portion of the list that could contain the item until you have narrowed down the possible locations to just one.',
    when_to_use = 'Use when searching in a strictly pre-sorted array or monotonic search space where O(log N) fast lookup is required.',
    advantages = '• Logarithmic time complexity O(log N).\n• Highly scalable for massive sorted arrays.\n• Minimal O(1) space requirement.',
    limitations = '• Requires array to be sorted beforehand.\n• Not suited for random-access linked structures.',
    constraints = '• Array MUST be sorted in ascending order\n• 1 <= input.length <= 50\n• Target integer'
WHERE slug = 'binary-search';

-- Seed Examples for Binary Search
INSERT INTO algorithm_examples (algorithm_id, example_number, title, input_data, output_data, explanation)
SELECT id, 1, 'Target Present in Array', 'Array: [2, 5, 8, 12, 16, 23, 38, 56, 72, 91], Target: 23', 'Index 5', 'Mid index 4 points to 16 (16 < 23), so search range shifts right to indices 5..9. Mid index 7 points to 56 (56 > 23), search range shifts left to 5..6. Mid index 5 points to 23 (Match!).'
FROM algorithms WHERE slug = 'binary-search'
ON CONFLICT (algorithm_id, example_number) DO NOTHING;

INSERT INTO algorithm_examples (algorithm_id, example_number, title, input_data, output_data, explanation)
SELECT id, 2, 'Target Absent from Array', 'Array: [2, 5, 8, 12, 16, 23], Target: 10', 'Index -1 (Not Found)', 'Pointers reduce until left exceeds right pointer without finding target 10.'
FROM algorithms WHERE slug = 'binary-search'
ON CONFLICT (algorithm_id, example_number) DO NOTHING;

-- Seed Code Implementations for Binary Search
INSERT INTO algorithm_implementations (algorithm_id, language, code, explanation, display_order)
SELECT id, 'JAVA',
'public class BinarySearch {
    public static int binarySearch(int[] arr, int target) {
        int left = 0, right = arr.length - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (arr[mid] == target) return mid;
            if (arr[mid] < target) left = mid + 1;
            else right = mid - 1;
        }
        return -1;
    }
}', 'Java binary search preventing integer overflow using mid calculation.', 1
FROM algorithms WHERE slug = 'binary-search'
ON CONFLICT (algorithm_id, language) DO NOTHING;

INSERT INTO algorithm_implementations (algorithm_id, language, code, explanation, display_order)
SELECT id, 'PYTHON',
'def binary_search(arr, target):
    left, right = 0, len(arr) - 1
    while left <= right:
        mid = (left + right) // 2
        if arr[mid] == target:
            return mid
        elif arr[mid] < target:
            left = mid + 1
        else:
            right = mid - 1
    return -1', 'Pythonic binary search implementation.', 2
FROM algorithms WHERE slug = 'binary-search'
ON CONFLICT (algorithm_id, language) DO NOTHING;

-- Seed Related Algorithms for Binary Search
INSERT INTO algorithm_related (algorithm_id, related_algorithm_id)
SELECT a1.id, a2.id
FROM algorithms a1, algorithms a2
WHERE a1.slug = 'binary-search' AND a2.slug IN ('linear-search', 'quick-sort')
ON CONFLICT (algorithm_id, related_algorithm_id) DO NOTHING;
