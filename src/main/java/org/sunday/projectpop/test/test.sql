
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE suggest_from_leader;
TRUNCATE TABLE user_skill_tag;
TRUNCATE TABLE user_trait;
TRUNCATE TABLE trait_match;
TRUNCATE TABLE member;
TRUNCATE TABLE project;
TRUNCATE TABLE user_account;
TRUNCATE TABLE skill_tag;
TRUNCATE TABLE project_require_tag;
TRUNCATE TABLE message;
TRUNCATE TABLE suggest_from_leader;
TRUNCATE TABLE ongoing_project;
TRUNCATE TABLE specification;

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


# select * from suggest_from_leader


INSERT INTO message (sender_id, receiver_id, content, checking, sent_at) VALUES
                                                                             ('u01', 'u02', '안녕하세요. 혹시 프로젝트 관심 있으신가요?', false, NOW() - INTERVAL 3 DAY),
                                                                             ('u01', 'u03', '이번 주 중으로 회의 한번 가능하신가요?', true, NOW() - INTERVAL 2 DAY),
                                                                             ('u04', 'u01', '네. 프로젝트 제안 확인했습니다.', false, NOW() - INTERVAL 1 DAY),
                                                                             ('u05', 'u01', '초대 감사드려요. 자세한 내용 공유 부탁드려요.', true, NOW());
-- project 테이블 목 데이터 3개 생성
INSERT INTO project (project_id, user_id, type, status, generated_by_ai, field, title, description, location_type, duration_weeks, team_size, created_at)
VALUES
    ('1', 'u01', 'PROJECT', '모집중', false, '백엔드', 'AI 기반 협업 툴 개발', '자동화된 협업툴을 개발하여 생산성을 향상시킵니다.', 'REMOTE', 8, 5, NOW()),
    ('2', 'u05', 'PROJECT', '진행중', false, '프론트엔드', '사용자 인터페이스 개선 프로젝트', '기존 서비스의 사용자 경험을 향상시키기 위한 UI/UX 개선 작업을 진행합니다.', 'REMOTE', 6, 3, NOW() - INTERVAL 2 MONTH),
    ('3', 'u08', 'PROJECT', '모집중', false, '데브옵스', 'CI/CD 파이프라인 구축', '개발 및 배포 프로세스를 자동화하기 위한 CI/CD 파이프라인을 구축합니다.', 'LOCAL', 10, 4, NOW() - INTERVAL 1 MONTH);



-- OnGoingProject 목 데이터 (id: 1, 2, 3)
INSERT INTO ongoing_project (id, project_id, team_leader_id, status, start_date, end_date)
VALUES
    ('1', '1', 'u01', 'ONGOING', '2025-05-01', '2025-07-31'),
    ('2', '2', 'u05', 'COMPLETED', '2025-04-01', '2025-04-30'),
    ('3', '3', 'u08', 'ONGOING', '2025-05-15', '2025-08-15');

-- Specification 목 데이터 (10개, completed 7개, 나머지 3개 랜덤)
INSERT INTO specification (id, ongoing_project_id, requirement, assignee, status, due_date, created_at, updated_at)
VALUES
    ('s01', '1', '로그인 기능 구현', 'u02', 'completed', '2025-05-08', NOW() - INTERVAL 10 DAY, NOW() - INTERVAL 10 DAY),
    ('s02', '1', '사용자 프로필 페이지 개발', 'u03', 'completed', '2025-05-15', NOW() - INTERVAL 9 DAY, NOW() - INTERVAL 9 DAY),
    ('s03', '1', '데이터베이스 설계', 'u01', 'onGoing', '2025-05-22', NOW() - INTERVAL 8 DAY, NOW()),
    ('s04', '1', 'API 연동 테스트', 'u04', 'waiting', '2025-05-29', NOW() - INTERVAL 7 DAY, NOW() - INTERVAL 7 DAY),
    ('s05', '2', '결제 모듈 통합', 'u06', 'completed', '2025-04-20', NOW() - INTERVAL 6 DAY, NOW() - INTERVAL 6 DAY),
    ('s06', '2', '상품 목록 페이지 디자인', 'u07', 'completed', '2025-04-25', NOW() - INTERVAL 5 DAY, NOW() - INTERVAL 5 DAY),
    ('s07', '2', '주문 처리 로직 구현', 'u05', 'completed', '2025-04-30', NOW() - INTERVAL 4 DAY, NOW() - INTERVAL 4 DAY),
    ('s08', '3', 'UI 컴포넌트 개발', 'u09', 'completed', '2025-06-05', NOW() - INTERVAL 3 DAY, NOW() - INTERVAL 3 DAY),
    ('s09', '3', '서버 배포 설정', 'u10', 'completed', '2025-06-10', NOW() - INTERVAL 2 DAY, NOW() - INTERVAL 2 DAY),
    ('s10', '3', '사용자 인증 방식 변경', 'u08', CASE WHEN RAND() < 0.5 THEN 'ONGOING' ELSE 'WAITING' END, '2025-06-15', NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 1 DAY);


# 리더 u01이 제안 보낸 것 (p001)
INSERT INTO suggest_from_leader (project_id, sender_id, receiver_id, message, checking, created_at) VALUES
                                                                                                        ('p001', 'u01', 'u03', '저희 팀에 함께 하시면 좋겠습니다!', false, NOW()),
                                                                                                        ('p001', 'u01', 'u04', '프론트 경험 있으시다 들었어요. 관심 있으신가요?', false, NOW() - INTERVAL 1 DAY),
                                                                                                        ('p001', 'u05', 'u01', '백엔드 경험자 찾고 있습니다. 관심 있으신가요?', false, NOW());


# 반대로 다른 리더가 u01에게 보낸 것처럼 예시
INSERT INTO suggest_from_leader (project_id, sender_id, receiver_id, message,checking, created_at) VALUES
    ('p001', 'u05', 'u01', '백엔드 경험자 찾고 있습니다. 관심 있으신가요?',false, NOW());





INSERT INTO message (sender_id, receiver_id, content, checking, sent_at) VALUES
                                                                             ('u01', 'u02', '안녕하세요! 제안드릴게 있어요.', false, NOW()),
                                                                             ('u01', 'u02', '혹시 관심 있으시면 알려주세요.', true, NOW() - INTERVAL 1 DAY),
                                                                             ('u02', 'u01', '확인했습니다. 검토해볼게요.', true, NOW());



select * from member
select * from message;
select * from suggest_from_leader
