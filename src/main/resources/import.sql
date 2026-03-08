-- Dev/test seed data for CoffeeZip
INSERT INTO member (provider, provider_id, email, nickname, profile_image, created_at)
VALUES ('google', 'test-google-id-1', 'test@example.com', '커피러버', NULL, NOW());

INSERT INTO recipe (member_id, title, description, coffee_bean, origin, roast_level, grinder, grind_size,
                    coffee_grams, water_grams, water_temp, target_yield, is_public, like_count, created_at, updated_at)
VALUES (1, '클래식 핸드드립', '깔끔하고 밸런스 좋은 핸드드립 레시피', '에티오피아 예가체프', '에티오피아', 'Light',
        '코만단테', '30클릭', 15.0, 250.0, 93, 230, TRUE, 0, NOW(), NOW());

INSERT INTO recipe_step (recipe_id, step_order, label, duration, water_amount)
VALUES (1, 1, '뜸들이기', 45, 30),
       (1, 2, '1차 추출', 30, 80),
       (1, 3, '2차 추출', 30, 80),
       (1, 4, '3차 추출', 30, 60);

INSERT INTO recipe_tag (recipe_id, tag) VALUES (1, '핸드드립'), (1, '에티오피아');
