
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE user_skill_tag;
TRUNCATE TABLE user_trait;
TRUNCATE TABLE trait_match;
TRUNCATE TABLE member;
TRUNCATE TABLE project;
TRUNCATE TABLE user_account;
TRUNCATE TABLE skill_tag;

SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO user_account (user_id, email, password, provider, admin, banned)
VALUES
    ('u01', 'leader@example.com', 'encoded_pw1', 'LOCAL', false, false),
    ('u02', 'user2@example.com', 'encoded_pw2', 'LOCAL', false, false),
    ('u03', 'user3@example.com', 'encoded_pw3', 'LOCAL', false, false),
    ('u04', 'user4@example.com', 'encoded_pw4', 'LOCAL', false, false),
    ('u05', 'user5@example.com', 'encoded_pw5', 'LOCAL', false, false),
    ('u06', 'user6@example.com', 'encoded_pw6', 'LOCAL', false, false),
    ('u07', 'user7@example.com', 'encoded_pw7', 'LOCAL', false, false),
    ('u08', 'user8@example.com', 'encoded_pw8', 'LOCAL', false, false),
    ('u09', 'user9@example.com', 'encoded_pw9', 'LOCAL', false, false),
    ('u10', 'user10@example.com', 'encoded_pw10', 'LOCAL', false, false);

INSERT INTO project (project_id, user_id, type, status, generated_by_ai, field, title, description, location_type, duration_weeks, team_size, created_at)
VALUES ('p001', 'u01', 'PROJECT', '모집중', false, '백엔드', 'AI 기반 협업 툴', '자동화된 협업툴을 개발합니다.', 'REMOTE', 8, 5, NOW());


INSERT INTO member (project_id, user_id) VALUES ('p001', 'u02');


INSERT INTO skill_tag (tag_id, name)
VALUES (1, 'Spring'), (2, 'React'), (3, 'Docker');


INSERT INTO user_skill_tag (user_id, tag_id)
VALUES
    ('u01', 1), ('u01', 2), -- leader
    ('u02', 1), ('u02', 3),
    ('u03', 2),
    ('u04', 1), ('u04', 2), ('u04', 3),
    ('u05', 1),
    ('u06', 3),
    ('u07', 1),
    ('u08', 2),
    ('u09', 2),
    ('u10', 3);

INSERT INTO user_trait (user_id, openness, conscientiousness, extraversion, agreeableness, neuroticism)
VALUES
    ('u01', 5, 4, 3, 4, 2),
    ('u02', 3, 5, 4, 3, 3),
    ('u03', 4, 4, 4, 4, 4),
    ('u04', 5, 5, 5, 5, 5),
    ('u05', 2, 3, 3, 3, 2),
    ('u06', 1, 2, 3, 2, 1),
    ('u07', 3, 3, 3, 3, 3),
    ('u08', 5, 5, 4, 5, 2),
    ('u09', 2, 2, 4, 2, 3),
    ('u10', 4, 4, 4, 4, 4);


INSERT INTO trait_match (
    leader_ocean_key, openness, conscientiousness, extraversion, agreeableness, neuroticism, updated, base
) VALUES (
             '54342', 4.3, 3.9, 3.5, 3.7, 2.5, 1, 5
         );
