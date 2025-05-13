-- -- 💡 TRUNCATE 대신 DELETE
-- DELETE FROM project_require_tag;
-- DELETE FROM project_selective_tag;
-- DELETE FROM project;
-- DELETE FROM user_account;
-- DELETE FROM project_field;
-- DELETE FROM skill_tag;

-- 👤 유저
INSERT INTO user_account (user_id, email, password, provider, admin, banned)
VALUES ('u01', 'leader@example.com', 'encoded_pw1', 'LOCAL', false, false);

-- 🏷️ 기술 태그
-- INSERT INTO skill_tag (tag_id, name)
-- VALUES
--     (1, 'Spring'),
--     (2, 'React'),
--     (3, 'Docker'),
--     (4, 'MySQL'),
--     (5, 'Figma');
INSERT INTO skill_tag (tag_id, name) VALUES
                                         (1, 'Spring'),
                                         (2, 'React'),
                                         (3, 'Docker'),
                                         (4, 'Spring Boot'),
                                         (5, 'Vue.js'),
                                         (6, 'Node.js'),
                                         (7, 'Express'),
                                         (8, 'Java'),
                                         (9, 'Python'),
                                         (10, 'C++'),
                                         (11, 'JavaScript'),
                                         (12, 'TypeScript'),
                                         (13, 'MySQL'),
                                         (14, 'PostgreSQL'),
                                         (15, 'MongoDB'),
                                         (16, 'Redis'),
                                         (17, 'Kubernetes'),
                                         (18, 'AWS'),
                                         (19, 'GCP'),
                                         (20, 'Nginx'),
                                         (21, 'Linux'),
                                         (22, 'Git'),
                                         (23, 'GitHub Actions'),
                                         (24, 'CI/CD'),
                                         (25, 'Jenkins'),
                                         (26, 'REST API'),
                                         (27, 'GraphQL'),
                                         (28, 'JUnit'),
                                         (29, 'pytest'),
                                         (30, 'TensorFlow'),
                                         (31, 'PyTorch'),
                                         (32, 'OpenAI'),
                                         (33, 'LangChain'),
                                         (34, 'FastAPI'),
                                         (35, 'Flask'),
                                         (36, 'Thymeleaf'),
                                         (37, 'JSP'),
                                         (38, 'Kafka'),
                                         (39, 'RabbitMQ'),
                                         (40, 'OAuth2'),
                                         (41, 'JWT'),
                                         (42, 'Swagger'),
                                         (43, 'Postman');

-- 📂 분야 (ProjectField)
INSERT INTO project_field (id, name, description) VALUES
                                                      (1, 'backend', '백엔드 개발'),
                                                      (2, 'frontend', '프론트엔드 개발'),
                                                      (3, 'ai', '인공지능');

INSERT INTO project_field (id, name, description) VALUES
                                                      (4, 'web', '웹 개발'),
                                                      (5, 'app', '모바일 앱 개발'),
                                                      (6, 'game', '게임 개발'),
                                                      (7, 'data', '데이터 분석/엔지니어링'),
                                                      (8, 'security', '정보 보안'),
                                                      (9, 'devops', 'DevOps 및 인프라'),
                                                      (10, 'design', 'UX/UI 디자인'),
                                                      (11, 'etc', '기타 분야');
-- 📢 프로젝트 공고
INSERT INTO project (
    project_id, user_id, type, status, generated_by_ai, field_id,
    title, description, location_type, duration_weeks, team_size,
    experience_level, created_at
) VALUES (
             'p001', 'u01', 'PROJECT', '모집중', false, 2,
             'AI 기반 협업 툴', '자동화된 협업툴을 개발합니다.', '비대면', 8, 5,
             '실무 1년 이상', NOW()
         );

INSERT INTO project (
    project_id, user_id, type, status, generated_by_ai, field_id,
    title, description, location_type, duration_weeks, team_size,
    experience_level, created_at
) VALUES (
             'p005', 'u01', 'PROJECT', '모집중', false, 4,
             '주니어 대상 협업 툴 기획', '비전공자도 쉽게 협업할 수 있는 도구를 함께 기획합니다.', '비대면', 4, 3,
             '비전공/비개발 경험자', NOW()
         );
INSERT INTO project_require_tag (project_id, tag_id) VALUES ('p005', 22); -- Git
INSERT INTO project_selective_tag (project_id, tag_id) VALUES ('p005', 5); -- Vue.js

-- 📢 프로젝트 6: 개인 포트폴리오 웹 개발 (초급 수준)
INSERT INTO project (
    project_id, user_id, type, status, generated_by_ai, field_id,
    title, description, location_type, duration_weeks, team_size,
    experience_level, created_at
) VALUES (
             'p006', 'u01', 'PROJECT', '모집중', false, 2,
             '개인 포트폴리오 웹 개발', 'HTML/CSS/JavaScript 기반 포트폴리오 웹사이트를 함께 개발합니다.', '대면', 3, 2,
             '초급 (개인 프로젝트 수준)', NOW()
         );
INSERT INTO project_require_tag (project_id, tag_id) VALUES ('p006', 11); -- JavaScript
INSERT INTO project_selective_tag (project_id, tag_id) VALUES ('p006', 36); -- Thymeleaf
INSERT INTO project (
    project_id, user_id, type, status, generated_by_ai, field_id,
    title, description, location_type, duration_weeks, team_size,
    experience_level, created_at
) VALUES (
             'p007', 'u01', 'PROJECT', '모집중', false, 1,
             '팀 협업 게시판 개발', '팀 커뮤니케이션을 위한 실시간 게시판 서비스를 개발합니다.', '비대면', 6, 5,
             '중급 (팀 프로젝트 경험자)', NOW()
         );
INSERT INTO project_require_tag (project_id, tag_id) VALUES ('p007', 1), ('p007', 3); -- Spring, Docker
INSERT INTO project_selective_tag (project_id, tag_id) VALUES ('p007', 38);
-- 🔖 필수 태그
INSERT INTO project_require_tag (project_id, tag_id) VALUES
                                                         ('p001', 1),
                                                         ('p001', 2);

-- 🔖 선택 태그
INSERT INTO project_selective_tag (project_id, tag_id) VALUES
                                                           ('p001', 3),
                                                           ('p001', 4);

INSERT INTO user_profile (user_id, nickname, bio, profile_image_url, phone)
VALUES (
           'u01',
           'sewoni',
           '함께 성장하며 협업하는 백엔드 개발자입니다.',
           'https://example.com/profile.jpg',
           '010-1234-5678'
       );

INSERT INTO user_skill_tag (user_id, tag_id) VALUES
                                                 ('u01', 1), -- Spring
                                                 ('u01', 3); -- Docker


ALTER TABLE project
    MODIFY experience_level ENUM(
    '비전공/비개발 경험자',
    '초급 (개인 프로젝트 수준)',
    '중급 (팀 프로젝트 경험자)',
    '준실무 (부트캠프/인턴)',
    '실무 1년 이상',
    '실무 3년 이상'
    );