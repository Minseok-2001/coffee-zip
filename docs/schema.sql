-- CoffeeZip MySQL Schema

CREATE TABLE IF NOT EXISTS users (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    provider     VARCHAR(20)  NOT NULL COMMENT 'google | kakao',
    provider_id  VARCHAR(255) NOT NULL,
    email        VARCHAR(255),
    nickname     VARCHAR(100) NOT NULL,
    profile_image VARCHAR(500),
    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_provider (provider, provider_id)
);

CREATE TABLE IF NOT EXISTS recipes (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id      BIGINT       NOT NULL,
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
    updated_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS recipe_steps (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    recipe_id    BIGINT       NOT NULL,
    step_order   INT          NOT NULL,
    label        VARCHAR(200) NOT NULL,
    duration     INT          NOT NULL COMMENT '초',
    water_amount INT          COMMENT 'ml',
    FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS recipe_tags (
    recipe_id    BIGINT      NOT NULL,
    tag          VARCHAR(50) NOT NULL,
    PRIMARY KEY (recipe_id, tag),
    FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS recipe_likes (
    recipe_id    BIGINT   NOT NULL,
    user_id      BIGINT   NOT NULL,
    created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (recipe_id, user_id),
    FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id)   REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS recipe_comments (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    recipe_id    BIGINT NOT NULL,
    user_id      BIGINT NOT NULL,
    content      TEXT   NOT NULL,
    created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id)   REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS daily_notes (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id      BIGINT NOT NULL,
    note_date    DATE   NOT NULL,
    content      TEXT,
    rating       INT    COMMENT '1~5',
    recipe_id    BIGINT,
    created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uq_user_date (user_id, note_date),
    FOREIGN KEY (user_id)   REFERENCES users(id),
    FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS timer_logs (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id      BIGINT       NOT NULL,
    note_id      BIGINT,
    recipe_id    BIGINT       NOT NULL,
    recipe_name  VARCHAR(200) NOT NULL,
    started_at   DATETIME     NOT NULL,
    completed_at DATETIME,
    FOREIGN KEY (user_id)  REFERENCES users(id),
    FOREIGN KEY (note_id)  REFERENCES daily_notes(id) ON DELETE SET NULL,
    FOREIGN KEY (recipe_id) REFERENCES recipes(id)
);
