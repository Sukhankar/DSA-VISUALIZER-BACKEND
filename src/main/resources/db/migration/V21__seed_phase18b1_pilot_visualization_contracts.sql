-- ==============================================================================
-- Flyway Migration V21: Seed Phase 18B-1 Pilot Visualization Contracts
-- Complete contract seeds for 5 pilot Array & Search algorithms
-- ==============================================================================

-- 1. Two Sum (Arrays - Hash / Complement Array Lookup)
INSERT INTO algorithm_visualization_contracts (
    algorithm_id,
    visualization_type,
    data_structure_type,
    input_mode,
    input_schema,
    sample_input,
    generator_key,
    renderer_key,
    step_schema,
    visualization_config,
    learning_visualization_description,
    supports_custom_input,
    max_input_size
)
SELECT 
    id,
    'ARRAY_BARS',
    'ARRAY',
    'CUSTOMIZABLE',
    '{"type":"object","properties":{"input":{"type":"array","items":{"type":"integer"}},"target":{"type":"integer"}}}',
    '{"input":[2,7,11,15],"target":9}',
    'two-sum',
    'array',
    '{"type":"object","properties":{"array":{"type":"array","items":{"type":"integer"}},"currentIndex":{"type":"integer"},"targetIndex":{"type":"integer"},"found":{"type":"boolean"}}}',
    '{"inputLabel":"Array & Target","placeholder":"input: 2,7,11,15, target: 9"}',
    'Two Sum inspects each element and searches for its complement (target - current) in a hash lookup structure.',
    TRUE,
    20
FROM algorithms WHERE slug = 'two-sum'
ON CONFLICT (algorithm_id) DO UPDATE SET
    renderer_key = EXCLUDED.renderer_key,
    generator_key = EXCLUDED.generator_key,
    sample_input = EXCLUDED.sample_input;

-- 2. Binary Search (Searching - Pointer Array with LEFT, MID, RIGHT)
INSERT INTO algorithm_visualization_contracts (
    algorithm_id,
    visualization_type,
    data_structure_type,
    input_mode,
    input_schema,
    sample_input,
    generator_key,
    renderer_key,
    step_schema,
    visualization_config,
    learning_visualization_description,
    supports_custom_input,
    max_input_size
)
SELECT 
    id,
    'POINTER_ARRAY',
    'ARRAY',
    'CUSTOMIZABLE',
    '{"type":"object","properties":{"input":{"type":"array","items":{"type":"integer"}},"target":{"type":"integer"}}}',
    '{"input":[1,3,5,7,9,11,13],"target":7}',
    'binary-search',
    'pointer-array',
    '{"type":"object","properties":{"array":{"type":"array","items":{"type":"integer"}},"left":{"type":"integer"},"mid":{"type":"integer"},"right":{"type":"integer"},"found":{"type":"boolean"}}}',
    '{"inputLabel":"Sorted Array & Target","placeholder":"input: 1,3,5,7,9, target: 7"}',
    'Binary Search halves the search space at each step by comparing the target with the middle element of the sorted array.',
    TRUE,
    20
FROM algorithms WHERE slug = 'binary-search'
ON CONFLICT (algorithm_id) DO UPDATE SET
    renderer_key = EXCLUDED.renderer_key,
    generator_key = EXCLUDED.generator_key,
    sample_input = EXCLUDED.sample_input;

-- 3. Linear Search (Searching - Sequential Pointer Array)
INSERT INTO algorithm_visualization_contracts (
    algorithm_id,
    visualization_type,
    data_structure_type,
    input_mode,
    input_schema,
    sample_input,
    generator_key,
    renderer_key,
    step_schema,
    visualization_config,
    learning_visualization_description,
    supports_custom_input,
    max_input_size
)
SELECT 
    id,
    'POINTER_ARRAY',
    'ARRAY',
    'CUSTOMIZABLE',
    '{"type":"object","properties":{"input":{"type":"array","items":{"type":"integer"}},"target":{"type":"integer"}}}',
    '{"input":[5,2,8,1,9],"target":8}',
    'linear-search',
    'pointer-array',
    '{"type":"object","properties":{"array":{"type":"array","items":{"type":"integer"}},"currentIndex":{"type":"integer"},"found":{"type":"boolean"}}}',
    '{"inputLabel":"Array & Target","placeholder":"input: 5,2,8,1,9, target: 8"}',
    'Linear Search sequentially checks each element from start to end until the target is found or array bounds are reached.',
    TRUE,
    20
FROM algorithms WHERE slug = 'linear-search'
ON CONFLICT (algorithm_id) DO UPDATE SET
    renderer_key = EXCLUDED.renderer_key,
    generator_key = EXCLUDED.generator_key,
    sample_input = EXCLUDED.sample_input;

-- 4. Kadane''s Algorithm (Arrays - Dynamic Subarray Window)
INSERT INTO algorithm_visualization_contracts (
    algorithm_id,
    visualization_type,
    data_structure_type,
    input_mode,
    input_schema,
    sample_input,
    generator_key,
    renderer_key,
    step_schema,
    visualization_config,
    learning_visualization_description,
    supports_custom_input,
    max_input_size
)
SELECT 
    id,
    'ARRAY_BARS',
    'ARRAY',
    'CUSTOMIZABLE',
    '{"type":"object","properties":{"input":{"type":"array","items":{"type":"integer"}}}}',
    '{"input":[-2,1,-3,4,-1,2,1,-5,4]}',
    'kadanes-algorithm',
    'array',
    '{"type":"object","properties":{"array":{"type":"array","items":{"type":"integer"}},"currentSum":{"type":"integer"},"maxSum":{"type":"integer"},"currentIndex":{"type":"integer"}}}',
    '{"inputLabel":"Array Elements","placeholder":"-2, 1, -3, 4, -1, 2, 1, -5, 4"}',
    'Kadanes Algorithm tracks the maximum contiguous subarray sum in O(N) time using dynamic programming principles.',
    TRUE,
    25
FROM algorithms WHERE slug = 'kadanes-algorithm'
ON CONFLICT (algorithm_id) DO UPDATE SET
    renderer_key = EXCLUDED.renderer_key,
    generator_key = EXCLUDED.generator_key,
    sample_input = EXCLUDED.sample_input;

-- 5. KMP Algorithm (Searching - String Character Alignment & LPS Table)
INSERT INTO algorithm_visualization_contracts (
    algorithm_id,
    visualization_type,
    data_structure_type,
    input_mode,
    input_schema,
    sample_input,
    generator_key,
    renderer_key,
    step_schema,
    visualization_config,
    learning_visualization_description,
    supports_custom_input,
    max_input_size
)
SELECT 
    id,
    'STRING_MATCH',
    'STRING',
    'CUSTOMIZABLE',
    '{"type":"object","properties":{"text":{"type":"string"},"pattern":{"type":"string"}}}',
    '{"text":"ABABDABACDABABCABAB","pattern":"ABABCABAB"}',
    'kmp-string-matching-hard',
    'string',
    '{"type":"object","properties":{"text":{"type":"string"},"pattern":{"type":"string"},"textIndex":{"type":"integer"},"patternIndex":{"type":"integer"},"lps":{"type":"array","items":{"type":"integer"}}}}',
    '{"inputLabel":"Text & Pattern","placeholder":"text: ABABD..., pattern: ABABC..."}',
    'Knuth-Morris-Pratt (KMP) pattern searching uses a Longest Prefix Suffix (LPS) array to avoid redundant comparisons.',
    TRUE,
    50
FROM algorithms WHERE slug = 'kmp-string-matching-hard'
ON CONFLICT (algorithm_id) DO UPDATE SET
    renderer_key = EXCLUDED.renderer_key,
    generator_key = EXCLUDED.generator_key,
    sample_input = EXCLUDED.sample_input;
