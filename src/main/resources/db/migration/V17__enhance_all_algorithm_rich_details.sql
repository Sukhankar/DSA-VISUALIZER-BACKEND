-- ==============================================================================
-- Flyway Migration V17: Enhance All Algorithms with GeeksforGeeks & StackOverflow Grade Rich Theory
-- ==============================================================================

-- 1. Quick Sort
UPDATE algorithms
SET overview = 'Quick Sort is a highly efficient, divide-and-conquer comparison-based sorting algorithm. It selects an element as a "pivot" and partitions the array such that elements smaller than the pivot are moved to its left, and elements greater are moved to its right. The sub-arrays are then sorted recursively. It is the default sorting algorithm in standard C++ std::sort and many high-performance standard libraries.',
    when_to_use = 'Ideal for general-purpose in-place sorting of large datasets where average-case performance O(N log N) with low cache-miss overhead is required.',
    advantages = '• In-place sorting requiring minimal auxiliary stack space O(log N).\n• Outstanding practical performance due to locality of reference and minimal cache misses.\n• Highly parallelizable across sub-arrays.',
    limitations = '• Unstable sort: does not guarantee preservation of relative order for duplicate elements.\n• O(N²) worst-case performance if pivot selection is poor (e.g. already sorted array with last element pivot).',
    constraints = '• 1 <= input.length <= 100000\n• Random access memory structure required (arrays/vectors).'
WHERE slug IN ('quick-sort', 'quicksort');

-- Seed Examples for Quick Sort
INSERT INTO algorithm_examples (algorithm_id, example_number, title, input_data, output_data, explanation)
SELECT id, 1, 'Standard Unsorted Array', '[10, 80, 30, 90, 40, 50, 70]', '[10, 30, 40, 50, 70, 80, 90]', 'Pivot chosen as 70. Partitioning arranges elements into [<70]: [10, 30, 40, 50] and [>70]: [90, 80]. Recursive calls independently sort left and right partitions.'
FROM algorithms WHERE slug IN ('quick-sort', 'quicksort')
ON CONFLICT (algorithm_id, example_number) DO UPDATE SET explanation = EXCLUDED.explanation;

-- Seed Implementations for Quick Sort
INSERT INTO algorithm_implementations (algorithm_id, language, code, explanation, display_order)
SELECT id, 'JAVA',
'public class QuickSort {
    public static void quickSort(int[] arr, int low, int high) {
        if (low < high) {
            int pIdx = partition(arr, low, high);
            quickSort(arr, low, pIdx - 1);
            quickSort(arr, pIdx + 1, high);
        }
    }
    private static int partition(int[] arr, int low, int high) {
        int pivot = arr[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (arr[j] <= pivot) {
                i++;
                int temp = arr[i]; arr[i] = arr[j]; arr[j] = temp;
            }
        }
        int temp = arr[i + 1]; arr[i + 1] = arr[high]; arr[high] = temp;
        return i + 1;
    }
}', 'Standard Lomuto partition scheme in Java.', 1
FROM algorithms WHERE slug IN ('quick-sort', 'quicksort')
ON CONFLICT (algorithm_id, language) DO UPDATE SET code = EXCLUDED.code;

INSERT INTO algorithm_implementations (algorithm_id, language, code, explanation, display_order)
SELECT id, 'PYTHON',
'def quick_sort(arr, low=0, high=None):
    if high is None:
        high = len(arr) - 1
    if low < high:
        p_idx = partition(arr, low, high)
        quick_sort(arr, low, p_idx - 1)
        quick_sort(arr, p_idx + 1, high)
    return arr

def partition(arr, low, high):
    pivot = arr[high]
    i = low - 1
    for j in range(low, high):
        if arr[j] <= pivot:
            i += 1
            arr[i], arr[j] = arr[j], arr[i]
    arr[i + 1], arr[high] = arr[high], arr[i + 1]
    return i + 1', 'Clean Python recursive quick sort implementation.', 2
FROM algorithms WHERE slug IN ('quick-sort', 'quicksort')
ON CONFLICT (algorithm_id, language) DO UPDATE SET code = EXCLUDED.code;


-- 2. Merge Sort
UPDATE algorithms
SET overview = 'Merge Sort is a classic comparison-based sorting algorithm based on the Divide and Conquer strategy. It works by recursively dividing the input array into two equal halves until each sub-array contains a single element (base case). It then merges adjacent sorted sub-arrays back together in sorted order to build the final sorted result.',
    when_to_use = 'Best for linked lists, external sorting of massive disk datasets, or applications requiring guaranteed stable O(N log N) performance regardless of input initial order.',
    advantages = '• Guaranteed O(N log N) worst-case time complexity.\n• Fully stable sort algorithm.\n• Highly suited for sequential access data structures like Linked Lists.',
    limitations = '• Requires O(N) additional memory space for auxiliary arrays during merging.\n• Slightly higher constant factors than Quick Sort for small array sizes.',
    constraints = '• 1 <= input.length <= 100000\n• Additional O(N) memory required'
WHERE slug IN ('merge-sort', 'mergesort');

-- Seed Examples for Merge Sort
INSERT INTO algorithm_examples (algorithm_id, example_number, title, input_data, output_data, explanation)
SELECT id, 1, 'Divide & Merge Example', '[38, 27, 43, 3, 9, 82, 10]', '[3, 9, 10, 27, 38, 82, 43]', 'Array splits into [38, 27, 43, 3] and [9, 82, 10]. Sub-arrays divide down to single elements and merge back together: [27, 38] with [3, 43] -> [3, 27, 38, 43], then combined with [9, 10, 82].'
FROM algorithms WHERE slug IN ('merge-sort', 'mergesort')
ON CONFLICT (algorithm_id, example_number) DO UPDATE SET explanation = EXCLUDED.explanation;

-- Seed Implementations for Merge Sort
INSERT INTO algorithm_implementations (algorithm_id, language, code, explanation, display_order)
SELECT id, 'JAVA',
'public class MergeSort {
    public static void mergeSort(int[] arr, int l, int r) {
        if (l < r) {
            int m = l + (r - l) / 2;
            mergeSort(arr, l, m);
            mergeSort(arr, m + 1, r);
            merge(arr, l, m, r);
        }
    }
    private static void merge(int[] arr, int l, int m, int r) {
        int n1 = m - l + 1, n2 = r - m;
        int[] L = new int[n1]; int[] R = new int[n2];
        for (int i = 0; i < n1; ++i) L[i] = arr[l + i];
        for (int j = 0; j < n2; ++j) R[j] = arr[m + 1 + j];
        int i = 0, j = 0, k = l;
        while (i < n1 && j < n2) {
            if (L[i] <= R[j]) arr[k++] = L[i++];
            else arr[k++] = R[j++];
        }
        while (i < n1) arr[k++] = L[i++];
        while (j < n2) arr[k++] = R[j++];
    }
}', 'Standard Merge Sort implementation in Java.', 1
FROM algorithms WHERE slug IN ('merge-sort', 'mergesort')
ON CONFLICT (algorithm_id, language) DO UPDATE SET code = EXCLUDED.code;

INSERT INTO algorithm_implementations (algorithm_id, language, code, explanation, display_order)
SELECT id, 'PYTHON',
'def merge_sort(arr):
    if len(arr) > 1:
        mid = len(arr) // 2
        L = arr[:mid]
        R = arr[mid:]
        merge_sort(L)
        merge_sort(R)
        i = j = k = 0
        while i < len(L) and j < len(R):
            if L[i] <= R[j]:
                arr[k] = L[i]; i += 1
            else:
                arr[k] = R[j]; j += 1
            k += 1
        while i < len(L):
            arr[k] = L[i]; i += 1; k += 1
        while j < len(R):
            arr[k] = R[j]; j += 1; k += 1
    return arr', 'Pythonic divide and merge implementation.', 2
FROM algorithms WHERE slug IN ('merge-sort', 'mergesort')
ON CONFLICT (algorithm_id, language) DO UPDATE SET code = EXCLUDED.code;


-- 3. Selection Sort
UPDATE algorithms
SET overview = 'Selection Sort is an in-place comparison sorting algorithm. It divides the input array into two parts: a sorted sublist at the left end and an unsorted sublist at the right end. In each pass, it finds the smallest element from the unsorted sublist and swaps it with the leftmost unsorted element, expanding the sorted sublist boundary by one.',
    when_to_use = 'Useful when memory write operations are extremely costly (since it makes at most O(N) swaps), or for very small arrays.',
    advantages = '• In-place algorithm requiring O(1) auxiliary memory.\n• Performs a maximum of O(N) swaps, making it ideal when memory writing is expensive.',
    limitations = '• O(N²) time complexity for all cases (best, average, and worst).\n• Unstable sort in standard implementation.',
    constraints = '• 1 <= input.length <= 100'
WHERE slug IN ('selection-sort', 'selectionsort');

-- Seed Implementations for Selection Sort
INSERT INTO algorithm_implementations (algorithm_id, language, code, explanation, display_order)
SELECT id, 'JAVA',
'public class SelectionSort {
    public static void selectionSort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < n; j++) {
                if (arr[j] < arr[minIdx]) minIdx = j;
            }
            int temp = arr[minIdx]; arr[minIdx] = arr[i]; arr[i] = temp;
        }
    }
}', 'Selection Sort in Java with minimal memory swaps.', 1
FROM algorithms WHERE slug IN ('selection-sort', 'selectionsort')
ON CONFLICT (algorithm_id, language) DO UPDATE SET code = EXCLUDED.code;


-- 4. Insertion Sort
UPDATE algorithms
SET overview = 'Insertion Sort builds the final sorted array one item at a time. It takes each element from the unsorted portion and shifts all greater elements in the sorted portion one position to the right, inserting the candidate element into its correct sorted location. It operates similarly to how cards are sorted in a human hand.',
    when_to_use = 'Highly effective for small arrays (N <= 25) or nearly-sorted datasets where it achieves linear time O(N). Often used as the base case in hybrid algorithms like Timsort.',
    advantages = '• Simple, intuitive implementation.\n• In-place O(1) space and fully stable.\n• Adaptive: O(N) linear time for nearly-sorted data.',
    limitations = '• O(N²) worst-case time complexity on reverse-sorted inputs.',
    constraints = '• 1 <= input.length <= 1000'
WHERE slug IN ('insertion-sort', 'insertionsort');


-- 5. Breadth-First Search (BFS)
UPDATE algorithms
SET overview = 'Breadth-First Search (BFS) is a fundamental graph traversal algorithm. Starting from a source node, it explores all neighboring vertices at the current depth level before proceeding to vertices at the next depth level. It utilizes a Queue (FIFO data structure) to maintain the traversal frontier.',
    when_to_use = 'Essential for finding the shortest path in unweighted graphs, level-order tree traversal, connected components, and finding nearest targets.',
    advantages = '• Guarantees finding the shortest path (fewest edges) in unweighted graphs.\n• Never gets trapped in infinite depth loops.',
    limitations = '• Requires O(V) space to maintain queue and visited set.',
    constraints = '• Graph with vertices V and edges E\n• Adjacency list/matrix representation'
WHERE slug IN ('breadth-first-search', 'bfs');


-- 6. Depth-First Search (DFS)
UPDATE algorithms
SET overview = 'Depth-First Search (DFS) explores a graph or tree by advancing as far as possible along each branch before backtracking. It relies on a Stack (LIFO data structure) or system call stack recursion to visit vertices.',
    when_to_use = 'Used for topological sorting, cycle detection, strongly connected components, solving mazes, and pathfinding in decision trees.',
    advantages = '• Requires less memory space O(H) where H is maximum depth tree path.\n• Ideal for checking graph connectivity and topological ordering.',
    limitations = '• Does not guarantee shortest path in general graphs.\n• Risk of stack overflow on deep paths if un-memoized.',
    constraints = '• Graph with vertices V and edges E'
WHERE slug IN ('depth-first-search', 'dfs');


-- 7. Dijkstra''s Algorithm
UPDATE algorithms
SET overview = 'Dijkstra''s Algorithm computes the shortest paths from a single source vertex to all other vertices in a weighted graph with non-negative edge weights. It uses a Greedy approach, continuously expanding the vertex with the minimum tentative distance using a Priority Queue (Min-Heap).',
    when_to_use = 'Used in GPS navigation, network routing protocols (OSPF), social network connections, and robotics path planning.',
    advantages = '• Optimal O((V + E) log V) time complexity using Min-Heap.\n• Guarantees shortest path for non-negative edge weights.',
    limitations = '• Fails on graphs containing negative edge weights (Bellman-Ford is required for negative weights).',
    constraints = '• Non-negative edge weights W >= 0'
WHERE slug IN ('dijkstras-algorithm', 'dijkstra');
