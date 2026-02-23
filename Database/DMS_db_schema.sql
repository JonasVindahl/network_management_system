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
    sold_at            timestamp NOT NULL,
    buyer_id           bigint NOT NULL,
    material_id        bigint NOT NULL,
    total_weight       numeric(10, 2) NOT NULL,
    price_kg           numeric(10, 2) NOT NULL,
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
    FOREIGN KEY (cooperative_id) REFERENCES public.cooperative(cooperative_id)
    UNIQUE (cooperative_id)
);

-- 11. Depends on: materials, buyers, workers
CREATE TABLE IF NOT EXISTS public.sales
(
    sale_id     bigserial NOT NULL,
    date        date NOT NULL,
    material    bigint NOT NULL,
    weight      numeric(10, 2) NOT NULL,
    price_kg    numeric(10, 2) NOT NULL,
    buyer       bigint NOT NULL,
    responsible bigint NOT NULL,   -- the worker responsible for the sale
    PRIMARY KEY (sale_id),
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

END;

-- TODO --
-- Add gamification based on Mario's draft