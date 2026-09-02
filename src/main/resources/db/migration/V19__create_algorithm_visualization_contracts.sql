-- ==============================================================================
-- Flyway Migration V19: Create Algorithm Visualization Contracts
-- Universal contract engine 1:1 table extending algorithms
-- ==============================================================================

CREATE TABLE IF NOT EXISTS algorithm_visualization_contracts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    algorithm_id UUID NOT NULL UNIQUE REFERENCES algorithms(id) ON DELETE CASCADE,
    visualization_type VARCHAR(64) NOT NULL,
    data_structure_type VARCHAR(64) NOT NULL,
    input_mode VARCHAR(32) NOT NULL DEFAULT 'CUSTOMIZABLE',
    input_schema TEXT,
    sample_input TEXT,
    generator_key VARCHAR(64) NOT NULL,
    renderer_key VARCHAR(64) NOT NULL,
    step_schema TEXT,
    visualization_config TEXT,
    learning_visualization_description TEXT,
    supports_custom_input BOOLEAN NOT NULL DEFAULT TRUE,
    max_input_size INT NOT NULL DEFAULT 100,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_vis_contracts_algorithm_id ON algorithm_visualization_contracts(algorithm_id);

-- Seed pilot READY visualization contract for Bubble Sort
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
    '{"type":"array","itemType":"integer","minItems":2,"maxItems":20}',
    '[5, 1, 4, 2, 8]',
    'bubble-sort',
    'array',
    '{"type":"object","properties":{"array":{"type":"array","items":{"type":"integer"}},"indices":{"type":"array","items":{"type":"integer"}},"sortedIndices":{"type":"array","items":{"type":"integer"}},"swapped":{"type":"boolean"}},"required":["array"]}',
    '{"inputLabel":"Array Elements","placeholder":"5, 1, 4, 2, 8"}',
    'Bubble Sort repeatedly compares adjacent elements and swaps them if they are in wrong order.',
    TRUE,
    20
FROM algorithms WHERE slug = 'bubble-sort'
ON CONFLICT (algorithm_id) DO NOTHING;
