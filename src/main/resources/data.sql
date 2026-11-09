-- ============================================================
-- Demo data for ORM Learning Platform (idempotent)
-- ============================================================

-- Users
INSERT INTO users (id, name, email, role, created_at) VALUES
                                                          (1, 'Ivan Petrov',    'teacher@example.com', 'TEACHER', CURRENT_TIMESTAMP),
                                                          (2, 'Alice Smirnova', 'alice@example.com',   'STUDENT', CURRENT_TIMESTAMP),
                                                          (3, 'Bob Ivanov',     'bob@example.com',     'STUDENT', CURRENT_TIMESTAMP),
                                                          (4, 'Admin User',     'admin@example.com',   'ADMIN',   CURRENT_TIMESTAMP)
    ON CONFLICT (email) DO NOTHING;

-- Profiles
INSERT INTO profiles (id, user_id, bio, avatar_url, phone, location) VALUES
                                                                         (1, 1, 'Senior Java developer, 10+ years of experience', NULL, '+7-900-000-0001', 'Moscow'),
                                                                         (2, 2, 'Junior Java developer, learning Hibernate',      NULL, '+7-900-000-0002', 'Saint Petersburg'),
                                                                         (3, 3, 'Student, interested in backend development',     NULL, '+7-900-000-0003', 'Kazan')
    ON CONFLICT (user_id) DO NOTHING;

-- Categories
INSERT INTO categories (id, name, description) VALUES
                                                   (1, 'Programming', 'Software development courses'),
                                                   (2, 'Databases',   'Database design and SQL'),
                                                   (3, 'Web',         'Web development')
    ON CONFLICT (name) DO NOTHING;

-- Courses
INSERT INTO courses (id, title, description, duration_hours, start_date, category_id, teacher_id) VALUES
                                                                                                      (1, 'Hibernate Basics',    'Introduction to ORM and Hibernate', 40, CURRENT_DATE, 1, 1),
                                                                                                      (2, 'PostgreSQL for Devs', 'Practical PostgreSQL for developers', 24, CURRENT_DATE, 2, 1)
    ON CONFLICT (id) DO NOTHING;

-- Modules
INSERT INTO modules (id, title, description, order_index, course_id) VALUES
                                                                         (1, 'Getting Started', 'Setup and first entity', 1, 1),
                                                                         (2, 'Relations',       'OneToMany, ManyToOne, ManyToMany', 2, 1)
    ON CONFLICT (id) DO NOTHING;

-- Lessons
INSERT INTO lessons (id, title, content, video_url, order_index, module_id) VALUES
                                                                                (1, 'What is ORM?',          'ORM maps objects to relational tables', NULL, 1, 1),
                                                                                (2, 'First Entity',          'Create your first JPA entity',         NULL, 2, 1),
                                                                                (3, 'OneToMany in practice', 'Bidirectional OneToMany example',      NULL, 1, 2)
    ON CONFLICT (id) DO NOTHING;

-- Assignments
INSERT INTO assignments (id, title, description, due_date, max_score, lesson_id) VALUES
                                                                                     (1, 'Create User entity', 'Implement @Entity for User', CURRENT_TIMESTAMP + INTERVAL '7 days',  100, 2),
                                                                                     (2, 'Map OneToMany',      'Map Course -> Modules',      CURRENT_TIMESTAMP + INTERVAL '14 days', 100, 3)
    ON CONFLICT (id) DO NOTHING;

-- Quizzes
INSERT INTO quizzes (id, title, time_limit_minutes, module_id) VALUES
    (1, 'Hibernate Basics Quiz', 15, 1)
    ON CONFLICT (module_id) DO NOTHING;

-- Questions
INSERT INTO questions (id, text, type, order_index, points, quiz_id) VALUES
                                                                         (1, 'What does ORM stand for?',       'SINGLE_CHOICE', 1, 1, 1),
                                                                         (2, 'Which annotation maps a table?', 'SINGLE_CHOICE', 2, 1, 1)
    ON CONFLICT (id) DO NOTHING;

-- AnswerOptions
INSERT INTO answer_options (id, text, is_correct, order_index, question_id) VALUES
                                                                                (1, 'Object-Relational Mapping', true,  1, 1),
                                                                                (2, 'Only Relational Mapping',   false, 2, 1),
                                                                                (3, 'Object-Resource Mapping',   false, 3, 1),
                                                                                (4, '@Entity',                   true,  1, 2),
                                                                                (5, '@Table',                    false, 2, 2),
                                                                                (6, '@Column',                   false, 3, 2)
    ON CONFLICT (id) DO NOTHING;

-- Enrollments
INSERT INTO enrollments (id, enroll_date, status, student_id, course_id) VALUES
                                                                             (1, CURRENT_TIMESTAMP, 'ACTIVE', 2, 1),
                                                                             (2, CURRENT_TIMESTAMP, 'ACTIVE', 3, 1)
    ON CONFLICT (student_id, course_id) DO NOTHING;

-- Tags
INSERT INTO tags (id, name) VALUES
                                (1, 'Java'), (2, 'Hibernate'), (3, 'Beginner')
    ON CONFLICT (name) DO NOTHING;

-- Course tags
INSERT INTO course_tag (course_id, tag_id) VALUES
                                               (1, 1), (1, 2), (1, 3)
    ON CONFLICT (course_id, tag_id) DO NOTHING;

-- Notifications
INSERT INTO notifications (id, title, message, is_read, created_at, user_id) VALUES
                                                                                 (1, 'Welcome!', 'Welcome to the ORM learning platform', false, CURRENT_TIMESTAMP, 2),
                                                                                 (2, 'New assignment', 'You have a new assignment',     false, CURRENT_TIMESTAMP, 2)
    ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- Sync IDENTITY sequences after explicit-id inserts.
-- Without this, the first JPA insert will fail with
-- "duplicate key value violates unique constraint".
-- ============================================================
SELECT setval(pg_get_serial_sequence('users', 'id'),          (SELECT COALESCE(MAX(id), 1) FROM users));
SELECT setval(pg_get_serial_sequence('profiles', 'id'),       (SELECT COALESCE(MAX(id), 1) FROM profiles));
SELECT setval(pg_get_serial_sequence('categories', 'id'),     (SELECT COALESCE(MAX(id), 1) FROM categories));
SELECT setval(pg_get_serial_sequence('courses', 'id'),        (SELECT COALESCE(MAX(id), 1) FROM courses));
SELECT setval(pg_get_serial_sequence('modules', 'id'),        (SELECT COALESCE(MAX(id), 1) FROM modules));
SELECT setval(pg_get_serial_sequence('lessons', 'id'),        (SELECT COALESCE(MAX(id), 1) FROM lessons));
SELECT setval(pg_get_serial_sequence('assignments', 'id'),    (SELECT COALESCE(MAX(id), 1) FROM assignments));
SELECT setval(pg_get_serial_sequence('quizzes', 'id'),        (SELECT COALESCE(MAX(id), 1) FROM quizzes));
SELECT setval(pg_get_serial_sequence('questions', 'id'),      (SELECT COALESCE(MAX(id), 1) FROM questions));
SELECT setval(pg_get_serial_sequence('answer_options', 'id'), (SELECT COALESCE(MAX(id), 1) FROM answer_options));
SELECT setval(pg_get_serial_sequence('enrollments', 'id'),    (SELECT COALESCE(MAX(id), 1) FROM enrollments));
SELECT setval(pg_get_serial_sequence('tags', 'id'),           (SELECT COALESCE(MAX(id), 1) FROM tags));
SELECT setval(pg_get_serial_sequence('notifications', 'id'),  (SELECT COALESCE(MAX(id), 1) FROM notifications));