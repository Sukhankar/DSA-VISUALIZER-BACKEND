-- Fix invalid achievement category for XP_EXPERT
UPDATE achievements SET category = 'XP_COLLECTOR' WHERE code = 'XP_EXPERT';
UPDATE achievements SET category = 'XP_COLLECTOR' WHERE category = 'XP_EXPERT';
