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
INSERT INTO skill_tag (tag_id, name)
VALUES
    (1, 'Spring'),
    (2, 'React'),
    (3, 'Docker'),
    (4, 'MySQL'),
    (5, 'Figma');

-- 📂 분야 (ProjectField)
INSERT INTO project_field (id, name, description) VALUES
                                                      (1, 'backend', '백엔드 개발'),
                                                      (2, 'frontend', '프론트엔드 개발'),
                                                      (3, 'ai', '인공지능');

-- 📢 프로젝트 공고
INSERT INTO project (
    project_id, user_id, type, status, generated_by_ai, field_id,
    title, description, location_type, duration_weeks, team_size,
    experience_level, created_at
) VALUES (
             'p001', 'u01', 'PROJECT', '모집중', false, 2,
             'AI 기반 협업 툴', '자동화된 협업툴을 개발합니다.', '비대면', 8, 5,
             'BEGINNER', NOW()
         );

-- 🔖 필수 태그
INSERT INTO project_require_tag (project_id, tag_id) VALUES
                                                         ('p001', 1),
                                                         ('p001', 2);

-- 🔖 선택 태그
INSERT INTO project_selective_tag (project_id, tag_id) VALUES
                                                           ('p001', 3),
                                                           ('p001', 4);
