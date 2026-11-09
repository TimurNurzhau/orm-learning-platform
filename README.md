ORM Learning Platform
https://github.com/TimurNurzhau/orm-learning-platform/actions/workflows/ci.yml/badge.svg

Учебная платформа для онлайн-курса по ORM и Hibernate. Демонстрирует практическое применение JPA/Hibernate, Spring Boot, PostgreSQL и интеграционного тестирования с Testcontainers.

Стек технологий
Java 17

Spring Boot 3.5.7 (Web, Data JPA, Validation)

Hibernate ORM 6.6 (JPA)

PostgreSQL 16

Maven 3.9

Lombok

SpringDoc OpenAPI 2.8 (Swagger UI)

Testcontainers 1.21.4 (PostgreSQL)

Docker / Docker Compose

GitHub Actions (CI)

Модель данных — 19 сущностей
User: 1-1 Profile, 1-M Course (teacher), 1-M Enrollment, Submission, QuizSubmission, CourseReview, Notification, LessonProgress

Profile: 1-1 User

Category: 1-M Course

Course: M-1 Category, User (teacher); 1-M Module, Enrollment, CourseReview; M-M Tag

Tag: M-M Course

Enrollment: M-1 User, Course (unique: user+course)

Module: M-1 Course; 1-M Lesson; 1-1 Quiz

Lesson: M-1 Module; 1-M Assignment, Attachment, LessonProgress

Assignment: M-1 Lesson; 1-M Submission

Submission: M-1 User, Assignment (unique: user+assignment)

Attachment: M-1 Lesson

LessonProgress: M-1 User, Lesson (unique: user+lesson)

Quiz: M-1 Module (unique); 1-M Question, QuizSubmission

Question: M-1 Quiz; 1-M AnswerOption

AnswerOption: M-1 Question

QuizSubmission: M-1 User, Quiz

CourseReview: M-1 User, Course (unique: user+course)

Notification: M-1 User

Все коллекции — FetchType.LAZY, open-in-view: false (демонстрация LazyInitializationException и способов её обхода через @EntityGraph / JOIN FETCH).

Запуск проекта
Требования:

JDK 17+

Maven 3.9+

Docker Desktop (для PostgreSQL и Testcontainers)

Способ 1: Docker Compose (рекомендуется)

docker compose up -d
mvn spring-boot:run

При старте: Hibernate создаст схему (ddl-auto: update), data.sql наполнит БД демо-данными (4 пользователя, 2 курса, 3 урока, 2 задания, 1 тест с 2 вопросами), SpringDoc откроет Swagger UI на http://localhost:8080/swagger-ui.html.

Остановить: Ctrl+C, затем docker compose down.

Способ 2: Docker (всё в контейнерах)

docker build -t orm-learning-platform:local .
docker run --rm -p 8080:8080 -e SPRING_DATASOURCE_URL="jdbc:postgresql://host.docker.internal:5432/learning_platform" -e SPRING_DATASOURCE_USERNAME=postgres -e SPRING_DATASOURCE_PASSWORD=postgres -e SPRING_JPA_HIBERNATE_DDL_AUTO=update -e SPRING_DOCKER_COMPOSE_ENABLED=false orm-learning-platform:local

PostgreSQL должен быть уже запущен (docker compose up -d).

Swagger UI
http://localhost:8080/swagger-ui.html

Все эндпойнты сгруппированы по тегам: Users, Courses, Categories, Modules, Lessons, Assignments, Submissions, Quizzes, Reviews, Progress, Notifications, Tags.

REST API — примеры
Создать пользователя:

curl -X POST http://localhost:8080/api/users -H "Content-Type: application/json" -d '{"name":"John Doe","email":"john@example.com","role":"STUDENT"}'

Записаться на курс:

curl -X POST http://localhost:8080/api/courses/1/enroll/2

Отправить решение задания:

curl -X POST http://localhost:8080/api/submissions/assignments/1 -H "Content-Type: application/json" -d '{"studentId":2,"content":"My solution"}'

Пройти тест:

curl -X POST http://localhost:8080/api/quizzes/1/take -H "Content-Type: application/json" -d '{"studentId":2,"selectedAnswers":{"1":[1],"2":[4]}}'

Оставить отзыв о курсе:

curl -X POST "http://localhost:8080/api/courses/1/reviews?studentId=2" -H "Content-Type: application/json" -d '{"rating":5,"comment":"Great course!"}'

Средний рейтинг курса:

curl http://localhost:8080/api/courses/1/reviews/average

Полный список эндпойнтов
POST /api/users — Создать пользователя
GET /api/users — Список пользователей (фильтр ?role=)
GET /api/users/{id} — Пользователь по id
PUT /api/users/{id}/profile — Создать/обновить профиль
DELETE /api/users/{id} — Удалить пользователя
POST /api/courses — Создать курс
GET /api/courses — Список курсов (фильтр ?categoryId=, ?teacherId=)
GET /api/courses/{id} — Курс по id
POST /api/courses/{courseId}/enroll/{studentId} — Записать студента на курс
GET /api/courses/{courseId}/students — Студенты курса
DELETE /api/courses/{id} — Удалить курс
POST /api/categories — Создать категорию
GET /api/categories — Список категорий
GET /api/categories/{id} — Категория по id
DELETE /api/categories/{id} — Удалить категорию
POST /api/modules — Создать модуль
GET /api/modules?courseId= — Модули курса
GET /api/modules/{id} — Модуль по id
DELETE /api/modules/{id} — Удалить модуль
POST /api/lessons — Создать урок
GET /api/lessons?moduleId= — Уроки модуля
GET /api/lessons/{id} — Урок по id
DELETE /api/lessons/{id} — Удалить урок
POST /api/assignments — Создать задание
GET /api/assignments?lessonId= — Задания урока
GET /api/assignments/{id} — Задание по id
DELETE /api/assignments/{id} — Удалить задание
POST /api/submissions/assignments/{assignmentId} — Отправить решение
PATCH /api/submissions/{id}/grade — Оценить решение
GET /api/submissions/assignments/{assignmentId} — Решения по заданию
GET /api/submissions/students/{studentId} — Решения студента
POST /api/quizzes — Создать тест
GET /api/quizzes/{id} — Тест по id
POST /api/quizzes/{quizId}/take — Пройти тест
GET /api/quizzes/students/{studentId}/results — Результаты студента
GET /api/quizzes/{quizId}/results — Результаты теста
POST /api/courses/{courseId}/reviews?studentId= — Оставить отзыв
GET /api/courses/{courseId}/reviews — Отзывы курса
GET /api/courses/{courseId}/reviews/average — Средний рейтинг
POST /api/progress/users/{userId}/lessons/{lessonId}/complete — Отметить урок пройденным
GET /api/progress/users/{userId} — Прогресс пользователя
GET /api/progress/users/{userId}/completed-count — Кол-во пройденных уроков
POST /api/notifications — Отправить уведомление
GET /api/notifications/users/{userId} — Уведомления пользователя
GET /api/notifications/users/{userId}/unread — Непрочитанные уведомления
PATCH /api/notifications/{id}/read — Отметить прочитанным
POST /api/tags — Создать тег
GET /api/tags — Список тегов
POST /api/tags/courses/{courseId}/tags/{tagId} — Прикрепить тег к курсу
DELETE /api/tags/courses/{courseId}/tags/{tagId} — Открепить тег
DELETE /api/tags/{id} — Удалить тег

Архитектура
src/main/java/com/example/ormlearning/

entity/ — 19 JPA-сущностей (User, Course, Module, Lesson, Quiz, ...)

repository/ — 16 Spring Data JPA репозиториев

service/ — 13 сервисов (бизнес-логика, @Transactional)

dto/ — ~27 DTO (records для request/response)

web/ — 13 REST-контроллеров + GlobalExceptionHandler

exception/ — ResourceNotFoundException, DuplicateResourceException, BusinessException

OrmLearningPlatformApplication.java

Слои:
Entity — JPA-модель, связи, индексы, ограничения.
Repository — JpaRepository + кастомные методы (findByEmail, existsBy..., @EntityGraph).
Service — бизнес-логика, валидация, транзакции.
Web — REST-контроллеры, DTO, @Valid, обработка ошибок через @RestControllerAdvice.
Exception — кастомные исключения + GlobalExceptionHandler с кодами 400/404/409/500.

Валидация и обработка ошибок
Bean Validation на всех DTO: @NotBlank, @Email, @Size, @Min, @Max, @Positive, @NotNull.
@RestControllerAdvice (GlobalExceptionHandler) возвращает JSON:
{
"timestamp": "2026-11-09T20:00:00",
"status": 404,
"error": "Not Found",
"message": "Course with id=999 not found",
"details": null
}
Коды: 400 — валидация/бизнес-ошибка, 404 — не найдено, 409 — дубликат, 500 — прочее.

Тесты
9 интеграционных тестов на Testcontainers (PostgreSQL 16):
UserRepositoryIT — CRUD пользователя (3 теста)
CourseRepositoryIT — Сохранение курса с категорией и преподавателем
EnrollmentServiceIT — Запись на курс + запрет дубликата + отписка (2 теста)
SubmissionServiceIT — Отправка решения + оценка + запрет повторной отправки
QuizServiceIT — Прохождение теста, подсчёт баллов, pass/fail
CourseReviewServiceIT — Отзыв + запрет дубликата + средний рейтинг

Запуск: mvn clean test
Требует Docker. Testcontainers поднимает PostgreSQL один раз на весь прогон.

CI
GitHub Actions: .github/workflows/ci.yml
При каждом push и pull_request в main: checkout, JDK 17 (temurin), Maven cache, mvn -B clean compile, mvn -B test (Testcontainers PostgreSQL), загрузка Surefire-отчётов как артефакта.
Статус: https://github.com/TimurNurzhau/orm-learning-platform/actions/workflows/ci.yml/badge.svg

Демо-данные
src/main/resources/data.sql — идемпотентный (ON CONFLICT DO NOTHING), наполняет БД при каждом старте:
4 пользователя (teacher@example.com, alice@example.com, bob@example.com, admin@example.com)
2 курса, 2 модуля, 3 урока
2 задания, 1 тест с 2 вопросами и вариантами
2 записи на курс, 3 тега, 2 уведомления

Переменные окружения (для Docker)
SPRING_DATASOURCE_URL — JDBC URL PostgreSQL — по умолчанию jdbc:postgresql://localhost:5432/learning_platform
SPRING_DATASOURCE_USERNAME — Логин — postgres
SPRING_DATASOURCE_PASSWORD — Пароль — postgres
SPRING_JPA_HIBERNATE_DDL_AUTO — Стратегия схемы — update
SPRING_DOCKER_COMPOSE_ENABLED — Авто-запуск compose — false (в контейнере)

Лицензия
Учебный проект