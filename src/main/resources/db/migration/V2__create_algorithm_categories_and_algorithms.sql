CREATE TABLE algorithm_categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    name VARCHAR(100) NOT NULL UNIQUE,

    slug VARCHAR(100) NOT NULL UNIQUE,

    description VARCHAR(500),

    created_at TIMESTAMP WITH TIME ZONE NOT NULL
        DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE algorithms (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    category_id UUID NOT NULL,

    name VARCHAR(150) NOT NULL,

    slug VARCHAR(150) NOT NULL UNIQUE,

    description TEXT,

    difficulty VARCHAR(20) NOT NULL,

    time_complexity VARCHAR(100),

    space_complexity VARCHAR(100),

    created_at TIMESTAMP WITH TIME ZONE NOT NULL
        DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_algorithm_category
        FOREIGN KEY (category_id)
        REFERENCES algorithm_categories(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_algorithm_difficulty
        CHECK (difficulty IN ('EASY', 'MEDIUM', 'HARD'))
);


CREATE INDEX idx_algorithms_category_id
ON algorithms(category_id);


CREATE INDEX idx_algorithms_slug
ON algorithms(slug);
