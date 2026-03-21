-- ── Forward Migration (run BEFORE deploying feat/bean-catalog) ──────────
-- Step 1: Create new tables

CREATE TABLE IF NOT EXISTS bean (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  roastery VARCHAR(255) NOT NULL,
  origin VARCHAR(255) NOT NULL,
  region VARCHAR(255),
  farm VARCHAR(255),
  variety VARCHAR(255),
  processing VARCHAR(255),
  roast_level VARCHAR(50) NOT NULL,
  altitude INT,
  harvest_year INT,
  description TEXT,
  created_by BIGINT NOT NULL,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS bean_flavor_note (
  bean_id BIGINT NOT NULL,
  note VARCHAR(50) NOT NULL,
  FOREIGN KEY (bean_id) REFERENCES bean(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS bean_review (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  bean_id BIGINT NOT NULL,
  member_id BIGINT NOT NULL,
  rating INT NOT NULL,
  content TEXT,
  acidity INT,
  sweetness INT,
  body INT,
  aroma INT,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  UNIQUE KEY uq_bean_review (bean_id, member_id)
);

-- Step 2: Add beanId to recipe table
ALTER TABLE recipe ADD COLUMN IF NOT EXISTS bean_id BIGINT;

-- Bean Catalog Migration Notes
-- These SQL statements are for MANUAL execution in production
-- after the feat/bean-catalog branch is deployed.
--
-- Step 1: Deploy backend (adds bean_id column via Hibernate update mode)
-- Step 2: Deploy frontend (uses bean_id instead of free-text fields)
-- Step 3: After verifying frontend works, run these to clean up:

-- ALTER TABLE recipe DROP COLUMN coffee_bean;   -- only after FE confirmed working
-- ALTER TABLE recipe DROP COLUMN origin;         -- only after FE confirmed working
-- ALTER TABLE recipe DROP COLUMN roast_level;    -- only after FE confirmed working

-- ==========================================
-- Phase 2: Dripper Catalog
-- ==========================================

-- Forward DDL (run before deploy)
CREATE TABLE dripper (
    id               BIGSERIAL PRIMARY KEY,
    name             VARCHAR(255) NOT NULL,
    brand            VARCHAR(255) NOT NULL,
    type             VARCHAR(50)  NOT NULL,
    material         VARCHAR(50)  NOT NULL,
    holes_count      INT,
    extraction_speed VARCHAR(20),
    description      TEXT,
    created_by       BIGINT       NOT NULL,
    created_at       TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE dripper_review (
    id              BIGSERIAL PRIMARY KEY,
    dripper_id      BIGINT    NOT NULL,
    member_id       BIGINT    NOT NULL,
    rating          INT       NOT NULL,
    content         TEXT,
    extraction_rate INT,
    cleanability    INT,
    durability      INT,
    heat_retention  INT,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE (dripper_id, member_id)
);

ALTER TABLE recipe ADD COLUMN IF NOT EXISTS dripper_id BIGINT;
