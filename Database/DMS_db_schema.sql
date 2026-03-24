-- DMS Database Schema v2
-- All table and column names are lowercase without quoted identifiers
-- to avoid case-sensitivity issues with Hibernate/JPA
BEGIN;

-- 1. No dependencies
CREATE TABLE IF NOT EXISTS public.cooperative
(
    cooperative_id       bigserial NOT NULL,
    cooperative_name     text NOT NULL,
    cooperative_location text NOT NULL,
    contact_email        text NOT NULL,
    phone_number         text NOT NULL,
    created_at           timestamp DEFAULT now(),
    last_updated         timestamp DEFAULT now(),
    PRIMARY KEY (cooperative_id)
);

-- 2. No dependencies
CREATE TABLE IF NOT EXISTS public.groups
(
    group_id   bigserial NOT NULL,
    group_name text NOT NULL,
    PRIMARY KEY (group_id)
);

-- 3. Depends on: groups
CREATE TABLE IF NOT EXISTS public.materials
(
    material_id    bigserial NOT NULL,
    material_name  text NOT NULL,
    material_group bigint,
    PRIMARY KEY (material_id),
    FOREIGN KEY (material_group) REFERENCES public.groups(group_id)
);

-- 4. No dependencies
CREATE TABLE IF NOT EXISTS public.buyers
(
    buyer_id   bigserial NOT NULL,
    buyer_name text NOT NULL,
    PRIMARY KEY (buyer_id)
);

-- 5. Depends on: cooperative
CREATE TABLE IF NOT EXISTS public.workers
(
    worker_id   bigserial NOT NULL,
    worker_name text NOT NULL,
    cooperative bigint NOT NULL,
    cpf         bytea NOT NULL,
    user_type   char NOT NULL,
    birth_date  date NOT NULL,
    enter_date  date NOT NULL,
    exit_date   date,
    pis         bytea NOT NULL,
    rg          bytea NOT NULL,
    gender      text,
    password    bytea NOT NULL,
    email       text NOT NULL,
    last_update date,
    PRIMARY KEY (worker_id),
    FOREIGN KEY (cooperative) REFERENCES public.cooperative(cooperative_id)
);

-- 6. Depends on: cooperative
CREATE TABLE IF NOT EXISTS public.devices
(
    device_id      bigserial NOT NULL,
    cooperative_id bigint NOT NULL,
    PRIMARY KEY (device_id),
    FOREIGN KEY (cooperative_id) REFERENCES public.cooperative(cooperative_id)
);

-- 7. Depends on: cooperative, workers
-- NULL cooperative_id means the notice applies to all cooperatives (sys admin only)
CREATE TABLE IF NOT EXISTS public.notice_board
(
    notice_id      bigserial NOT NULL,
    cooperative_id bigint,
    created_at     timestamp DEFAULT now(),
    last_updated   timestamp DEFAULT now(),
    created_by     bigint NOT NULL,
    priority       smallint DEFAULT 1,
    expires_at     timestamp,
    title          text NOT NULL,
    content        text NOT NULL,
    PRIMARY KEY (notice_id),
    FOREIGN KEY (cooperative_id) REFERENCES public.cooperative(cooperative_id),
    FOREIGN KEY (created_by) REFERENCES public.workers(worker_id)
);

-- 8. Depends on: materials, buyers
CREATE TABLE IF NOT EXISTS public.collective_sale
(
    collective_sale_id bigserial NOT NULL,
    created_at         timestamp DEFAULT now(),
    sold_at            timestamp,
    buyer_id           bigint NOT NULL,
    material_id        bigint NOT NULL,
    total_weight       numeric(10, 2) NOT NULL,
    price_kg           numeric(10, 2) NOT NULL,
    expected_sale_date timestamp NOT NULL,
    PRIMARY KEY (collective_sale_id),
    FOREIGN KEY (material_id) REFERENCES public.materials(material_id),
    FOREIGN KEY (buyer_id) REFERENCES public.buyers(buyer_id)
);

-- 9. Depends on: collective_sale, cooperative
-- Tracks each cooperative's contribution to a collective sale
CREATE TABLE IF NOT EXISTS public.collective_sale_contribution
(
    contribution_id    bigserial NOT NULL,
    collective_sale_id bigint NOT NULL,
    cooperative_id     bigint NOT NULL,
    contributed_weight numeric(10, 2) NOT NULL,
    revenue_share      numeric(10, 2),
    PRIMARY KEY (contribution_id),
    FOREIGN KEY (collective_sale_id) REFERENCES public.collective_sale(collective_sale_id),
    FOREIGN KEY (cooperative_id) REFERENCES public.cooperative(cooperative_id),
    UNIQUE (collective_sale_id, cooperative_id)
);

-- 10. Depends on: cooperative, materials
-- Allows each cooperative to have a custom price multiplier per material
CREATE TABLE IF NOT EXISTS public.cooperative_material_multiplier
(
    cooperative_material_multiplier_id uuid DEFAULT gen_random_uuid() NOT NULL,
    cooperative_id                     bigint NOT NULL,
    material_id                        bigint NOT NULL,
    multiplier_value                   double precision NOT NULL,
    last_updated                       timestamp DEFAULT now(),
    PRIMARY KEY (cooperative_material_multiplier_id),
    FOREIGN KEY (cooperative_id) REFERENCES public.cooperative(cooperative_id),
    FOREIGN KEY (material_id) REFERENCES public.materials(material_id)
);

CREATE TABLE IF NOT EXISTS public.cooperative_random_multiplier
(
    cooperative_random_multiplier_id uuid DEFAULT gen_random_uuid() NOT NULL,
    cooperative_id                     bigint NOT NULL,
    multiplier_value                   double precision NOT NULL DEFAULT 1,
    last_updated                       timestamp DEFAULT now(),
    PRIMARY KEY (cooperative_random_multiplier_id),
    FOREIGN KEY (cooperative_id) REFERENCES public.cooperative(cooperative_id),
    UNIQUE (cooperative_id)
);

-- 11. Depends on: materials, buyers, workers
CREATE TABLE IF NOT EXISTS public.sales
(
    sale_id     bigserial NOT NULL,
    created_at  timestamp DEFAULT now(),
    sold_at     timestamp,
    material    bigint NOT NULL,
    weight      numeric(10, 2) NOT NULL,
    price_kg    numeric(10, 2) NOT NULL,
    buyer       bigint NOT NULL,
    responsible bigint NOT NULL,   -- the worker responsible for the sale
    cooperative_id bigint NOT NULL,    
    expected_sale_date timestamp NOT NULL,
    PRIMARY KEY (sale_id),
    FOREIGN KEY (cooperative_id) REFERENCES public.cooperative(cooperative_id),
    FOREIGN KEY (material) REFERENCES public.materials(material_id),
    FOREIGN KEY (buyer) REFERENCES public.buyers(buyer_id),
    FOREIGN KEY (responsible) REFERENCES public.workers(worker_id)
);

-- 12. Depends on: workers, materials, devices
-- Records each individual weighing of collected material
CREATE TABLE IF NOT EXISTS public.measurements
(
    weighting_id bigserial NOT NULL,
    weight_kg    numeric(10, 2) NOT NULL,
    time_stamp   timestamp NOT NULL,
    wastepicker  bigint NOT NULL,  -- the worker who collected the material
    material     bigint NOT NULL,
    device       bigint NOT NULL,  -- the scale/device used for weighing
    bag_filled   boolean NOT NULL, -- whether the bag was completely filled
    PRIMARY KEY (weighting_id),
    FOREIGN KEY (wastepicker) REFERENCES public.workers(worker_id),
    FOREIGN KEY (material) REFERENCES public.materials(material_id),
    FOREIGN KEY (device) REFERENCES public.devices(device_id)
);

-- 13. Depends on: cooperative, materials
-- Tracks total collected, sold, and current stock per material per cooperative
CREATE TABLE IF NOT EXISTS public.stock
(
    stock_id           bigserial NOT NULL,
    cooperative        bigint NOT NULL,
    material           bigint NOT NULL,
    total_collected_kg numeric(900, 2) NOT NULL,
    total_sold_kg      numeric(900, 2) NOT NULL,
    current_stock_kg   numeric(45, 2) NOT NULL,
    PRIMARY KEY (stock_id),
    FOREIGN KEY (cooperative) REFERENCES public.cooperative(cooperative_id),
    FOREIGN KEY (material) REFERENCES public.materials(material_id)
);

-- 14. Depends on: workers, materials, cooperative
-- Tracks each worker's material contributions over a time period
CREATE TABLE IF NOT EXISTS public.worker_contributions
(
    contribution_id bigserial NOT NULL,
    wastepicker     bigint NOT NULL,
    material        bigint NOT NULL,
    cooperative     bigint NOT NULL,
    period          daterange NOT NULL, -- e.g. [2026-01-01, 2026-01-31]
    weight_kg       numeric(15, 2) NOT NULL,
    last_updated    date,
    PRIMARY KEY (contribution_id),
    FOREIGN KEY (wastepicker) REFERENCES public.workers(worker_id),
    FOREIGN KEY (material) REFERENCES public.materials(material_id),
    FOREIGN KEY (cooperative) REFERENCES public.cooperative(cooperative_id)
);
CREATE TABLE IF NOT EXISTS public.material_bag_state
(
    bag_state_id   bigserial PRIMARY KEY,
    cooperative_id bigint NOT NULL REFERENCES public.cooperative(cooperative_id),
    material_id    bigint NOT NULL REFERENCES public.materials(material_id),

    -- state
    is_begun       boolean NOT NULL DEFAULT false,
    current_kg     numeric(10,2) NOT NULL DEFAULT 0,  -- hvor meget der ligger i posen nu
    last_updated   timestamp NOT NULL DEFAULT now(),

    -- sikrer 1 "pose-status" pr (cooperative, material)
    UNIQUE (cooperative_id, material_id),

    -- simple sanity
    CHECK (current_kg >= 0)
);

-- GAMIFICATION TABLES

-- 15. Depends on: "none" - hardcoded achivement definitions
-- Manager kan kun ændre xp_reward via achievement_xp_override, ikke oprette nye rækker

CREATE TABLE IF NOT EXISTS public.achievement_definition
(
    achievement_id bigserial NOT NULL,
    achievement_key text NOT NULL UNIQUE, -- 'WEIGHT_100KG' or 'DAYS_5'
    achievement_name text NOT NULL, 
    description text NOT NULL,
    category text NOT NULL, -- 'WEIGHT' , 'DAYS_WORKED'
    threshold_value numeric(15,2) NOT NULL, -- 50 (kg), 5 (days), 10 (achievements)
    base_xp_reward int NOT NULL DEFAULT 100,
    difficulty text NOT NULL DEFAULT 'MEDIUM', -- 'EASY', 'MEDIUM', 'HARD'
    PRIMARY KEY (achievement_id)
);

-- 16. Depends on: cooperative, achievement_definition, workers
-- Manager kan kun overskrive XP-belønning per achievement per kooperativ
CREATE TABLE IF NOT EXISTS public.achievement_xp_override
(
    override_id bigserial NOT NULL,
    cooperative_id bigint NOT NULL,
    achievement_id bigint NOT NULL,
    xp_reward_override int NOT NULL,
    updated_by bigint NOT NULL, 
    updated_at timestamp DEFAULT now(),
    PRIMARY KEY (override_id),
    UNIQUE (cooperative_id, achievement_id),
    FOREIGN KEY (cooperative_id) REFERENCES public.cooperative(cooperative_id),
    FOREIGN KEY (achievement_id) REFERENCES public.achievement_definition(achievement_id),
    FOREIGN KEY (updated_by) REFERENCES public.workers(worker_id)
);

-- 17. Depends on: workers, achievement_definition, cooperative
-- Månedlig achievement-progess per worker (nulstiller ikke - ny række per måned?)
CREATE TABLE IF NOT EXISTS public.worker_achievement
(
    worker_achievement_id bigserial NOT NULL,
    worker_id bigint NOT NULL,
    achievement_id bigint NOT NULL,
    cooperative_id bigint NOT NULL,
    year_month char(7) NOT NULL, -- 'YYYY-MM'
    unlocked_at timestamp, -- NULL - hvis ikke 'achievement' er opnået endnu
    progress_value numeric(15,2) NOT NULL DEFAULT 0,
    PRIMARY KEY(worker_achievement_id),
    UNIQUE (worker_id, achievement_id, cooperative_id, year_month),
    FOREIGN KEY(worker_id) REFERENCES public.workers(worker_id),
    FOREIGN KEY(achievement_id) REFERENCES public.achievement_definition(achievement_id),
    FOREIGN KEY(cooperative_id) REFERENCES public.cooperative(cooperative_id)
);


-- 18. Depends on: cooperative
-- Pre-beregnede leaderboard rækker (opdateres hver 7. dag via scheduler)
CREATE TABLE IF NOT EXISTS public.leaderboard_snapshot
(
    snapshot_id bigserial NOT NULL,
    cooperative_id bigint NOT NULL,
    year_month char(7) NOT NULL,
    week_number int NOT NULL,
    computed_at timestamp DEFAULT now(),
    PRIMARY KEY (snapshot_id),
    UNIQUE (cooperative_id, year_month, week_number),
    FOREIGN KEY (cooperative_id) REFERENCES public.cooperative(cooperative_id)
);

-- 19. Depends on: leaderboard_snapshot, workers
CREATE TABLE IF NOT EXISTS public.leaderboard_entry
(
    entry_id bigserial NOT NULL,
    snapshot_id bigint NOT NULL,
    rank_position int NOT NULL, -- 1, 2, 3
    worker_id bigint NOT NULL,
    worker_name text NOT NULL,
    raw_xp numeric(15,2) NOT NULL, -- XP before multiplier
    final_xp numeric(15,2) NOT NULL, -- XP after multiplier -> raw_xp * random_multiplier
    random_mult double precision NOT NULL,
    PRIMARY KEY (entry_id),
    FOREIGN KEY (snapshot_id) REFERENCES public.leaderboard_snapshot(snapshot_id)
        ON DELETE CASCADE, -- hvis snapshot slettes, slettes de tilhørende leaderboard entries også
    FOREIGN KEY (worker_id) REFERENCES public.workers(worker_id)
);

-- 20. Depends on: (none)
-- Hardcoded level definitions - exponential XP curve
CREATE TABLE IF NOT EXISTS public.level_definition
(
    level_number int NOT NULL,
    level_name text NOT NULL,
    xp_required int NOT NULL, -- XP required to reach this level
    PRIMARY KEY (level_number)
);

-- Seed: Hardcoded level definitions
INSERT INTO public.level_definition (level_number, level_name, xp_required)
    (1,  'Beginner',     100),
    (2,  'Amateur',      167),
    (3,  'Apprentice',   278),
    (4,  'Collector',    464),
    (5,  'Professional', 774),
    (6,  'Expert',       1291),
    (7,  'Master',       2154),
    (8,  'Elite',        3593),
    (9,  'Champion',     5992),
    (10, 'Legend',       10000)
ON CONFLICT (level_number) DO NOTHING; -- sikrer at vi ikke får duplikater hvis vi kører seed flere gange

-- 21. Depends on: workers
-- Tracks workers global XP and level (nulstilles aldrig)
CREATE TABLE IF NOT EXISTS public.worker_xp
(
    worker_id bigint NOT NULL,
    total_xp int NOT NULL DEFAULT 0,
    current_level int NOT NULL DEFAULT 1,
    PRIMARY KEY (worker_id),
    FOREIGN KEY (worker_id) REFERENCES public.workers(worker_id),
    FOREIGN KEY (current_level) REFERENCES public.level_definition(level_number)
);

-- SEED: Hardcoded achievements (må ikke slettes/ændres - kun xp via achievement_xp_override)

INSERT INTO public.achievement_definition
    (achievement_key, achievement_name, description, category, threshold_value, base_xp_reward, difficulty)
VALUES
    -- Weight-milestones (kg collected in a month)
    ('WEIGHT_50KG', 'Beginner', 'Collect 50 kg of materials in a month', 'WEIGHT', 50, 100, 'EASY'),
    ('WEIGHT_100KG', 'Amateur', 'Collect 100 kg of materials in a month', 'WEIGHT', 100, 200, 'EASY'),
    ('WEIGHT_250KG', 'Professional', 'Collect 250 kg of materials in a month', 'WEIGHT', 250, 400, 'MEDIUM'),
    ('WEIGHT_500KG', 'Master Collector', 'Collect 500 kg of materials in a month', 'WEIGHT', 500, 750, 'HARD'),
    ('WEIGHT_1000KG', 'Legendary Collector', 'Collect 1000 kg of materials in a month', 'WEIGHT', 1000, 1500, 'HARD'),

    -- Days-worked milestones (in a month) -> our new implementations of the so-called "streak" proposed
    ('DAYS_5', 'Getting Started', 'Work at least 5 days in a month', 'DAYS_WORKED', 5, 75, 'EASY'),
    ('DAYS_10', 'On a Roll', 'Work at least 10 days in a month', 'DAYS_WORKED', 10, 150, 'MEDIUM'),
    ('DAYS_15', 'Committed Worker', 'Work at least 15 days in a month', 'DAYS_WORKED', 15, 250, 'HARD'),
    ('DAYS_20', 'Dedicated Worker', 'Work at least 20 days in a month', 'DAYS_WORKED', 20, 400, 'HARD'),
    ('DAYS_25', 'Unstoppable Worker', 'Work at least 25 days in a month', 'DAYS_WORKED', 25, 600, 'HARD'),

    -- All-achievement milestones
    ('ACHIEVEMENTS_COUNT_3', 'Rising Star', 'Unlock 3 different achievements in a month', 'ACHIEVEMENTS_COUNT', 3, 125, 'MEDIUM'),
    ('ACHIEVEMENTS_COUNT_5', 'Shining Star', 'Unlock 5 different achievements in a month', 'ACHIEVEMENTS_COUNT', 5, 300, 'HARD'),
    ('ACHIEVEMENTS_COUNT_8', 'Superstar', 'Unlock 8 different achievements in a month', 'ACHIEVEMENTS_COUNT', 8, 500, 'HARD'),
    ('ACHIEVEMENTS_COUNT_10', 'Legendary Superstar', 'Unlock 10 different achievements in a month', 'ACHIEVEMENTS_COUNT', 10, 750, 'HARD')

    -- Note fra Dwaj -> Vi har ikke nok achievements endnu til at fylde 10 forskellige i ACHIEVEMENTS_COUNT (skal tilføje mere achievements)

    ON CONFLICT (achievement_key) DO NOTHING; -- sikrer at vi ikke får duplikater hvis vi kører seed flere gange

END;