
SET FOREIGN_KEY_CHECKS = 0;

# DROP TABLE IF EXISTS user_skill_tag;
# DROP TABLE IF EXISTS user_trait;
# DROP TABLE IF EXISTS trait_match;
# DROP TABLE IF EXISTS member;
# DROP TABLE IF EXISTS project;
# DROP TABLE IF EXISTS user_account;
# DROP TABLE IF EXISTS skill_tag;
# DROP TABLE IF EXISTS project_require_tag;
# DROP TABLE IF EXISTS message;
# DROP TABLE IF EXISTS suggest_from_leader;


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

INSERT INTO project_require_tag (project_id, tag_id) VALUES
                                                         ('p001', 1), -- Spring
                                                         ('p001', 2); -- React


# 메시지 테이블 초기화
TRUNCATE TABLE message;

# 메시지 데이터 (u01이 보낸 쪽지들, u02가 받은 쪽지들)
INSERT INTO message (sender_id, receiver_id, content, checking, sent_at) VALUES
                                                                               ('u01', 'u02', '안녕하세요! 제안드릴게 있어요.', false, NOW()),
                                                                               ('u01', 'u02', '혹시 관심 있으시면 알려주세요.', true, NOW() - INTERVAL 1 DAY),
                                                                               ('u02', 'u01', '확인했습니다. 검토해볼게요.', true, NOW());

# 제안 테이블 초기화
TRUNCATE TABLE suggest_from_leader;

# 리더 u01이 제안 보낸 것 (p001)
INSERT INTO suggest_from_leader (project_id, sender_id, receiver_id, message, checking, created_at) VALUES
                                                                                                        ('p001', 'u01', 'u03', '저희 팀에 함께 하시면 좋겠습니다!', false, NOW()),
                                                                                                        ('p001', 'u01', 'u04', '프론트 경험 있으시다 들었어요. 관심 있으신가요?', false, NOW() - INTERVAL 1 DAY),
                                                                                                        ('p001', 'u05', 'u01', '백엔드 경험자 찾고 있습니다. 관심 있으신가요?', false, NOW());


# 반대로 다른 리더가 u01에게 보낸 것처럼 예시
INSERT INTO suggest_from_leader (project_id, sender_id, receiver_id, message, created_at) VALUES
    ('p001', 'u05', 'u01', '백엔드 경험자 찾고 있습니다. 관심 있으신가요?', NOW());


# select * from suggest_from_leader


INSERT INTO message (sender_id, receiver_id, content, checking, sent_at) VALUES
                                                                             ('u01', 'u02', '안녕하세요! 제안드릴게 있어요.', false, NOW()),
                                                                             ('u01', 'u02', '혹시 관심 있으시면 알려주세요.', true, NOW() - INTERVAL 1 DAY),
                                                                             ('u02', 'u01', '확인했습니다. 검토해볼게요.', true, NOW());



select * from message
select * from suggest_from_leader
