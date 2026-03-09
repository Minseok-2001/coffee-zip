-- Dev/test seed data for CoffeeZip

INSERT INTO member (provider, provider_id, email, nickname, profile_image, created_at)
VALUES ('google', 'test-google-id-1', 'test@example.com', '커피러버', NULL, NOW());

INSERT INTO recipe (member_id, title, description, coffee_bean, origin, roast_level, grinder, grind_size,
                    coffee_grams, water_grams, water_temp, target_yield, published_at, like_count, created_at, updated_at)
VALUES (1, '클래식 핸드드립', '깔끔하고 밸런스 좋은 핸드드립 레시피', '에티오피아 예가체프', '에티오피아', 'Light',
        '코만단테', '30클릭', 15.0, 250.0, 93, 230, NOW(), 0, NOW(), NOW()),
       (1, '에스프레소 룽고', '진하고 묵직한 에스프레소 베이스 레시피', '콜롬비아 수프리모', '콜롬비아', 'Medium',
        '바라짜 엔코어', '14클릭', 18.0, 36.0, 93, 40, NOW(), 3, NOW(), NOW()),
       (1, '콜드브루 더치', '12시간 저온 추출 더치커피', '과테말라 안티구아', '과테말라', 'Medium-Dark',
        NULL, NULL, 60.0, 600.0, NULL, 550, NOW(), 7, NOW(), NOW()),
       (1, '비공개 레시피', '아직 다듬는 중인 레시피', '케냐 AA', '케냐', 'Light',
        NULL, NULL, 14.0, 210.0, 91, 200, NULL, 0, NOW(), NOW());

INSERT INTO recipe_step (recipe_id, step_order, label, duration, water_amount)
VALUES (1, 1, '뜸들이기', 45, 30),
       (1, 2, '1차 추출', 30, 80),
       (1, 3, '2차 추출', 30, 80),
       (1, 4, '3차 추출', 30, 60),
       (2, 1, '원두 분쇄', 10, NULL),
       (2, 2, '에스프레소 추출', 25, 36),
       (3, 1, '원두 세팅', 30, NULL),
       (3, 2, '1차 물 투입', 120, 200),
       (3, 3, '2차 물 투입', 120, 200),
       (3, 4, '3차 물 투입', 120, 200);

INSERT INTO recipe_tag (recipe_id, tag)
VALUES (1, '핸드드립'), (1, '에티오피아'), (1, '입문'),
       (2, '에스프레소'), (2, '콜롬비아'),
       (3, '콜드브루'), (3, '더치'), (3, '과테말라');
