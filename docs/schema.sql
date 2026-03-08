-- CoffeeZip MySQL Schema (no FK constraints, singular table names)

CREATE TABLE IF NOT EXISTS member (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    provider      VARCHAR(20)  NOT NULL COMMENT 'google | kakao',
    provider_id   VARCHAR(255) NOT NULL,
    email         VARCHAR(255),
    nickname      VARCHAR(100) NOT NULL,
    profile_image VARCHAR(500),
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_provider (provider, provider_id)
);

CREATE TABLE IF NOT EXISTS recipe (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id    BIGINT       NOT NULL,
    title        VARCHAR(200) NOT NULL,
    description  TEXT,
    coffee_bean  VARCHAR(200),
    origin       VARCHAR(100),
    roast_level  VARCHAR(50),
    grinder      VARCHAR(100),
    grind_size   VARCHAR(50),
    coffee_grams DOUBLE,
    water_grams  DOUBLE,
    water_temp   INT,
    target_yield INT,
    is_public    BOOLEAN      NOT NULL DEFAULT TRUE,
    like_count   INT          NOT NULL DEFAULT 0,
    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS recipe_step (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    recipe_id    BIGINT       NOT NULL,
    step_order   INT          NOT NULL,
    label        VARCHAR(200) NOT NULL,
    duration     INT          NOT NULL COMMENT '초',
    water_amount INT          COMMENT 'ml'
);

CREATE TABLE IF NOT EXISTS recipe_tag (
    recipe_id    BIGINT      NOT NULL,
    tag          VARCHAR(50) NOT NULL,
    PRIMARY KEY (recipe_id, tag)
);

CREATE TABLE IF NOT EXISTS recipe_like (
    recipe_id    BIGINT   NOT NULL,
    member_id    BIGINT   NOT NULL,
    created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (recipe_id, member_id)
);

CREATE TABLE IF NOT EXISTS recipe_comment (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    recipe_id    BIGINT   NOT NULL,
    member_id    BIGINT   NOT NULL,
    content      TEXT     NOT NULL,
    created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS daily_note (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id    BIGINT NOT NULL,
    note_date    DATE   NOT NULL,
    content      TEXT,
    rating       INT    COMMENT '1~5',
    created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uq_member_date (member_id, note_date)
);

-- 타이머 로그가 노트-레시피 연결을 담당 (하루 여러 레시피 대응)
CREATE TABLE IF NOT EXISTS timer_log (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id    BIGINT       NOT NULL,
    note_id      BIGINT,
    recipe_id    BIGINT       NOT NULL,
    recipe_name  VARCHAR(200) NOT NULL,
    started_at   DATETIME     NOT NULL,
    completed_at DATETIME
);
