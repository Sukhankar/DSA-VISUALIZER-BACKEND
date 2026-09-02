-- ==============================================================================
-- Flyway Migration V22: Seed Contracts for Pre-built Backend Generators
-- ==============================================================================

-- 1. Selection Sort
INSERT INTO algorithm_visualization_contracts (
    algorithm_id, visualization_type, data_structure_type, input_mode, input_schema, sample_input, generator_key, renderer_key, step_schema, visualization_config, learning_visualization_description, supports_custom_input, max_input_size
)
SELECT id, 'ARRAY_BARS', 'ARRAY', 'CUSTOMIZABLE', '{"type":"object","properties":{"input":{"type":"array","items":{"type":"integer"}}}}', '{"input":[64,25,12,22,11]}', 'selection-sort', 'array', '{"type":"object"}', '{"inputLabel":"Array Elements","placeholder":"64,25,12,22,11"}', 'Selection Sort repeatedly finds the minimum element from the unsorted segment.', TRUE, 25
FROM algorithms WHERE slug = 'selection-sort'
ON CONFLICT (algorithm_id) DO UPDATE SET renderer_key = EXCLUDED.renderer_key, generator_key = EXCLUDED.generator_key;

-- 2. Insertion Sort
INSERT INTO algorithm_visualization_contracts (
    algorithm_id, visualization_type, data_structure_type, input_mode, input_schema, sample_input, generator_key, renderer_key, step_schema, visualization_config, learning_visualization_description, supports_custom_input, max_input_size
)
SELECT id, 'ARRAY_BARS', 'ARRAY', 'CUSTOMIZABLE', '{"type":"object","properties":{"input":{"type":"array","items":{"type":"integer"}}}}', '{"input":[12,11,13,5,6]}', 'insertion-sort', 'array', '{"type":"object"}', '{"inputLabel":"Array Elements","placeholder":"12,11,13,5,6"}', 'Insertion Sort builds the final sorted array one item at a time by shifting larger elements.', TRUE, 25
FROM algorithms WHERE slug = 'insertion-sort'
ON CONFLICT (algorithm_id) DO UPDATE SET renderer_key = EXCLUDED.renderer_key, generator_key = EXCLUDED.generator_key;

-- 3. Merge Sort
INSERT INTO algorithm_visualization_contracts (
    algorithm_id, visualization_type, data_structure_type, input_mode, input_schema, sample_input, generator_key, renderer_key, step_schema, visualization_config, learning_visualization_description, supports_custom_input, max_input_size
)
SELECT id, 'ARRAY_BARS', 'ARRAY', 'CUSTOMIZABLE', '{"type":"object","properties":{"input":{"type":"array","items":{"type":"integer"}}}}', '{"input":[38,27,43,3,9,82,10]}', 'merge-sort', 'array', '{"type":"object"}', '{"inputLabel":"Array Elements","placeholder":"38,27,43,3,9,82,10"}', 'Merge Sort divides the array into halves, sorts them recursively, and merges sorted subarrays.', TRUE, 25
FROM algorithms WHERE slug = 'merge-sort'
ON CONFLICT (algorithm_id) DO UPDATE SET renderer_key = EXCLUDED.renderer_key, generator_key = EXCLUDED.generator_key;

-- 4. Quick Sort
INSERT INTO algorithm_visualization_contracts (
    algorithm_id, visualization_type, data_structure_type, input_mode, input_schema, sample_input, generator_key, renderer_key, step_schema, visualization_config, learning_visualization_description, supports_custom_input, max_input_size
)
SELECT id, 'ARRAY_BARS', 'ARRAY', 'CUSTOMIZABLE', '{"type":"object","properties":{"input":{"type":"array","items":{"type":"integer"}}}}', '{"input":[10,80,30,90,40,50,70]}', 'quick-sort', 'array', '{"type":"object"}', '{"inputLabel":"Array Elements","placeholder":"10,80,30,90,40,50,70"}', 'Quick Sort selects a pivot element and partitions the array into sub-arrays around the pivot.', TRUE, 25
FROM algorithms WHERE slug = 'quick-sort'
ON CONFLICT (algorithm_id) DO UPDATE SET renderer_key = EXCLUDED.renderer_key, generator_key = EXCLUDED.generator_key;

-- 5. AVL Tree Rotations & Balance
INSERT INTO algorithm_visualization_contracts (
    algorithm_id, visualization_type, data_structure_type, input_mode, input_schema, sample_input, generator_key, renderer_key, step_schema, visualization_config, learning_visualization_description, supports_custom_input, max_input_size
)
SELECT id, 'TREE_NODES', 'TREE', 'CUSTOMIZABLE', '{"type":"object","properties":{"input":{"type":"array","items":{"type":"integer"}}}}', '{"input":[10,20,30,40,50,25]}', 'avl-tree-extreme', 'tree', '{"type":"object"}', '{"inputLabel":"Keys to Insert","placeholder":"10, 20, 30, 40, 50, 25"}', 'AVL Tree balances itself via single and double rotations after each insertion.', TRUE, 20
FROM algorithms WHERE slug = 'avl-tree-extreme'
ON CONFLICT (algorithm_id) DO UPDATE SET renderer_key = EXCLUDED.renderer_key, generator_key = EXCLUDED.generator_key;

-- 6. Binary Search Tree
INSERT INTO algorithm_visualization_contracts (
    algorithm_id, visualization_type, data_structure_type, input_mode, input_schema, sample_input, generator_key, renderer_key, step_schema, visualization_config, learning_visualization_description, supports_custom_input, max_input_size
)
SELECT id, 'TREE_NODES', 'TREE', 'CUSTOMIZABLE', '{"type":"object","properties":{"input":{"type":"array","items":{"type":"integer"}}}}', '{"input":[50,30,70,20,40,60,80]}', 'binary-search-tree', 'tree', '{"type":"object"}', '{"inputLabel":"Node Values","placeholder":"50, 30, 70, 20, 40, 60, 80"}', 'Binary Search Tree maintains left < root < right property across all subtrees.', TRUE, 20
FROM algorithms WHERE slug = 'binary-search-tree'
ON CONFLICT (algorithm_id) DO UPDATE SET renderer_key = EXCLUDED.renderer_key, generator_key = EXCLUDED.generator_key;

-- 7. Breadth First Search (BFS)
INSERT INTO algorithm_visualization_contracts (
    algorithm_id, visualization_type, data_structure_type, input_mode, input_schema, sample_input, generator_key, renderer_key, step_schema, visualization_config, learning_visualization_description, supports_custom_input, max_input_size
)
SELECT id, 'GRAPH_NETWORK', 'GRAPH', 'CUSTOMIZABLE', '{"type":"object","properties":{"graph":{"type":"object"}}}', '{"graph":{"nodes":["A","B","C","D","E"],"edges":[{"from":"A","to":"B"},{"from":"A","to":"C"},{"from":"B","to":"D"},{"from":"C","to":"E"}],"startNode":"A"}}', 'breadth-first-search', 'graph', '{"type":"object"}', '{"inputLabel":"Graph & Start Node","placeholder":"Nodes A, B, C..."}', 'Breadth-First Search traverses graph level-by-level using a queue data structure.', TRUE, 30
FROM algorithms WHERE slug = 'breadth-first-search'
ON CONFLICT (algorithm_id) DO UPDATE SET renderer_key = EXCLUDED.renderer_key, generator_key = EXCLUDED.generator_key;

-- 8. Depth First Search (DFS)
INSERT INTO algorithm_visualization_contracts (
    algorithm_id, visualization_type, data_structure_type, input_mode, input_schema, sample_input, generator_key, renderer_key, step_schema, visualization_config, learning_visualization_description, supports_custom_input, max_input_size
)
SELECT id, 'GRAPH_NETWORK', 'GRAPH', 'CUSTOMIZABLE', '{"type":"object","properties":{"graph":{"type":"object"}}}', '{"graph":{"nodes":["A","B","C","D","E"],"edges":[{"from":"A","to":"B"},{"from":"A","to":"C"},{"from":"B","to":"D"},{"from":"C","to":"E"}],"startNode":"A"}}', 'depth-first-search', 'graph', '{"type":"object"}', '{"inputLabel":"Graph & Start Node","placeholder":"Nodes A, B, C..."}', 'Depth-First Search explores as far as possible along each branch before backtracking.', TRUE, 30
FROM algorithms WHERE slug = 'depth-first-search'
ON CONFLICT (algorithm_id) DO UPDATE SET renderer_key = EXCLUDED.renderer_key, generator_key = EXCLUDED.generator_key;

-- 9. Dijkstras Algorithm
INSERT INTO algorithm_visualization_contracts (
    algorithm_id, visualization_type, data_structure_type, input_mode, input_schema, sample_input, generator_key, renderer_key, step_schema, visualization_config, learning_visualization_description, supports_custom_input, max_input_size
)
SELECT id, 'GRAPH_NETWORK', 'GRAPH', 'CUSTOMIZABLE', '{"type":"object","properties":{"graph":{"type":"object"}}}', '{"graph":{"nodes":["A","B","C","D"],"edges":[{"from":"A","to":"B","weight":4},{"from":"A","to":"C","weight":2},{"from":"C","to":"B","weight":1},{"from":"B","to":"D","weight":5}],"startNode":"A"}}', 'dijkstras-algorithm', 'graph', '{"type":"object"}', '{"inputLabel":"Weighted Graph","placeholder":"Nodes A, B, C with weights"}', 'Dijkstras Algorithm computes shortest path distances from a source node using a priority queue.', TRUE, 30
FROM algorithms WHERE slug = 'dijkstras-algorithm'
ON CONFLICT (algorithm_id) DO UPDATE SET renderer_key = EXCLUDED.renderer_key, generator_key = EXCLUDED.generator_key;

-- 10. Fibonacci Dynamic Programming
INSERT INTO algorithm_visualization_contracts (
    algorithm_id, visualization_type, data_structure_type, input_mode, input_schema, sample_input, generator_key, renderer_key, step_schema, visualization_config, learning_visualization_description, supports_custom_input, max_input_size
)
SELECT id, 'DP_TABLE', 'MATRIX', 'CUSTOMIZABLE', '{"type":"object","properties":{"target":{"type":"integer"}}}', '{"target":6}', 'fibonacci-dynamic-programming', 'dp-table', '{"type":"object"}', '{"inputLabel":"Nth Term","placeholder":"6"}', 'Fibonacci DP fills a 1D state array storing subproblem solutions to achieve O(N) time efficiency.', TRUE, 30
FROM algorithms WHERE slug = 'fibonacci-dynamic-programming'
ON CONFLICT (algorithm_id) DO UPDATE SET renderer_key = EXCLUDED.renderer_key, generator_key = EXCLUDED.generator_key;

-- 11. 0/1 Knapsack Problem DP
INSERT INTO algorithm_visualization_contracts (
    algorithm_id, visualization_type, data_structure_type, input_mode, input_schema, sample_input, generator_key, renderer_key, step_schema, visualization_config, learning_visualization_description, supports_custom_input, max_input_size
)
SELECT id, 'DP_TABLE', 'MATRIX', 'CUSTOMIZABLE', '{"type":"object","properties":{"weights":{"type":"array"},"values":{"type":"array"},"capacity":{"type":"integer"}}}', '{"weights":[1,2,3],"values":[10,15,40],"capacity":6}', 'knapsack-01-dp-hard', 'dp-table', '{"type":"object"}', '{"inputLabel":"Items & Capacity","placeholder":"weights, values, capacity"}', '0/1 Knapsack computes the 2D DP matrix maximizing total value under given weight constraints.', TRUE, 30
FROM algorithms WHERE slug = 'knapsack-01-dp-hard'
ON CONFLICT (algorithm_id) DO UPDATE SET renderer_key = EXCLUDED.renderer_key, generator_key = EXCLUDED.generator_key;
