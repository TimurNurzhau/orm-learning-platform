-- ============================================================
-- Demo data for ORM Learning Platform
-- ============================================================

-- Users
INSERT INTO users (name, email, role, created_at) VALUES
                                                      ('Ivan Petrov',    'teacher@example.com', 'TEACHER', CURRENT_TIMESTAMP),
                                                      ('Alice Smirnova', 'alice@example.com',   'STUDENT', CURRENT_TIMESTAMP),
                                                      ('Bob Ivanov',     'bob@example.com',     'STUDENT', CURRENT_TIMESTAMP),
                                                      ('Admin User',     'admin@example.com',   'ADMIN',   CURRENT_TIMESTAMP);

-- Profiles
INSERT INTO profiles (user_id, bio, avatar_url, phone, location) VALUES
                                                                     (1, 'Senior Java developer, 10+ years of experience', NULL, '+7-900-000-0001', 'Moscow'),
                                                                     (2, 'Junior Java developer, learning Hibernate',      NULL, '+7-900-000-0002', 'Saint Petersburg'),
                                                                     (3, 'Student, interested in backend development',     NULL, '+7-900-000-0003', 'Kazan');

-- Categories
INSERT INTO categories (name, description) VALUES
                                               ('Programming',  'Software development courses'),
                                               ('Databases',    'Database design and SQL'),
                                               ('Web',          'Web development');

-- Courses
INSERT INTO courses (title, description, duration_hours, start_date, category_id, teacher_id) VALUES
                                                                                                  ('Hibernate Basics',    'Introduction to ORM and Hibernate', 40, CURRENT_DATE, 1, 1),
                                                                                                  ('PostgreSQL for Devs', 'Practical PostgreSQL for developers', 24, CURRENT_DATE, 2, 1);

-- Modules
INSERT INTO modules (title, description, order_index, course_id) VALUES
                                                                     ('Getting Started', 'Setup and first entity', 1, 1),
                                                                     ('Relations',       'OneToMany, ManyToOne, ManyToMany', 2, 1);

-- Lessons
INSERT INTO lessons (title, content, video_url, order_index, module_id) VALUES
                                                                            ('What is ORM?',           'ORM maps objects to relational tables', NULL, 1, 1),
                                                                            ('First Entity',           'Create your first JPA entity',         NULL, 2, 1),
                                                                            ('OneToMany in practice',  'Bidirectional OneToMany example',      NULL, 1, 2);

-- Assignments
INSERT INTO assignments (title, description, due_date, max_score, lesson_id) VALUES
                                                                                 ('Create User entity', 'Implement @Entity for User', CURRENT_TIMESTAMP + INTERVAL '7 days',  100, 2),
                                                                                 ('Map OneToMany',      'Map Course -> Modules',      CURRENT_TIMESTAMP + INTERVAL '14 days', 100, 3);

-- Quizzes
INSERT INTO quizzes (title, time_limit_minutes, module_id) VALUES
    ('Hibernate Basics Quiz', 15, 1);

-- Questions
INSERT INTO questions (text, type, order_index, points, quiz_id) VALUES
                                                                     ('What does ORM stand for?',      'SINGLE_CHOICE', 1, 1, 1),
                                                                     ('Which annotation maps a table?','SINGLE_CHOICE', 2, 1, 1);

-- AnswerOptions
INSERT INTO answer_options (text, is_correct, order_index, question_id) VALUES
                                                                            ('Object-Relational Mapping', true,  1, 1),
                                                                            ('Only Relational Mapping',   false, 2, 1),
                                                                            ('Object-Resource Mapping',   false, 3, 1),
                                                                            ('@Entity',                   true,  1, 2),
                                                                            ('@Table',                    false, 2, 2),
                                                                            ('@Column',                   false, 3, 2);

-- Enrollments
INSERT INTO enrollments (enroll_date, status, student_id, course_id) VALUES
                                                                         (CURRENT_TIMESTAMP, 'ACTIVE', 2, 1),
                                                                         (CURRENT_TIMESTAMP, 'ACTIVE', 3, 1);

-- Tags
INSERT INTO tags (name) VALUES
                            ('Java'), ('Hibernate'), ('Beginner');

-- Course tags
INSERT INTO course_tag (course_id, tag_id) VALUES
                                               (1, 1), (1, 2), (1, 3);

-- Notifications
INSERT INTO notifications (title, message, is_read, created_at, user_id) VALUES
                                                                             ('Welcome!', 'Welcome to the ORM learning platform', false, CURRENT_TIMESTAMP, 2),
                                                                             ('New assignment', 'You have a new assignment',     false, CURRENT_TIMESTAMP, 2);